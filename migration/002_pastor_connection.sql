-- Phase 3: 목사-성도 연결 테이블 신규 생성
CREATE TABLE pastor_connection (
    connection_id  BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id      INT NOT NULL,
    pastor_id      INT NOT NULL,
    status         VARCHAR(20) NOT NULL,
    requested_at   DATETIME NOT NULL,
    responded_at   DATETIME NULL,
    CONSTRAINT fk_connection_member FOREIGN KEY (member_id) REFERENCES user (user_id),
    CONSTRAINT fk_connection_pastor FOREIGN KEY (pastor_id) REFERENCES user (user_id)
);

CREATE INDEX idx_connection_member_status ON pastor_connection (member_id, status);
CREATE INDEX idx_connection_pastor_status ON pastor_connection (pastor_id, status);
