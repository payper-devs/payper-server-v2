/* =========================================================
   MySQL 8.0 더미 데이터 생성 (대용량 / FK+UK 만족)
   - category:       50
   - merchant:     5,000
   - users:       50,000
   - posts:      200,000
   - comments:   800,000
   - post_like:1,000,000
   - post_bookmark:300,000
   - comment_like:500,000
   ========================================================= */

SET SESSION sql_safe_updates = 0;
SET SESSION foreign_key_checks = 0;
SET SESSION unique_checks = 0;

-- 큰 트랜잭션이 부담이면 autocommit=1로 두고 실행해도 됨.
SET autocommit = 0;

-- 최근 365일 랜덤 날짜 생성 기준
SET @start_dt := TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 365 DAY));

/* ---------------------------------------------------------
   0) 0..999,999 시퀀스(seq_1m) 만들기
   - TEMP digits 재사용 에러(1137) 방지: 인라인 digits 사용
   --------------------------------------------------------- */
DROP TEMPORARY TABLE IF EXISTS seq_1m;
CREATE TEMPORARY TABLE seq_1m (
  n INT UNSIGNED NOT NULL PRIMARY KEY
) ENGINE=InnoDB;

INSERT INTO seq_1m(n)
SELECT
    d0.n
        + d1.n*10
        + d2.n*100
        + d3.n*1000
        + d4.n*10000
        + d5.n*100000 AS n
FROM (SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
      UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) d0
         CROSS JOIN (SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
                     UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) d1
         CROSS JOIN (SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
                     UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) d2
         CROSS JOIN (SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
                     UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) d3
         CROSS JOIN (SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
                     UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) d4
         CROSS JOIN (SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
                     UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) d5;

COMMIT;

/* ---------------------------------------------------------
   1) (선택) 기존 데이터 비우기
   - FK 관계 고려해서 child -> parent 순서로 TRUNCATE
   --------------------------------------------------------- */
TRUNCATE TABLE comment_like;
TRUNCATE TABLE post_like;
TRUNCATE TABLE post_bookmark;
TRUNCATE TABLE comments;
TRUNCATE TABLE posts;
TRUNCATE TABLE users;
TRUNCATE TABLE merchant;
TRUNCATE TABLE category;

-- 필요하면 추가로 비우기(원하면 주석 해제)
-- TRUNCATE TABLE favorite;
-- TRUNCATE TABLE post_report;
-- TRUNCATE TABLE refresh_token_entity;

/* ---------------------------------------------------------
   2) category 50
   - 앞 10개는 루트, 나머지는 1~10 중 랜덤 부모
   --------------------------------------------------------- */
INSERT INTO category (id, parent_category_id, name)
SELECT
    n + 1 AS id,
    CASE
        WHEN n < 10 THEN NULL
        ELSE 1 + FLOOR(RAND(n) * 10)
        END AS parent_category_id,
    CONCAT('Category_', LPAD(n + 1, 2, '0')) AS name
FROM seq_1m
WHERE n < 50;

COMMIT;

/* ---------------------------------------------------------
   3) merchant 5,000
   --------------------------------------------------------- */
INSERT INTO merchant (id, category_id, image_url, name)
SELECT
    n + 1 AS id,
    1 + FLOOR(RAND(n) * 50) AS category_id,
    CONCAT('https://picsum.photos/seed/merchant_', n + 1, '/300/300') AS image_url,
    CONCAT('Merchant_', LPAD(n + 1, 5, '0')) AS name
FROM seq_1m
WHERE n < 5000;

COMMIT;

/* ---------------------------------------------------------
   4) users 50,000 (user_identifier UNIQUE 보장)
   --------------------------------------------------------- */
INSERT INTO users (id, active, name, oauth_id, user_identifier, auth_type, user_role)
SELECT
    n + 1 AS id,
    b'1' AS active,
    CONCAT('User_', LPAD(n + 1, 5, '0')) AS name,
    CONCAT('kakao_', n + 1) AS oauth_id,
    CONCAT('uid_', LPAD(n + 1, 6, '0')) AS user_identifier,
    'KAKAO' AS auth_type,
    CASE WHEN n < 20 THEN 'ADMIN' ELSE 'USER' END AS user_role
FROM seq_1m
WHERE n < 50000;

COMMIT;

/* ---------------------------------------------------------
   5) posts 200,000
   - author_id: 1..50,000
   - merchant_id: 1..5,000
   - created_at: 최근 365일 랜덤
   - updated_at: created_at + 0~7일
   --------------------------------------------------------- */
INSERT INTO posts (
    id, is_deleted, is_inactive,
    author_id, comment_count,
    created_at, deleted_at,
    inactive_at, like_count,
    merchant_id, report_count,
    updated_at, view_count,
    content, title, type
)
SELECT
    t.n + 1 AS id,
    b'0' AS is_deleted,
    b'0' AS is_inactive,
    1 + FLOOR(RAND(t.n) * 50000) AS author_id,
    0 AS comment_count,
    t.created_at,
    NULL AS deleted_at,
    NULL AS inactive_at,
    0 AS like_count,
    1 + FLOOR(RAND(t.n + 2) * 5000) AS merchant_id,
    0 AS report_count,
    t.created_at + INTERVAL FLOOR(RAND(t.n + 3) * 10080) MINUTE AS updated_at,
    FLOOR(RAND(t.n + 4) * 5000) AS view_count,
    CONCAT('Post#', t.n + 1, ' ',
    REPEAT(SUBSTRING(MD5(CONCAT('p', t.n)), 1, 32), 20)) AS content,
    CONCAT('Post title ', t.n + 1) AS title,
    ELT(1 + (t.n % 3), 'BENEFIT','ETC','QUESTION') AS type
