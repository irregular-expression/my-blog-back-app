UPDATE comments c
SET is_deleted = TRUE
WHERE id = :commentId AND post_id = :postId