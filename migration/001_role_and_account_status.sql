-- Phase 1: role 용어 통일 + 계정 승인 상태 컬럼 추가
-- 적용 전: 예상 밖의 role 값이 있는지 확인
--   SELECT DISTINCT role FROM user;
-- 위 결과가 '목사' 또는 '신도' 이외의 값을 포함하면 배포 전에 반드시 확인할 것.

-- 1) "신도" -> "성도" 용어 통일 (기존 가입자 데이터)
UPDATE user SET role = '성도' WHERE role = '신도';

-- 2) 계정 승인 상태 컬럼 추가
ALTER TABLE user
    ADD COLUMN account_status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    ADD COLUMN approved_by INT NULL,
    ADD COLUMN approved_at DATETIME NULL,
    ADD CONSTRAINT fk_user_approved_by FOREIGN KEY (approved_by) REFERENCES user (user_id);

-- 3) 기존에 이미 가입되어 있던 "목사" 계정은 이번 기능 도입 이전부터 실사용 중이었으므로
--    소급 승인 처리(ACTIVE)한다. 이후 신규 가입하는 목사 계정만 관리자 승인 대상이 된다.
UPDATE user SET account_status = 'ACTIVE' WHERE role = '목사';

-- 적용 후 확인
--   SELECT user_id, loginID, role, account_status FROM user;
