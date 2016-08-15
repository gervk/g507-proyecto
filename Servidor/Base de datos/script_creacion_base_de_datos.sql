

CREATE DATABASE IF NOT EXISTS enersaving;

USE enersaving;

CREATE TABLE IF NOT EXISTS usuarios(
	user_name varchar(20) PRIMARY KEY NOT NULL,
	password varchar(40) NOT NULL,
	mail varchar(40) NOT NULL UNIQUE,
	ciudad varchar(50),
	id_preg_seguridad INT(10) UNSIGNED NOT NULL,
	codigo_arduino INT(10) UNSIGNED NOT NULL UNIQUE,
	id_serv_agua SMALLINT(3) NOT NULL,
	id_serv_luz SMALLINT(3) NOT NULL
);

CREATE TABLE IF NOT EXISTS pregunta_seguridad(
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	pregunta varchar(255),
	respuesta varchar(255)
);

CREATE TABLE IF NOT EXISTS arduino(
	codigo INT(10) UNSIGNED NOT NULL PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS consumo(
	id SERIAL,
	user_name varchar(20) NOT NULL,
	tipo TINYINT(1) NOT NULL,
	codigo_arduino INT(10) UNSIGNED NOT NULL,	
	consumo DOUBLE(7,2),
	fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (id, user_name, tipo)
);

CREATE TABLE IF NOT EXISTS tipo_consumo(
	id TINYINT(1) NOT NULL PRIMARY KEY,
	descripcion varchar(20)
);

CREATE TABLE IF NOT EXISTS servicio_luz(
	id INT(3) NOT NULL PRIMARY KEY,
	id_empresa INT(3) NOT NULL,
	id_tarifa INT(3) NOT NULL
);

CREATE TABLE IF NOT EXISTS empresa_luz(
	id INT(3) NOT NULL PRIMARY KEY,
	nombre varchar(50)
);

CREATE TABLE IF NOT EXISTS tarifa_luz(
	id INT(3) NOT NULL PRIMARY KEY,
	descripcion varchar(50),
	id_empresa INT(3) NOT NULL,
	cargo_fijo DOUBLE(7,2),
	cargo_variable DOUBLE(7,2)
);

