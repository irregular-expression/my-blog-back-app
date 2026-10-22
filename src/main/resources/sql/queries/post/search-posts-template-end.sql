),
total_count AS (
    -- 2. Считаем общее количество таких постов (одна строка с числом)
    SELECT COUNT(*) AS total_posts
    FROM filtered_posts
),
paginated_posts AS (
    -- 3. Делаем пагинацию уже по отфильтрованным данным
    SELECT fp.*
    FROM filtered_posts fp
    ORDER BY fp.created_at DESC
    LIMIT :pageSize
    OFFSET :pageOffset
)
-- 4. Соединяем пагинированные посты, теги и total_posts
SELECT
    pp.id,
    pp.title,
    pp.content,
    pp.likes_count AS likesCount,
    pp.comments_count AS commentsCount,
    t.tag,
    tc.total_posts AS fullCount,
    :pageSize AS pageSize,
    :pageOffset AS pageOffset
FROM paginated_posts pp
CROSS JOIN total_count tc
LEFT JOIN blogtest.tags t ON t.post_id = pp.id;