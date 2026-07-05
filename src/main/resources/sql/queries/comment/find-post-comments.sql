SELECT c.id as id,
       c.content as content,
       c.post_id as postId
FROM comments c
WHERE c.id = :postId AND c.is_deleted = FALSE
ORDER BY id