CREATE DATABASE IF NOT EXISTS alexej_krawez_todo_db CHARACTER SET utf8 COLLATE utf8_general_ci;
USE alexej_krawez_todo_db;
CREATE TABLE alexej_krawez_todo_db.users (
  user_id int(9) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  email char(50) NOT NULL,
  login char(30) NOT NULL,
  password char(30) NOT NULL
) ENGINE=InnoDB;
CREATE UNIQUE INDEX users_user_id_uindex ON alexej_krawez_todo_db.users (user_id);
CREATE UNIQUE INDEX users_email_uindex ON alexej_krawez_todo_db.users (email);
CREATE UNIQUE INDEX users_login_uindex ON alexej_krawez_todo_db.users (login);
SET time_zone = '+00:00';
CREATE TABLE alexej_krawez_todo_db.notes (
  user_id int(9) NOT NULL,
  id int(9) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  date DATETIME DEFAULT CURRENT_TIMESTAMP,
  target_date DATETIME DEFAULT CURRENT_TIMESTAMP,
  note TEXT(500) DEFAULT NULL,
  file_path varchar(255) DEFAULT NULL,
  status TINYINT(1) DEFAULT 1 NOT NULL,
  FOREIGN KEY notes_users__fk(user_id)
  REFERENCES users(user_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB;
CREATE UNIQUE INDEX notes_id_uindex ON notes (id);