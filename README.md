# 🏃‍♂️ RunningGoalTracker

[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.21-blue.svg?logo=kotlin)](https://kotlinlang.org)
[![KSP](https://img.shields.io/badge/KSP-2.2.21--2.0.4-blue.svg)](https://github.com/google/ksp)
[![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg)]()
[![Platform](https://img.shields.io/badge/platform-Android-green.svg)]()
[![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg?logo=android)]()
[![Architecture](https://img.shields.io/badge/Architecture-Clean%20%7C%20MVVM%20%7C%20Multi_Module-orange)]()
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack_Compose-blue?logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Hilt](https://img.shields.io/badge/DI-Hilt-blue?logo=dagger)](https://dagger.dev/hilt/)

**RunningGoalTracker**는 사용자의 러닝 목표를 설정하고 관리하며, 안드로이드의 활동 인식 기능을 통해 러닝 상태를 실시간으로 모니터링하는 앱입니다. MVVM을 적용한 Clean Architecture를 준수하였습니다.

---

## 📝 프로젝트 개요

- **앱 이름**: `RunningGoalTracker`
- **목적**: 사용자가 꾸준히 러닝 습관을 형성하고 목표를 달성할 수 있도록 돕는 동기부여 앱입니다. 실시간 활동 인식을 통해 정확한 운동 상태를 추적하고, 개인화된 목표 설정과 스마트 리마인더 기능이 있습니다.
- **개발 기간**: 2025.12.-2026.01.
- **개발 인원**: 1인 개발

---

## 🛠️ 기술 스택 (Tech Stack)

- **Language**: [Kotlin](https://kotlinlang.org/) (JVM 21)
- **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (BOM 기반 최신 버전)
- **Dependency Injection**: [Hilt (Dagger Hilt)](https://dagger.dev/hilt/)
- **Database**: [Room](https://developer.android.com/training/data-storage/room)
- **Architecture**: MVVM, Clean Architecture, Multi Module, Hybrid (Layered + Feature-based) Architecture
- **Asynchronous**: [Coroutines & Flow](https://kotlinlang.org/docs/coroutines-guide.html)
- **Libraries**:
  - [Play Services Location & Activity Recognition](https://developer.android.com/training/location)
  - [Navigation Compose](https://developer.android.com/jetpack/compose/navigation)

---

## ✨ 주요 기능 (Core Features)

- **🤖 AI 코칭**: Pose Detection을 활용하여 런지, 스쿼트 등 운동 횟수를 측정합니다.
- **🏃 활동 인식 모니터링**: 사용자의 현재 상태(걷기, 달리기, 정지 등)를 실시간으로 추적합니다.
- **📊 러닝 기록 관리**: 운동 시간, 거리, 속도 등을 저장하고 시각화된 통계를 제공합니다.
- **🎯 목표 설정**: 주간 단위 목표를 설정하고 달성률을 확인할 수 있습니다.
- **🔔 스마트 리마인더**: 설정한 시간에 맞춰 운동 알림을 제공합니다.

---

## 📱 기능 시연 (Key Features)

### 🤖 AI 코칭 (Pose Detection)
| 전신 화면 요청 | 런지 측정 | 스쿼트 측정 |
| :---: | :---: | :---: |
| <img src="gifs/전신%20화면%20요청.gif" width="32%"/> | <img src="gifs/런지.gif" width="32%"/> | <img src="gifs/스쿼트.gif" width="32%"/> |

### 🏃 러닝 및 활동 기록
#### [활동 시작 및 목표 설정]
| 러닝 시작 및 백그라운드 | 홈 화면 활동 기록 | 주간 목표 설정 |
| :---: | :---: | :---: |
| <img src="gifs/러닝%20시작%20버튼%20및%20백그라운드%20동작.gif" width="32%"/> | <img src="gifs/홈%20화면%20활동%20기록.gif" width="32%"/> | <img src="gifs/주간%20목표%20설정.gif" width="32%"/> |

#### [데이터 분석 및 통계]
| 일간/주간/월간 통계 | 통계 달력 확인 |
| :---: | :---: |
| <img src="gifs/일간%20주간%20월간%20통계.gif" width="32%"/> | <img src="gifs/통계%20달력%20확인.gif" width="32%"/> |

### 🔑 인증 및 초기 설정
#### [권한 및 정책]
| 개인정보처리방침 | 네트워크 권한 요청 | 권한 허용 |
| :---: | :---: | :---: |
| <img src="gifs/개인정보처리방침.gif" width="32%"/> | <img src="gifs/네트워크%20권한%20요청.gif" width="32%"/> | <img src="gifs/권한%20허용.gif" width="32%"/> |

#### [계정 생성 및 검사]
| 닉네임 유효성 검사 | 중복 닉네임 처리 | 회원가입 완료 |
| :---: | :---: | :---: |
| <img src="gifs/닉네임%20유효성%20검사.gif" width="32%"/> | <img src="gifs/중복%20닉네임%20처리.gif" width="32%"/> | <img src="gifs/회원가입%20완료.gif" width="32%"/> |

### ⚙️ 설정 및 기타
#### [알림 관리]
| 알림 설정 | 알림 확인 |
| :---: | :---: |
| <img src="gifs/알림%20설정.gif" width="32%"/> | <img src="gifs/알림%20확인.gif" width="32%"/> |

#### [앱 설정 및 계정]
| 다크모드 | 마이페이지 회원 탈퇴 |
| :---: | :---: |
| <img src="gifs/다크모드.gif" width="32%"/> | <img src="gifs/마이페이지%20회원%20탈퇴.gif" width="32%"/> |

---

## 프로젝트 구조 (Module Architecture)

```
:app
 ├── :feature
 │   ├── :home
 │   ├── :goal
 │   ├── :record
 │   ├── :reminder
 │   ├── :aicoach
 │   ├── :auth
 │   └── :mypage
 ├── :data
 ├── :domain
 └── :shared
     ├── :designsystem
     ├── :navigation
     └── :network
```

- **`:domain`**: 순수 Kotlin 모듈로, 앱의 핵심 비즈니스 로직(UseCase, Entity)을 포함합니다. 다른 모듈에 대한 의존성이 없습니다.
- **`:data`**: 데이터 소스(Local-Room)를 관리하고, `domain` 모듈의 Repository 인터페이스를 구현합니다.
- **`:feature`**: 각 화면(Home, Goal, Record, Reminder)에 해당하는 기능 단위 모듈입니다. `domain`과 `shared:designsystem` 모듈에 의존합니다.
- **`:shared:designsystem`**: 공통으로 사용되는 UI 컴포넌트, 테마, 색상, 폰트 등을 정의하는 모듈입니다.
- **`:app`**: 최종 애플리케이션 모듈로, 위 모듈들을 통합하여 완전한 앱을 구성합니다.

### 의존성 구조 다이어그램 (Dependency Graph)
```mermaid
%%{init: {
  "theme": "base",
  "themeVariables": {
    "background": "#ffffff",
    "primaryColor": "#f2f2f2",
    "primaryTextColor": "#111111",
    "primaryBorderColor": "#444444",
    "lineColor": "#444444",
    "fontSize": "14px"
  }
}}%%

flowchart TD
  subgraph App_Layer["App Layer"]
    APP[":app"]
  end

  subgraph Feature_Layer["Feature Layer"]
    direction TB
    AUTH[":feature:auth"]
    HOME[":feature:home"]
    GOAL[":feature:goal"]
    RECORD[":feature:record"]
    REMINDER[":feature:reminder"]
    AICOACH[":feature:aicoach"]
    MYPAGE[":feature:mypage"]
    
    %% Feature 노드들 가로 정렬 강제
    AUTH ~~~ HOME ~~~ GOAL ~~~ RECORD ~~~ REMINDER ~~~ AICOACH ~~~ MYPAGE
  end

  subgraph Shared_Layer["Shared Layer"]
    direction LR
    DS[":shared:designsystem"]
    NAV[":shared:navigation"]
    NETWORK[":shared:network"]
    
    %% Shared 노드들 가로 정렬 강제
    DS ~~~ NAV ~~~ NETWORK
  end

  subgraph Core_Layer["Core Layer"]
    direction LR
    DATA[":data"]
    DOMAIN[":domain"]
    DATA --> DOMAIN
  end

  %% App Connections
  APP --> AUTH & HOME & GOAL & RECORD & REMINDER & AICOACH & MYPAGE
  
  %% 가독성을 위해 App에서 하단 레이어로 바로 가는 선은 점선(dotted) 처리
  APP -.-> DATA
  APP -.-> DS
  APP -.-> NAV

  %% Feature Connections (묶음 처리로 선 꼬임 최소화)
  HOME & GOAL & RECORD & REMINDER & AICOACH & AUTH & MYPAGE --> DOMAIN
  HOME & GOAL & RECORD & REMINDER & AICOACH & AUTH & MYPAGE --> DS
  HOME & GOAL & RECORD & REMINDER & AICOACH & AUTH & MYPAGE --> NAV
  
  AUTH --> NETWORK

  %% Styling
  classDef app fill:#e0e0e0,stroke:#2f2f2f,stroke-width:2px,color:#111;
  classDef feature fill:#f0f0f0,stroke:#3a3a3a,stroke-width:1.5px,color:#111;
  classDef core fill:#fafafa,stroke:#3a3a3a,stroke-width:1.5px,color:#111;
  classDef shared fill:#ededed,stroke:#3a3a3a,stroke-width:1.5px,color:#111;

  class APP app;
  class HOME,GOAL,RECORD,REMINDER,AICOACH,AUTH,MYPAGE feature;
  class DOMAIN,DATA core;
  class DS,NAV,NETWORK shared;
```


---

## 🚀 설치 방법 (Installation)

1. **Repository 복제**:
   ```bash
   git clone https://github.com/your-username/RunningGoalTracker.git
   ```
2. **Android Studio에서 열기** 및 **Gradle 동기화**

---

## 🔒 필수 권한 설정

1. **활동 인식 (Activity Recognition)**: 실시간 활동 추적
2. **위치 정보 (Location)**: 경로 및 거리 측정
3. **알림 (Notification)**: 운동 독려 및 서비스 상태 유지
