UPDATE images
SET content = :content,
    upload_time = CURRENT_TIMESTAMP
WHERE post_id = :postId