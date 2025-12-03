
# 👵 T-Aging (TAM) - AI Agent 키오스크 & 모바일 서비스

> **디지털 취약 계층을 위한 AI 음성 안내 키오스크 및 연동 모바일 서비스**

\*\*T-Aging (TAM)\*\*은 시니어와 디지털 취약 계층이 키오스크 사용 시 겪는 어려움을 해결하기 위해 기획되었습니다. 복잡한 UI 터치 대신 **AI 에이전트와의 대화**를 통해 주문을 진행하며, 모바일 앱과 연동하여 **개인화된 메뉴(커스텀 메뉴)** 주문과 **전자 영수증** 발급을 지원하여 탄소 중립을 실현합니다.

-----

## 📅 프로젝트 배경 및 목적

  - **디지털 소외 해소:** 고령층의 키오스크 조작 미숙으로 인한 심리적 위축감 해소 및 접근성 향상.
  - **오주문 감소:** 음성 대화 및 재확인 과정을 통해 주문 실수 방지 및 자원 낭비 최소화.
  - **환경 보호:** 종이 영수증 대신 모바일 전자 영수증 자동 발급.

## 🛠 Tech Stack

### Backend

| 기술 | 버전/설명 |
| --- | --- |
| **Java** | JDK 17 |
| **Framework** | Spring Boot 3.4.0 |
| **Database** | MySQL 8.0 (JPA/Hibernate) |
| **Security** | Spring Security, JWT (AccessToken/RefreshToken) |
| **Docs** | Swagger (SpringDoc OpenAPI 3.0) |
| **Build Tool** | Gradle 9.2.1 |

### Key Libraries

  - **Lombok**: 보일러플레이트 코드 제거
  - **ZXing**: QR 코드 생성 및 처리
  - **Spring WebFlux**: 외부 API (카카오, AI 서버 등) 통신

-----

## 🌟 주요 기능 (Key Features)

### 1\. 📱 모바일 앱 (App)

  - **간편 로그인:** 카카오 소셜 로그인을 지원하며, 최초 로그인 시 전화번호 기반 회원가입이 진행됩니다.
  - **커스텀 메뉴 관리:** 복잡한 옵션(샷 추가, 연하게 등)을 미리 설정하여 '나만의 메뉴'로 저장합니다.
  - **회원 QR 코드:** 키오스크 로그인을 위한 전용 QR 코드를 생성합니다.
  - **주문 내역 & 전자 영수증:** 과거 주문 내역을 조회하고 모바일 영수증을 확인합니다.
  - **푸시 알림:** 주문 완료, 키오스크 로그인 확인 등을 알림으로 받습니다.

### 2\. 🖥️ 키오스크 (Kiosk)

  - **QR 로그인:** 앱의 QR 코드를 스캔하여 즉시 회원 정보를 불러옵니다.
  - **AI 음성 주문:** (Python 서버 연동) 자연어 처리를 통해 "카페인 없는 거 추천해줘"와 같은 주문이 가능합니다.
  - **커스텀 주문 연동:** 앱에서 등록한 커스텀 메뉴를 키오스크에서 바로 불러와 주문할 수 있습니다.

-----

## 📂 프로젝트 구조 (Project Structure)

```
src/main/java/com/example/tam
├── common
│   └── entity          # JPA Entity (User, Menu, Order 등)
├── config              # Security, Swagger, Web 설정
├── dto                 # Data Transfer Objects (Request/Response)
├── exception           # Global Exception Handling
├── modules             # 도메인별 기능 모듈
│   ├── auth            # 인증 (Kakao, JWT)
│   ├── custom          # 커스텀 메뉴 관리
│   ├── kiosk           # 키오스크 전용 API
│   ├── login           # 로그인 로직
│   ├── menu            # 메뉴 관리
│   ├── order           # 주문 및 영수증
│   ├── push            # 푸시 알림
│   ├── store           # 매장 관리
│   └── user            # 사용자 정보 관리
└── security            # JWT 필터 및 유틸리티
```

-----

## 📝 API 명세 (API Documentation)

서버 실행 후 아래 주소에서 Swagger UI를 통해 API 문서를 확인할 수 있습니다.

  - **Swagger URL:** `http://localhost:8080/swagger-ui.html`

### 주요 엔드포인트 요약

| 구분 | Method | URI | 설명 |
| --- | --- | --- | --- |
| **Auth** | POST | `/t-age/auth/login` | 카카오 로그인 및 토큰 발급 |
| **User** | GET | `/t-age/users/{id}` | 사용자 정보 조회 |
| **User** | GET | `/t-age/users/{id}/qr` | 키오스크 로그인용 QR 생성 |
| **Custom** | GET | `/t-age/users/{id}/custom-menus` | 커스텀 메뉴 목록 조회 |
| **Order** | GET | `/t-age/users/{id}/orders` | 주문 내역 및 영수증 조회 |
| **Kiosk** | POST | `/t-age/kiosk/auth/qr` | 키오스크에서 QR 로그인 요청 |

-----

## 🚀 시작하기 (Getting Started)

### 사전 요구사항 (Prerequisites)

  - Java 17 이상
  - MySQL Server (Port 3306)

### 설치 및 실행 (Installation & Run)

1.  **레포지토리 클론**

    ```bash
    git clone https://github.com/your-repo/ta-mobile.git
    cd ta-mobile/tam
    ```

2.  **데이터베이스 설정**
    `src/main/resources/application.properties` 파일에서 MySQL 설정 수정

    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/tam_db?useSSL=false
    spring.datasource.username=YOUR_USERNAME
    spring.datasource.password=YOUR_PASSWORD
    ```

3.  **환경 변수 설정 (Optional)**
    카카오 로그인 및 JWT 시크릿 키 설정

    ```properties
    jwt.secret=your_secure_jwt_secret_key
    KAKAO_CLIENT_ID=your_kakao_rest_api_key
    ```

4.  **빌드 및 실행**

    ```bash
    # Linux/Mac
    ./gradlew bootRun

    # Windows
    gradlew.bat bootRun
    ```

-----

## 👥 팀원 (Developers)

| 이름 | 역할 | 담당 파트 |
| --- | --- | --- |
| **희선** | Frontend | React, TypeScript, 모바일 앱 UI/UX |
| **소현** | Backend (Node) | 메인 서버, Redis, DB 설계 |
| **교원** | Backend (Spring) | 키오스크, AI 서버 연동, API 개발 |

-----

Copyright © 2024 T-Aging Team. All rights reserved.
