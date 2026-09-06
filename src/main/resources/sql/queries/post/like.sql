UPDATE posts
SET likes_count = likes_count + 1
WHERE id = :postId AND is_deleted = FALSE