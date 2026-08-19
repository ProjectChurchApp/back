-- Phase 5: Prayer 확장 (나의기도/중보기도 분리, 상태, 카운터)

ALTER TABLE prayer
    ADD COLUMN board_stage VARCHAR(30) NULL,
    ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'PRAYING',
    ADD COLUMN prayer_count INT NOT NULL DEFAULT 0,
    ADD COLUMN promoted_by INT NULL,
    ADD COLUMN promoted_at DATETIME NULL,
    ADD CONSTRAINT fk_prayer_promoted_by FOREIGN KEY (promoted_by) REFERENCES user (user_id);

-- 기존 visibility 값을 board_stage로 백필
UPDATE prayer SET board_stage = 'PERSONAL' WHERE visibility = 'PRIVATE';
UPDATE prayer SET board_stage = 'SHARED_WITH_PASTOR' WHERE visibility = 'PASTOR';

ALTER TABLE prayer MODIFY COLUMN board_stage VARCHAR(30) NOT NULL;

CREATE TABLE prayer_pray_log (
    pray_log_id  BIGINT AUTO_INCREMENT PRIMARY KEY,
    prayer_id    BIGINT NOT NULL,
    user_id      INT NOT NULL,
    created_date DATETIME NOT NULL,
    CONSTRAINT fk_pray_log_prayer FOREIGN KEY (prayer_id) REFERENCES prayer (prayer_id),
    CONSTRAINT fk_pray_log_user FOREIGN KEY (user_id) REFERENCES user (user_id),
    CONSTRAINT uq_pray_log_prayer_user UNIQUE (prayer_id, user_id)
);
