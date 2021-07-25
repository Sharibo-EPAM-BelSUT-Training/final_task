CREATE TABLE alexej_krawez_todo_db.users (
  user_id int(9) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  email char(50) NOT NULL,
  login char(30) NOT NULL,
  password char(30) NOT NULL
) ENGINE=InnoDB;
CREATE UNIQUE INDEX users_user_id_uindex ON alexej_krawez_todo_db.users (user_id);
CREATE UNIQUE INDEX users_email_uindex ON alexej_krawez_todo_db.users (email);
CREATE UNIQUE INDEX users_login_uindex ON alexej_krawez_todo_db.users (login);