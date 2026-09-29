-- Phase 6: 기도 카운트 누적, 상태 변경일 기록, 중보기도 요청 플래그

-- 1) 기도했어요를 누를 때마다 누적되도록 1인 1회 제약 제거
--    UNIQUE(prayer_id, user_id)의 앞부분이 prayer_id 외래키를 떠받치고 있으므로
--    (MySQL 오류 1553) prayer_id 단독 인덱스를 먼저 만든 뒤 UNIQUE를 제거한다.
ALTER TABLE prayer_pray_log ADD INDEX idx_pray_log_prayer (prayer_id);
ALTER TABLE prayer_pray_log DROP INDEX uq_pray_log_prayer_user;

-- 2) 응답/종료로 상태가 바뀐 시점 기록
ALTER TABLE prayer ADD COLUMN status_changed_at DATETIME NULL;

-- 3) 성도가 작성 시 중보기도까지 공유를 요청했는지 여부
ALTER TABLE prayer ADD COLUMN intercessory_requested BOOLEAN NOT NULL DEFAULT FALSE;

-- 이미 중보기도로 승격된 글은 요청한 것으로 백필
UPDATE prayer SET intercessory_requested = TRUE WHERE board_stage = 'INTERCESSORY';
