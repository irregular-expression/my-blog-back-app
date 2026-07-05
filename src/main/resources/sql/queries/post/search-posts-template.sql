SELECT p.id as id,
       p.title as title,
       p.content as content,
       p.likes_count as likesCount,
       p.comments_count as commentsCount,
       t.tag as tag,
       count(1) over() as fullCount,
       :pageSize as pageSize,
       :pageOffset as pageOffset
FROM posts p
LEFT JOIN tags t ON t.post_id = p.id
WHERE p.is_deleted = FALSE%s
ORDER BY p.id, t.tag
LIMIT :pageSize OFFSET :pageOffset