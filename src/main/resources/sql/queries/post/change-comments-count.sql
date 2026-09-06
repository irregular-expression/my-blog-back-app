UPDATE posts
SET comments_count = comments_count + :modifier
WHERE id = :postId AND is_deleted = FALSE