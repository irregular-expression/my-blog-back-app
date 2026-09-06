SELECT id,
       content,
       post_id as postId
FROM comments c
WHERE post_id = :postId AND is_deleted = FALSE
ORDER BY id