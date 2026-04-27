-- Creación de la tabla de créditos primero (por la relación ManyToOne)
CREATE TABLE creditos (
                          id UUID PRIMARY KEY,
                          cantidad INTEGER NOT NULL CHECK (cantidad >= 1 AND cantidad <= 15)
);

-- Creación de la tabla de asignaturas
CREATE TABLE asignaturas (
                             id UUID PRIMARY KEY,
                             nombre VARCHAR(255) NOT NULL,
                             credito_id UUID,
                             CONSTRAINT fk_asignatura_credito
                                 FOREIGN KEY (credito_id)
                                     REFERENCES creditos(id)
);

-- Creación de la tabla de prerrequisitos (relación recursiva)
CREATE TABLE prerequisitos (
                               id UUID PRIMARY KEY,
                               asignatura_id UUID,
                               prerequisito_id UUID,
                               CONSTRAINT fk_asignatura_principal
                                   FOREIGN KEY (asignatura_id)
                                       REFERENCES asignaturas(id),
                               CONSTRAINT fk_asignatura_requisito
                                   FOREIGN KEY (prerequisito_id)
                                       REFERENCES asignaturas(id)
);