FROM (
    SELECT
    n,
    (@start_dt
    + INTERVAL FLOOR(RAND(n + 1) * 365) DAY
    + INTERVAL FLOOR(RAND(n + 5) * 86400) SECOND
    ) AS created_at
    FROM seq_1m
    WHERE n < 200000
    ) t;

COMMIT;

/* ---------------------------------------------------------
   6) comments 800,000
   - parent_comment_id는 일단 NULL
   - 이후 20% 정도를 대댓글로 업데이트(부모 댓글과 post_id 동일)
   --------------------------------------------------------- */
INSERT INTO comments (
    id, is_deleted,
    created_at, deleted_at,
    like_count, parent_comment_id,
    post_id, updated_at,
    user_id, content
)
SELECT
    t.n + 1 AS id,
    b'0' AS is_deleted,
    t.created_at,
    NULL AS deleted_at,
    0 AS like_count,
    NULL AS parent_comment_id,
    1 + FLOOR(RAND(t.n + 10) * 200000) AS post_id,
    t.created_at + INTERVAL FLOOR(RAND(t.n + 11) * 1440) MINUTE AS updated_at,
    1 + FLOOR(RAND(t.n + 12) * 50000) AS user_id,
    CONCAT('Comment#', t.n + 1, ' ',
    REPEAT(SUBSTRING(MD5(CONCAT('c', t.n)), 1, 32), 5)) AS content
FROM (
    SELECT
    n,
    (@start_dt
    + INTERVAL FLOOR(RAND(n + 9) * 365) DAY
    + INTERVAL FLOOR(RAND(n + 13) * 86400) SECOND
    ) AS created_at
    FROM seq_1m
    WHERE n < 800000
    ) t;

COMMIT;

-- 대댓글(20%): 매 5번째 댓글은 직전 댓글을 부모로
-- 동시에 post_id도 부모와 동일하게 맞춤
UPDATE comments c
    JOIN comments p ON p.id = c.id - 1
    SET
        c.parent_comment_id = p.id,
        c.post_id = p.post_id
WHERE (c.id % 5) = 0;

COMMIT;

/* ---------------------------------------------------------
   7) post_like 1,000,000
   - UNIQUE(post_id, user_id) 충돌 방지 단사 매핑
   --------------------------------------------------------- */
INSERT INTO post_like (post_id, user_id)
SELECT
    1 + (n % 200000) AS post_id,
    1 + (((n % 200000) + (FLOOR(n / 200000) * 10000)) % 50000) AS user_id
FROM seq_1m
WHERE n < 1000000;

COMMIT;

/* ---------------------------------------------------------
   8) post_bookmark 300,000
   - UNIQUE(post_id, user_id) 충돌 방지 단사 매핑
   --------------------------------------------------------- */
INSERT INTO post_bookmark (post_id, user_id)
SELECT
    1 + (n % 200000) AS post_id,
    1 + (((n % 200000) + (FLOOR(n / 200000) * 25000)) % 50000) AS user_id
FROM seq_1m
WHERE n < 300000;

COMMIT;

/* ---------------------------------------------------------
   9) comment_like 500,000
   - comment_id를 1..500,000로 잡아 FK 만족 + 충돌 원천 차단
   --------------------------------------------------------- */
INSERT INTO comment_like (comment_id, user_id)
SELECT
    1 + n AS comment_id,
    1 + ((n * 37) % 50000) AS user_id
FROM seq_1m
WHERE n < 500000;

COMMIT;

/* ---------------------------------------------------------
   (선택) posts.like_count / posts.comment_count / comments.like_count
   실제 테이블 기반으로 집계해서 맞추기(시간 꽤 걸릴 수 있음)
   --------------------------------------------------------- */

-- posts.like_count
DROP TEMPORARY TABLE IF EXISTS tmp_post_like_cnt;
CREATE TEMPORARY TABLE tmp_post_like_cnt (
  post_id BIGINT PRIMARY KEY,
  cnt BIGINT NOT NULL
) ENGINE=InnoDB
AS
SELECT post_id, COUNT(*) AS cnt
FROM post_like
GROUP BY post_id;

UPDATE posts p
    LEFT JOIN tmp_post_like_cnt t ON t.post_id = p.id
    SET p.like_count = COALESCE(t.cnt, 0);

COMMIT;

-- posts.comment_count
DROP TEMPORARY TABLE IF EXISTS tmp_post_comment_cnt;
CREATE TEMPORARY TABLE tmp_post_comment_cnt (
  post_id BIGINT PRIMARY KEY,
  cnt BIGINT NOT NULL
) ENGINE=InnoDB
AS
SELECT post_id, COUNT(*) AS cnt
FROM comments
WHERE is_deleted = b'0'
GROUP BY post_id;

UPDATE posts p
    LEFT JOIN tmp_post_comment_cnt t ON t.post_id = p.id
    SET p.comment_count = COALESCE(t.cnt, 0);

COMMIT;

-- comments.like_count
DROP TEMPORARY TABLE IF EXISTS tmp_comment_like_cnt;
CREATE TEMPORARY TABLE tmp_comment_like_cnt (
  comment_id BIGINT PRIMARY KEY,
  cnt BIGINT NOT NULL
) ENGINE=InnoDB
AS
SELECT comment_id, COUNT(*) AS cnt
FROM comment_like
GROUP BY comment_id;

UPDATE comments c
    LEFT JOIN tmp_comment_like_cnt t ON t.comment_id = c.id
    SET c.like_count = COALESCE(t.cnt, 0);

COMMIT;

-- 마무리
SET SESSION foreign_key_checks = 1;
SET SESSION unique_checks = 1;
SET autocommit = 1;
