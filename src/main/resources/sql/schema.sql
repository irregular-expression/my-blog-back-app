begin;

create schema if not exists blog;
set local search_path to blog;

create table if not exists posts(
       id SERIAL PRIMARY KEY,
       title VARCHAR,
       content TEXT,
       likes_count INTEGER DEFAULT 0,
       comments_count INTEGER DEFAULT 0,
       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
       is_deleted BOOLEAN DEFAULT FALSE
);

create table if not exists comments (
       id SERIAL PRIMARY KEY,
       content TEXT,
       post_id INTEGER,
       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
       is_deleted BOOLEAN DEFAULT FALSE,
       FOREIGN KEY (post_id) REFERENCES posts(id)
);

create table if not exists tags (
       post_id INTEGER NOT NULL,
       tag VARCHAR NOT NULL,
       PRIMARY KEY (post_id, tag),
       FOREIGN KEY (post_id) REFERENCES posts(id)
);

create table if not exists images (
    id SERIAL PRIMARY KEY,
    post_id INTEGER,
    content BYTEA NOT NULL,
    upload_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES posts(id)
);

commit;