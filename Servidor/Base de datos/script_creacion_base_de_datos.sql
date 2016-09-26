CREATE TABLE IF NOT EXISTS usuario(
	user_name varchar(20) NOT NULL,
	password varchar(40) NOT NULL,
	mail varchar(40) NOT NULL UNIQUE,
	ciudad varchar(50),
	id_preg_seguridad INTEGER NOT NULL,
	codigo_arduino INTEGER NOT NULL UNIQUE,
	id_serv_agua INTEGER NOT NULL,
	id_serv_luz INTEGER NOT NULL,
	created_at TIMESTAMP NOT NULL,
	updated_at TIMESTAMP NOT NULL,
	PRIMARY KEY (user_name)
);

CREATE TABLE IF NOT EXISTS pregunta_seguridad(
	id SERIAL,
	user_name varchar(20) NOT NULL,
	pregunta varchar(255),
	respuesta varchar(255),
	created_at TIMESTAMP NOT NULL,
	updated_at TIMESTAMP NOT NULL,
	PRIMARY KEY (id, user_name)
);

CREATE TABLE IF NOT EXISTS arduino(
	codigo INTEGER NOT NULL PRIMARY KEY
	created_at TIMESTAMP NOT NULL,
	updated_at TIMESTAMP NOT NULL,
);

CREATE TABLE IF NOT EXISTS consumo(
	id SERIAL,
	codigo_arduino INTEGER NOT NULL,	
	tipo smallint NOT NULL,
	consumo double precision,
	created_at TIMESTAMP NOT NULL,
	updated_at TIMESTAMP NOT NULL,
	PRIMARY KEY (id, codigo_arduino, tipo)
);

CREATE TABLE IF NOT EXISTS tipo_consumo(
	id smallint NOT NULL PRIMARY KEY,
	descripcion varchar(20)
	created_at TIMESTAMP NOT NULL,
	updated_at TIMESTAMP NOT NULL,
);

CREATE TABLE IF NOT EXISTS servicio_luz(
	id INTEGER NOT NULL PRIMARY KEY,
	id_empresa INTEGER NOT NULL,
	id_tarifa INTEGER NOT NULL
	created_at TIMESTAMP NOT NULL,
	updated_at TIMESTAMP NOT NULL,
);

CREATE TABLE IF NOT EXISTS empresa_luz(
	id INTEGER NOT NULL PRIMARY KEY,
	nombre varchar(50)
	created_at TIMESTAMP NOT NULL,
	updated_at TIMESTAMP NOT NULL,
);

CREATE TABLE IF NOT EXISTS tarifa_luz(
	id INTEGER NOT NULL PRIMARY KEY,
	descripcion varchar(50),
	id_empresa INTEGER NOT NULL,
	cargo_fijo double precision,
	cargo_variable double precision
	created_at TIMESTAMP NOT NULL,
	updated_at TIMESTAMP NOT NULL,
);

