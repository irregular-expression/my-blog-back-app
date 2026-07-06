SELECT id,
       content,
       post_id as postId
FROM comments
WHERE post_id = :postId AND id = :commentId AND is_deleted = FALSE