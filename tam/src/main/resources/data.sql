-- =========================================
-- 중앙 DB(tam_central_db) 초기 데이터 세팅
-- =========================================

-- 자식 테이블부터 삭제 (FK / 의존 관계 고려)
DELETE FROM order_option;
DELETE FROM order_detail;
DELETE FROM order_header;

DELETE FROM custom_detail;
DELETE FROM custom_header;

DELETE FROM push_history;
DELETE FROM kakao;
DELETE FROM user;

DELETE FROM option_value;
DELETE FROM option_group;

DELETE FROM menu_option;
DELETE FROM menu;
DELETE FROM store;

-- =========================
-- 1. 매장 정보
-- =========================
INSERT INTO store (store_id, store_name, store_address)
VALUES
  (1, '타 카페 1호점', '서울 어딘가 1층');

-- =========================
-- 2. 메뉴 정보
--    (엔티티: Menu)
--    menu_id, menu_name, menu_price, description,
--    calorie, sugar, caffeine, allergic, menu_image
-- =========================
INSERT INTO menu (menu_id, menu_name, menu_price, description, calorie, sugar, caffeine, allergic, menu_image)
VALUES
  -- 아메리카노 계열
  (1,  '아메리카노',           2000, '기본 에스프레소 커피',                     5,   0,   120, 'NONE', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/coffee/hot-americano.jpg'),
  (3,  '연유 아메리카노',      2500, '연유가 들어가 달달한 아메리카노',          80,  15,  120, 'MILK', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/coffee/hot-americano.jpg'),
  (4,  '디카페인 아메리카노',  2500, '카페인을 줄인 아메리카노',                 5,   0,    5, 'NONE', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/coffee/hot-americano.jpg'),

  (5,  '콜드브루',             3000, '진하게 우려낸 콜드브루 커피',              10,  0,   150, 'NONE', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/coffee/ice-americano.jpg'),
  (6,  '디카페인 콜드브루',    3500, '카페인을 줄인 콜드브루',                   10,  0,    5, 'NONE', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/coffee/ice-americano.jpg'),

  -- 라떼 계열
  (10, '카페라떼',             2800, '우유가 들어간 부드러운 라떼',              180, 10,  120, 'MILK', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/coffee/hot-latte.jpg'),
  (11, '디카페인 라떼',        3300, '카페인을 줄인 부드러운 라떼',              180, 10,    5, 'MILK', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/coffee/hot-latte.jpg'),
  (12, '바닐라라떼',           3200, '바닐라 시럽이 들어간 달콤한 라떼',         220, 25,  120, 'MILK', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/coffee/hot-latte.jpg'),
  (13, '카라멜라떼',           3300, '카라멜 시럽이 들어간 달콤한 라떼',         230, 26,  120, 'MILK', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/coffee/hot-latte.jpg'),
  (14, '헤이즐넛라떼',         3200, '헤이즐넛 향이 나는 고소한 라떼',           220, 20,  120, 'MILK', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/coffee/hot-latte.jpg'),
  (15, '카페모카',             3200, '초콜릿이 들어간 달콤한 모카 라떼',         250, 28,  120, 'MILK', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/coffee/hot-latte.jpg'),

  -- 논커피 라떼 / 기타 라떼
  (20, '초코라떼',             3000, '달콤한 초콜릿이 들어간 논커피 라떼',       260, 30,   10, 'MILK', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/non-coffee/choco.jpg'),
  (21, '화이트초코라떼',       3200, '화이트 초콜릿이 들어간 부드러운 라떼',     270, 32,   10, 'MILK', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/coffee/ice-latte.jpg'),
  (22, '녹차라떼',             3200, '녹차와 우유가 어우러진 라떼',              240, 20,   30, 'MILK', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/non-coffee/green.jpg'),
  (23, '고구마라떼',           3500, '고구마가 들어간 포만감 있는 라떼',         280, 35,   10, 'MILK', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/coffee/hot-latte.jpg'),
  (24, '밀크티라떼',           3300, '홍차와 우유가 들어간 밀크티 라떼',          230, 25,   40, 'MILK', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/coffee/ice-latte.jpg'),

  -- 에이드
  (30, '레몬에이드',           3000, '상큼한 레몬이 들어간 에이드',              140, 30,    0, 'NONE', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/fruit/lemon.jpg'),
  (31, '자몽에이드',           3500, '자몽이 들어간 상큼한 에이드',              150, 32,    0, 'NONE', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/fruit/grapefruit.jpg'),
  (32, '청포도에이드',         3300, '청포도가 들어간 달콤한 에이드',            150, 32,    0, 'NONE', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/fruit/grape.jpg'),
  (33, '오렌지에이드',         3300, '오렌지가 들어간 상큼한 에이드',            150, 32,    0, 'NONE', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/fruit/orange-ade.jpg'),

  -- 티
  (40, '얼그레이티',           2500, '향긋한 얼그레이 홍차',                     5,   0,   30, 'NONE', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/tea/tea.jpg'),
  (41, '캐모마일티',           2500, '은은한 꽃향의 캐모마일 티',                5,   0,    0, 'NONE', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/tea/tea.jpg'),
  (42, '페퍼민트티',           2500, '상쾌한 페퍼민트 허브티',                   5,   0,    0, 'NONE', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/tea/tea.jpg'),
  (43, '복숭아 아이스티',      2500, '달달한 복숭아 향의 아이스티',              120, 28,   0, 'NONE', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/tea/tea.jpg'),

  -- 스무디
  (50, '딸기 스무디',          3800, '딸기가 들어간 달콤한 스무디',              250, 35,    0, 'MILK', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/fruit/berry.jpg'),
  (51, '망고 스무디',          3800, '망고가 들어간 달콤한 스무디',              250, 35,    0, 'MILK', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/fruit/mango.jpg'),
  (52, '요거트 스무디',        3800, '상큼한 요거트 베이스 스무디',              230, 30,    0, 'MILK', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/fruit/yogurt.jpg'),

  -- 프라페
  (60, '모카프라페',           4200, '모카 베이스의 달콤한 프라페',              300, 40,   80, 'MILK', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/coffee/coffee.jpg'),
  (61, '카라멜프라페',         4300, '카라멜 소스가 듬뿍 들어간 프라페',         310, 42,   80, 'MILK', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/non-coffee/caramel.jpg'),
  (62, '쿠키앤크림프라페',     4500, '쿠키와 크림이 들어간 프라페',              320, 45,   80, 'MILK', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/non-coffee/cookie.jpg'),

  -- 주스
  (70, '오렌지 주스',          3500, '상큼한 오렌지 생과일 주스',                140, 30,    0, 'NONE', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/fruit/orange-juice.jpg'),
  (71, '포도 주스',            3500, '달콤한 포도 생과일 주스',                  140, 30,    0, 'NONE', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/fruit/grape.jpg'),
  (72, '토마토 주스',          3500, '담백한 토마토 주스',                       80,  15,    0, 'NONE', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/fruit/tomato.jpg');

-- =========================
-- 3. 옵션 그룹 (OptionGroup)
--    group_id, group_name
-- =========================
INSERT INTO option_group (group_id, group_name)
VALUES
  (1, '사이즈 선택'),
  (2, 'HOT/ICE 선택'),
  (3, '샷 추가'),
  (4, '시럽 선택'),
  (5, '우유 선택');

-- =========================
-- 4. 옵션 값 (OptionValue)
--    value_id, group_id, value_name, extra_price
-- =========================
INSERT INTO option_value (value_id, group_id, value_name, extra_price)
VALUES
  -- SIZE
  (1, 1, '스몰',          0),
  (2, 1, '레귤러',        500),
  (3, 1, '라지',          1000),

  -- TEMPERATURE
  (4, 2, 'HOT',           0),
  (5, 2, 'ICE',           0),

  -- SHOT
  (6, 3, '샷 추가',   500),
  (7, 3, '샷 추가 2번',   1000),

  -- SYRUP
  (8, 4, '바닐라 시럽',   500),
  (9, 4, '카라멜 시럽',   500),
  (10,4, '헤이즐넛 시럽', 500),

  -- MILK_TYPE
  (11,5, '일반 우유',     0),
  (12,5, '저지방 우유',   0),
  (13,5, '오트 밀크',     500);

-- =========================
-- 5. MenuOption 더미 (중앙용 단순 옵션 정의)
--    (엔티티: MenuOption)
--    option_id, option_class, option_detail, extra_price
-- =========================
INSERT INTO menu_option (option_id, option_class, option_detail, extra_price)
VALUES
  (1, 'size',  '레귤러 사이즈',     0),
  (2, 'size',  '라지 사이즈',       1000),
  (3, 'shot',  '샷 추가',       500),
  (4, 'syrup', '바닐라 시럽 추가',  500),
  (5, 'milk',  '오트 밀크 변경',    500);

-- =========================
-- 6. User / Kakao 더미 데이터
-- =========================

-- User 한 명 생성 (user_id = 1 고정)
INSERT INTO user (user_id, username, profile_image, phone_number, signup_date, update_date, user_qr)
VALUES (
    1,
    '테스트유저',
    'https://example.com/profile/test-user.png',
    '01000000000',
    NOW(),
    NOW(),
    'USER1_QR_CODE'
);

-- 해당 유저에 연결된 Kakao 계정 1개 생성
INSERT INTO kakao (kakao_id, user_id, access_token, refresh_token, last_access_date)
VALUES (
    'kakao_test_1',
    1,
    'dummy-access-token',
    'dummy-refresh-token',
    NOW()
);

-- =========================
-- 7. PushHistory 더미 데이터
-- =========================
INSERT INTO push_history (user_id, push_type, message, sent_at, is_read)
VALUES
(1, 'WELCOME', '첫 로그인 안내 메시지입니다.', NOW(), 0),
(1, 'ORDER',   '주문이 접수되었습니다.',      NOW(), 0);

-- =========================
-- 8. CustomHeader / CustomDetail 더미 데이터
--    (사용자 커스텀 메뉴 예시)
-- =========================

-- 유저 1이 "진한 아이스 아메리카노" 커스텀을 저장했다고 가정
INSERT INTO custom_header (custom_id, user_id, menu_id, custom_name)
VALUES (
    1,             -- 직접 ID 지정 (AUTO_INCREMENT는 이후부터 계속 증가)
    1,             -- user_id
    1,             -- menu_id (아메리카노)
    '진한 아이스 아메리카노'
);

-- 해당 커스텀에 샷 1샷 추가, ICE, 라지 사이즈 등 옵션 연결 (예시)
INSERT INTO custom_detail (custom_detail_id, option_id, custom_id, extra_num)
VALUES
(1, 3, 1, 1),  -- option_id 3: 샷 1샷 추가
(2, 2, 1, 1);  -- option_id 2: 라지 사이즈
