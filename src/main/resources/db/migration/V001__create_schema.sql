begin;

set local search_path to blog;

create table posts(
       id SERIAL PRIMARY KEY,
       title VARCHAR,
       content TEXT,
       likes_count INTEGER DEFAULT 0,
       comments_count INTEGER DEFAULT 0,
       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
       is_deleted BOOLEAN DEFAULT FALSE
);

create table comments (
       id SERIAL PRIMARY KEY,
       content TEXT,
       post_id INTEGER,
       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
       is_deleted BOOLEAN DEFAULT FALSE,
       FOREIGN KEY (post_id) REFERENCES posts(id)
);

create table tags (
       post_id INTEGER NOT NULL,
       tag VARCHAR NOT NULL,
       PRIMARY KEY (post_id, tag),
       FOREIGN KEY (post_id) REFERENCES posts(id)
);

create table images (
    id SERIAL PRIMARY KEY,
    post_id INTEGER,
    content BYTEA NOT NULL,
    upload_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES posts(id)
);

commit;