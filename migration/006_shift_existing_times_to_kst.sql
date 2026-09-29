-- 일회성 데이터 보정: 기존 시각을 UTC 기준에서 KST 기준으로 +9시간 이동
--
-- 배경: 배포 서버(컨테이너)가 UTC로 동작해 LocalDateTime.now()가 UTC 벽시계로 저장되어 왔다.
-- 005 이후 애플리케이션 기본 시간대를 Asia/Seoul로 고정하므로, 그 이전에 쌓인 행만 한 번 보정한다.
--
-- 주의: 절대 두 번 실행하지 말 것. 두 번 돌리면 18시간이 밀린다.
-- 신규 DB에는 적용할 필요가 없다 (처음부터 KST로 저장되므로).
-- 실행 시점: 구버전 컨테이너를 내린 뒤, 신버전을 올리기 직전.
--
-- refresh_token / push_token은 화면에 노출되지 않고 만료 판정에만 쓰이므로 제외한다.

UPDATE board SET create_date = create_date + INTERVAL 9 HOUR,
                 update_date = update_date + INTERVAL 9 HOUR;

UPDATE comment SET created_date = created_date + INTERVAL 9 HOUR,
                   updated_date = updated_date + INTERVAL 9 HOUR;

UPDATE prayer SET created_date = created_date + INTERVAL 9 HOUR,
                  updated_date = updated_date + INTERVAL 9 HOUR,
                  promoted_at  = promoted_at  + INTERVAL 9 HOUR;

UPDATE prayer_comment SET created_date = created_date + INTERVAL 9 HOUR,
                          updated_date = updated_date + INTERVAL 9 HOUR;

UPDATE prayer_pray_log SET created_date = created_date + INTERVAL 9 HOUR;

UPDATE pastor_connection SET requested_at = requested_at + INTERVAL 9 HOUR,
                             responded_at = responded_at + INTERVAL 9 HOUR;

UPDATE user SET createdat   = createdat   + INTERVAL 9 HOUR,
                approved_at = approved_at + INTERVAL 9 HOUR;
