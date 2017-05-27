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
  commit_time int null,
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

