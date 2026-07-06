UPDATE comments c
SET content = :content,
    updated_at = CURRENT_TIMESTAMP
WHERE post_id = :postId AND is_deleted = FALSE