UPDATE images
SET content = :content,
    updated_at = CURRENT_TIMESTAMP
WHERE id = :postId