-- Phase 4: 관리자 계정 1회 시딩
-- 이 프로젝트에는 회원가입 API로 관리자 계정을 만드는 경로가 없다(SignupService에서 role=관리자 요청을 차단함).
-- 따라서 최초 관리자 계정은 아래처럼 수동으로 1회 INSERT 한다.
--
-- 1) 비밀번호 해시 생성 (BCrypt) — 아래 중 편한 방법으로 원하는 평문 비밀번호를 해시로 변환한다.
--    - 백엔드 콘솔/테스트 코드에서 `new BCryptPasswordEncoder().encode("원하는비밀번호")` 실행
--    - 또는 신뢰할 수 있는 BCrypt 해시 생성 도구 사용 (예: bcrypt 라운드 10 기본값과 동일하게 생성)
--
-- 2) 아래 INSERT의 <BCRYPT_HASH>, <ADMIN_LOGIN_ID>, <ADMIN_NAME> 값을 교체 후 1회 실행한다.

INSERT INTO user (loginID, password, role, name, account_status, createdat)
VALUES ('<ADMIN_LOGIN_ID>', '<BCRYPT_HASH>', '관리자', '<ADMIN_NAME>', 'ACTIVE', NOW());

-- 확인
--   SELECT user_id, loginID, role, account_status FROM user WHERE role = '관리자';
