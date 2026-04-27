CREATE TABLE especialidades (
                                id UUID PRIMARY KEY,
                                nombre VARCHAR(100) NOT NULL,
                                descripcion TEXT
);

CREATE TABLE docentes (
                          id UUID PRIMARY KEY,
                          nombre VARCHAR(100) NOT NULL,
                          apellido VARCHAR(100) NOT NULL,
                          email VARCHAR(150) NOT NULL UNIQUE,
                          especialidad_id UUID,
                          CONSTRAINT fk_docente_especialidad
                              FOREIGN KEY (especialidad_id)
                                  REFERENCES especialidades(id)
);

CREATE TABLE contratos (
                           id UUID PRIMARY KEY,
                           tipo_contrato VARCHAR(50) NOT NULL,
                           fecha_inicio DATE NOT NULL,
                           fecha_fin DATE,
                           sueldo_base DECIMAL(12, 2) NOT NULL CHECK (sueldo_base >= 0),
                           docente_id UUID UNIQUE NOT NULL,
                           CONSTRAINT fk_contrato_docente
                               FOREIGN KEY (docente_id)
                                   REFERENCES docentes(id)
                                   ON DELETE CASCADE
);