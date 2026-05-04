CREATE TABLE estudiantes (
    id UUID PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    rut VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    telefono VARCHAR(50),
    direccion VARCHAR(255),
    estado VARCHAR(20) NOT NULL
);