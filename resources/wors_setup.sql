#---------
#CREATE DATABASE & TABLES
#---------

CREATE DATABASE wors_database;
USE wors_database;

# This setting is required so you can delete things without doing a WHERE on a unique key
SET SQL_SAFE_UPDATES = 0;

CREATE TABLE join_roles
(
	role_id char(18) primary key,
    guild_id char(18)
);

CREATE TABLE guild_bans
(
	member_id char(18),
	guild_id char(18)
);

CREATE TABLE guild_logs
(
	guild_id char(18) primary key,
    log_channel_id char(18)
);

CREATE TABLE guild_permissions
(
	guild_id char(18),
    member_id char(18),
    permission varchar(1000)
);

CREATE TABLE guild_facts
(
    fact_id int NOT NULL AUTO_INCREMENT primary key,
    guild_id char(18),
    fact varchar(1000)
);