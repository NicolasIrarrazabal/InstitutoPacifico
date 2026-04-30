
-- 1. Crear tabla de Especialidades (No tiene dependencias)
CREATE TABLE especialidades (
                                id UUID PRIMARY KEY, -- Mapeado de @Id UUID
                                nombre VARCHAR(100) NOT NULL, -- Mapeado de @NotBlank
                                descripcion TEXT NOT NULL -- Mapeado de @NotBlank
);

-- 2. Crear tabla de Docentes (Depende de Especialidades)
CREATE TABLE docentes (
                          id UUID PRIMARY KEY, -- Mapeado de @Id UUID
                          nombre VARCHAR(100) NOT NULL, -- Mapeado de @NotBlank
                          apellido VARCHAR(100) NOT NULL, -- Mapeado de @NotBlank
                          email VARCHAR(150) NOT NULL UNIQUE, -- Mapeado de @Email y unique=true
                          especialidad_id UUID NOT NULL, -- Mapeado de @NotNull
                          CONSTRAINT fk_docente_especialidad
                              FOREIGN KEY (especialidad_id)
                                  REFERENCES especialidades(id)
);

-- 3. Crear tabla de Contratos (Depende de Docentes)
CREATE TABLE contratos (
                           id UUID PRIMARY KEY, -- Mapeado de @Id UUID
                           tipo_contrato VARCHAR(50) NOT NULL, -- Mapeado de @NotBlank
                           fecha_inicio DATE NOT NULL, -- Mapeado de @NotNull LocalDate
                           fecha_fin DATE, -- Campo opcional
                           sueldo_base DECIMAL(12, 2) NOT NULL CHECK (sueldo_base >= 0), -- Mapeado de @Min(0)
                           docente_id UUID NOT NULL UNIQUE, -- Mapeado de @OneToOne y @NotNull
                           CONSTRAINT fk_contrato_docente
                               FOREIGN KEY (docente_id)
                                   REFERENCES docentes(id)
                                   ON DELETE CASCADE
);