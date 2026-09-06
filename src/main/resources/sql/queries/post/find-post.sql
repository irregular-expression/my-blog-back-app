SELECT p.id as id,
       p.title as title,
       p.content as content,
       p.likes_count as likesCount,
       p.comments_count as commentsCount,
       t.tag as tag
FROM posts p
LEFT JOIN tags t ON t.post_id = p.id
WHERE p.id = :postId AND p.is_deleted = FALSE