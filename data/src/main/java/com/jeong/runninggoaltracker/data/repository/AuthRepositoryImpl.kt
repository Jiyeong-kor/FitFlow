package com.jeong.runninggoaltracker.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.jeong.runninggoaltracker.data.contract.FirestorePaths
import com.jeong.runninggoaltracker.data.contract.UserFirestoreFields
import com.jeong.runninggoaltracker.data.contract.UsernameFirestoreFields
import com.jeong.runninggoaltracker.data.util.awaitResult
import com.jeong.runninggoaltracker.data.util.UserProfileDocumentPayload
import com.jeong.runninggoaltracker.data.util.toAuthError
import com.jeong.runninggoaltracker.data.util.toFirestoreMap
import com.jeong.runninggoaltracker.data.util.UsernameReservationPolicy
import com.jeong.runninggoaltracker.domain.model.AuthError
import com.jeong.runninggoaltracker.domain.model.AuthProvider
import com.jeong.runninggoaltracker.domain.model.AuthResult
import com.jeong.runninggoaltracker.domain.repository.AuthRepository
import com.jeong.runninggoaltracker.domain.util.NicknameNormalizer
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import com.jeong.runninggoaltracker.domain.di.IoDispatcher
import javax.inject.Inject
import kotlin.coroutines.resume

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val runningDatabase: com.jeong.runninggoaltracker.data.local.RunningDatabase,
    @IoDispatcher private val ioDispatcher: kotlinx.coroutines.CoroutineDispatcher
) : AuthRepository {
    override suspend fun signInAnonymously(): Result<Unit> =
        suspendCancellableCoroutine { continuation ->
            firebaseAuth.signInAnonymously()
                .addOnSuccessListener {
                    continuation.resume(Result.success(Unit))
                }
                .addOnFailureListener { error ->
                    continuation.resume(Result.failure(error))
                }
        }

    override suspend fun reserveNicknameAndCreateUserProfile(
        nickname: String,
        authProvider: AuthProvider
    ): AuthResult<Unit> {
        val user = firebaseAuth.currentUser ?: return AuthResult.Failure(AuthError.PermissionDenied)
        val uid = user.uid
        val normalizedNickname = NicknameNormalizer.normalize(nickname)
        return try {
            val userDocRef = firestore.collection(FirestorePaths.COLLECTION_USERS).document(uid)
            val usernameDocRef =
                firestore.collection(FirestorePaths.COLLECTION_USERNAMES)
                    .document(normalizedNickname)
            firestore.runTransaction { transaction ->
                val usernameSnapshot = transaction.get(usernameDocRef)
                val usernameOwner = usernameSnapshot.getString(UsernameFirestoreFields.UID)
                val canReserve = UsernameReservationPolicy.shouldAllowReservation(
                    isExisting = usernameSnapshot.exists(),
                    ownerUid = usernameOwner,
                    currentUid = uid
                )
                if (!canReserve) {
                    throw NicknameTakenException()
                }
                val now = Timestamp.now()
                val usernameData = mapOf(
                    UsernameFirestoreFields.UID to uid,
                    UsernameFirestoreFields.CREATED_AT to now,
                    UsernameFirestoreFields.LAST_ACTIVE_AT to now,
                    UsernameFirestoreFields.IS_ANONYMOUS to user.isAnonymous
                )
                val userData = UserProfileDocumentPayload(
                    nickname = nickname,
                    createdAt = now,
                    lastActiveAt = now,
                    authProvider = authProvider
                ).toFirestoreMap()
                transaction.set(usernameDocRef, usernameData)
                transaction.set(userDocRef, userData, SetOptions.merge())
            }.awaitResult()
            val profileUpdate = UserProfileChangeRequest.Builder()
                .setDisplayName(nickname)
                .build()
            user.updateProfile(profileUpdate).awaitResult()
            AuthResult.Success(Unit)
        } catch (error: Exception) {
            AuthResult.Failure(error.toAuthErrorWithNickname())
        }
    }

    override suspend fun checkNicknameAvailability(nickname: String): AuthResult<Boolean> {
        val normalizedNickname = NicknameNormalizer.normalize(nickname)
        val user = firebaseAuth.currentUser ?: return AuthResult.Failure(AuthError.PermissionDenied)
        return try {
            val snapshot = firestore.collection(FirestorePaths.COLLECTION_USERNAMES)
                .document(normalizedNickname)
                .get()
                .awaitResult()
            val ownerUid = snapshot.getString(UsernameFirestoreFields.UID)
            AuthResult.Success(!snapshot.exists() || ownerUid == user.uid)
        } catch (error: Exception) {
            AuthResult.Failure(error.toAuthErrorWithNickname())
        }
    }

    override suspend fun deleteAccountAndReleaseNickname(): AuthResult<Unit> {
        val user = firebaseAuth.currentUser ?: return AuthResult.Failure(AuthError.PermissionDenied)
        val uid = user.uid
        return try {
            val userDocRef = firestore.collection(FirestorePaths.COLLECTION_USERS).document(uid)
            val userSnapshot = userDocRef.get().awaitResult()
            val nickname =
                userSnapshot.getString(UserFirestoreFields.NICKNAME)
                    ?: user.displayName
            val normalizedNickname = nickname?.let { NicknameNormalizer.normalize(it) }
            deleteUserSubcollections(userDocRef)
            val usernameDocRef = normalizedNickname?.let { normalized ->
                firestore.collection(FirestorePaths.COLLECTION_USERNAMES).document(normalized)
            }
            val usernameSnapshot = usernameDocRef?.get()?.awaitResult()
            val usernameOwner = usernameSnapshot?.getString(UsernameFirestoreFields.UID)
            if (userSnapshot.exists()) {
                userDocRef.delete().awaitResult()
            }
            if (usernameDocRef != null && usernameSnapshot?.exists() == true && usernameOwner == uid) {
                usernameDocRef.delete().awaitResult()
            }
            withContext(ioDispatcher) {
                runningDatabase.clearAllTables()
            }
            user.delete().awaitResult()
            AuthResult.Success(Unit)
        } catch (error: Exception) {
            AuthResult.Failure(error.toAuthErrorWithNickname())
        }
    }

    private suspend fun deleteUserSubcollections(userDocRef: com.google.firebase.firestore.DocumentReference) {
        deleteCollectionDocuments(userDocRef.collection(FirestorePaths.COLLECTION_RUNNING_RECORDS))
        deleteCollectionDocuments(userDocRef.collection(FirestorePaths.COLLECTION_RUNNING_GOALS))
        deleteCollectionDocuments(userDocRef.collection(FirestorePaths.COLLECTION_RUNNING_REMINDERS))
        deleteCollectionDocuments(userDocRef.collection(FirestorePaths.COLLECTION_WORKOUT_RECORDS))
    }

    private suspend fun deleteCollectionDocuments(
        collection: com.google.firebase.firestore.CollectionReference
    ) {
        val snapshots = collection.get().awaitResult()
        val documents = snapshots.documents
        if (documents.isEmpty()) return
        val chunkSize = 450
        documents.chunked(chunkSize).forEach { chunk ->
            val batch = firestore.batch()
            chunk.forEach { doc -> batch.delete(doc.reference) }
            commitBatchWithRetry(batch)
        }
    }

    private suspend fun commitBatchWithRetry(batch: com.google.firebase.firestore.WriteBatch) {
        var attempt = 0
        var lastError: Exception? = null
        while (attempt < 3) {
            try {
                batch.commit().awaitResult()
                return
            } catch (error: Exception) {
                lastError = error
                attempt += 1
            }
        }
        throw lastError ?: IllegalStateException()
    }

    override fun isSignedIn(): Boolean = firebaseAuth.currentUser != null

    override fun observeIsAnonymous(): Flow<Boolean> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.isAnonymous == true)
        }
        firebaseAuth.addAuthStateListener(listener)
        trySend(firebaseAuth.currentUser?.isAnonymous == true)
        awaitClose {
            firebaseAuth.removeAuthStateListener(listener)
        }
    }.distinctUntilChanged()

    override fun observeUserNickname(): Flow<String?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.displayName)
        }
        firebaseAuth.addAuthStateListener(listener)
        trySend(firebaseAuth.currentUser?.displayName)
        awaitClose {
            firebaseAuth.removeAuthStateListener(listener)
        }
    }.distinctUntilChanged()

    private fun Exception.toAuthErrorWithNickname(): AuthError =
        if (this is NicknameTakenException) {
            AuthError.NicknameTaken
        } else {
            toAuthError()
        }

    private class NicknameTakenException : IllegalStateException()

}
