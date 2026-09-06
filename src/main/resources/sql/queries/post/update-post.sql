UPDATE posts p
SET title = :title,
    content = :content,
    updated_at = CURRENT_TIMESTAMP
WHERE id = :postId AND is_deleted = FALSE