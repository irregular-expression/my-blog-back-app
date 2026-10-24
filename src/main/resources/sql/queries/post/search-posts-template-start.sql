WITH filtered_posts AS (
    -- 1. Находим все посты, подходящие под фильтры (без LIMIT/OFFSET)
    SELECT p.*
    FROM blogtest.posts p
    WHERE p.is_deleted = FALSE