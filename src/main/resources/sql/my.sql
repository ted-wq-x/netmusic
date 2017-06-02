create table netmusic
(
  id int not null
    primary key,
  commit_total int not null,
  constraint netmusic_id_uindex
  unique (id)
)
;

create table netmusic_comments
(
  id int not null
    primary key,
  liked_count int null,
  content mediumtext null,
  user_id int not null,
  commit_time int null,#错误的时间使用默认值0
  song_id int not null,
  constraint music_comments_id_uindex
  unique (id)
)
;

create table netmusic_user
(
  id int not null
    primary key,
  nick_name varchar(128) null,
  avatar_url varchar(256) null,
  constraint netmusic_user_id_uindex
  unique (id)
)
;

INSERT INTO netmusic_user VALUE (94442174,'萌太奇-熊','http://p1.music.126.net/gEEMi6-dEI8EWYw9xEX0fA==/1401877327530352.jpg')

# 修改字符集
# ALTER DATABASE tale CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
# ALTER TABLE netmusic_comments CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
# ALTER TABLE netmusic_comments MODIFY content TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
# SHOW VARIABLES WHERE Variable_name LIKE 'character_set_%' OR Variable_name LIKE 'collation%';