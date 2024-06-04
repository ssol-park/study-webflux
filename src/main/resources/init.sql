-- init.sql
CREATE DATABASE IF NOT EXISTS study;

-- 삭제할 경우
DROP USER IF EXISTS 'srpark'@'%';

-- 사용자 생성 및 권한 부여
CREATE USER IF NOT EXISTS 'srpark'@'%' IDENTIFIED BY '!@#QWE123qwe';
GRANT ALL PRIVILEGES ON study.* TO 'srpark'@'%';
FLUSH PRIVILEGES;