SELECT t.tag as tag
FROM tags t
WHERE t.post_id = :postId