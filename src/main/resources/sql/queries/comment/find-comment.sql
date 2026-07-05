SELECT id,
       content,
       post_id as postId
FROM comments
WHERE id = :postId AND id = :commentId AND is_deleted = FALSE