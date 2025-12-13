# 📱 TA Mobile Server (Spring Boot)

T-Aging 서비스의 모바일 앱 및 중앙 서버(Central Server) 역할을 담당하는 Spring Boot 기반 백엔드입니다.
키오스크 서버에서 발생한 주문 데이터는 RabbitMQ 기반 비동기 동기화를 통해 수신되며,
모바일 앱은 중앙 DB에 적재된 주문 이력과 회원 정보를 조회합니다.

---
## ✨ 핵심 기능
### 👤 사용자 / 인증

- 카카오 소셜 로그인(Kakao OAuth2)
- 전화번호 기반 사용자 식별 및 회원 관리
- JWT 기반 인증/인가
### 🧾 주문 이력 & 전자 영수증

- 키오스크 주문 이력 조회
- 주문 상세 및 전자 영수증 제공
### 📤 푸시 알림

- 주문 완료, 키오스크 로그인 확인 등 알림

### 📨 키오스크 서버 연동

- RabbitMQ 기반 주문 데이터 수신
- 키오스크 서버와 REST 통신
  - 전화번호/QR 로그인 연동

> 커스텀 메뉴 데이터의 조회 및 키오스크 연동은 추후 확장 기능으로 설계되어 있습니다.
---
## 🧱 시스템 아키텍처 개요

본 서버는 T-Aging 서비스의 Central Server로서 다음 역할을 수행합니다.

- 모바일 앱 클라이언트에 사용자/주문 관련 API 제공
- 키오스크 서버에서 전달되는 주문 데이터를 수신 및 중앙 DB에 저장
- 키오스크–모바일 간 인증 및 데이터 연동 역할
---
## 🔄 키오스크 서버 연동 흐름

1) **인증 연동 (전화번호/QR)** 
   1. 키오스크 서버 → 모바일 서버 REST 요청
   2. 모바일 서버에서 사용자 정보 조회
   3. 사용자 인증 결과 반환

2) **주문 데이터 동기화**
   1. 키오스크 서버에서 주문 확정
   2. 주문 이벤트를 RabbitMQ로 발행
   3. 모바일 서버가 MQ 메시지를 소비(Consumer)
   4. 중앙 DB에 주문 데이터 적재
---
## 🔌 Interfaces
### 🌐 REST API
### Swagger URL: `http://localhost:8080/swagger-ui.html`
✅ **모바일 앱 전용 API**

- POST `/t-age/auth/login`: 카카오 로그인 및 토큰 발급
- GET `/t-age/users/{id}`: 사용자 정보 조회
- GET `/t-age/users/{id}/qr`: 키오스크 로그인용 QR 생성
- GET `/t-age/users/{id}/custom-menus`: 커스텀 메뉴 목록 조회(확장)
- GET `/t-age/users/{id}/orders`: 주문 내역 및 영수증 조회

✅ **키오스크 서버 연동 API**

- POST `/inter/ta-kiosk/auth/phone-num`:키오스크에서 전화번호 로그인 요청
- POST `/inter/ta-kiosk/auth/qr`:키오스크에서 QR 로그인 요청

### 📨 RabbitMQ
- 주문 동기화 메시지 수신 (Consumer)
  - `EXCHANGE_KIOSK = "kiosk.exchange"`
  - `QUEUE_ORDER_SYNC = "order.sync.queue"`
  - `ROUTING_KEY_ORDER_SYNC = "order.sync"`
---
## 📁 디렉토리 구조
```text
tam/
├── common              # 공통 엔티티 및 유틸
├── config              # Security, JWT, MQ, Web 설정
├── dto                 # Request / Response DTO
├── exception           # Global Exception Handling
├── modules
│   ├── auth            # 인증 (Kakao OAuth2, JWT)
│   ├── user            # 사용자 정보 관리
│   ├── custom          # 커스텀 메뉴 관리(확장)
│   ├── menu            # 메뉴 관리
│   ├── order           # 주문/영수증
│   ├── push            # 푸시 알림
│   ├── store           # 매장 관리
│   ├── login           # 로그인 로직
│   ├── kiosklogin      # 키오스크 연동 로그인 로직
│   └── sync            # MQ 주문 동기화 Consumer
└── security            # 인증 필터 및 보안 유틸

```
---
## 🚀 로컬 실행
### ✅ 사전 요구사항
- Java 17+
- MySQL
- RabbitMQ
### 🔐 환경 변수
```text
MOBILE_ENDPOINT_LOCAL= YOUR_MOBILE_DB_URL_LOCAL
MOBILE_DB_USER_LOCAL= YOUR_KIOSK_DB_MASTER_ID_LOCAL
MOBILE_DB_PASS_LOCAL= YOUR_KIOSK_DB_PASSWORD_LOCAL

MOBILE_ENDPOINT_PROD= YOUR_MOBILE_DB_URL_PROD
MOBILE_DB_USER_PROD= YOUR_MOBILE_DB_MASTER_ID_PROD
MOBILE_DB_PASS_PROD= YOUR_MOBILE_DB_PASSWORD_PROD

JWT_SECRET= YOUR_JWT_KEY

KAKAO_CLIENT_ID= YOUR_KAKAO_APP_KEY
KAKAO_REDIRECT_URL= YOUR_KAKAO_REDIRECT_URL

SPRING_RABBITMQ_HOST=localhost

RABBITMQ_USER= YOUR_RABBITMQ_ID
RABBITMQ_PASS= YOUR_RABBITMA_PASSWORD
```

### ▶️ Run
```text
./gradlew bootRun
```
---
## 👥 팀원 역할
| 이름      | 역할              | 담당 파트                                                                     |
|---------| --------------- |---------------------------------------------------------------------------|
| **이소현** | Backend         | 모바일 앱 서버 API 개발, 소셜 로그인(Kakao OAuth), 사용자 인증 로직, 캐릭터 디자인                  |
| **정교원** | Backend / Infra | 키오스크 서버 ↔ 모바일 서버 연동, RabbitMQ 주문 DB 동기화, RestTemplate 기반 인증 통신, 데이터 구조 설계 |
---
## 📝 참고
- 본 서버는 키오스크 서버 및 AI Agent와 분리된 **중앙 서버**입니다.
- 프로젝트 전체 소개 및 서비스 아키텍처는 Organization README에서 다룹니다.