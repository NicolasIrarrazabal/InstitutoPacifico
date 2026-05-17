# Instituto Pacifico — Documentación de Endpoints


Sistema completo de **10 microservicios** Spring Boot que digitaliza la gestión académica de un instituto educativo. Este documento describe todos los endpoints disponibles, organizados por fases de ejecución.

---

## Tabla de contenido

1. [Equipo](#equipo)
2. [Stack tecnológico](#stack-tecnológico)
3. [Arquitectura](#arquitectura)
4. [Microservicios y puertos](#microservicios-y-puertos)
5. [Reglas de negocio implementadas](#reglas-de-negocio-implementadas)
6. [Fases de ejecución](#fases-de-ejecución)
7. [Notas importantes](#notas-importantes)

---

## Equipo

| Rol | Aporte principal |
|---|---|
| Estudiante 1 | Modelado y MS de carreras, asignaturas, docentes |
| Estudiante 2 | MS de empresas, estudiantes, matrículas, aranceles |
| Estudiante 3 | MS de notas, asistencia, prácticas y WebClient |

> Vicente Herrera y Nicolas Irarrazabal.

---

## Stack tecnológico

| Capa | Tecnología |
|---|---|
| Lenguaje | Java 17 |
| Build | Apache Maven 3.9+ (multi-módulo) |
| Framework | Spring Boot 3.x |
| Web | Spring Web (REST) 6.x |
| Comunicación | Spring WebFlux + WebClient |
| Validaciones | Bean Validation (JSR-380) + Hibernate Validator |
| Logs | SLF4J + Logback |
| Persistencia | JPA + Hibernate con H2 (in-memory) |
| Pruebas | Postman 10+ |

---

## Arquitectura

El sistema sigue el patrón **CSR (Controller → Service → Repository)** y utiliza comunicación **WebClient** entre microservicios para validar reglas de negocio.

```mermaid
flowchart LR
    Cliente([Cliente / Postman])

    subgraph dominioAcademico["Dominio: Académico"]
        msCarreras["ms-carreras<br/>:8084"]
        msAsignaturas["ms-asignaturas<br/>:8080"]
        msDocente["ms-docente<br/>:8082"]
    end

    subgraph dominioEstudiantes["Dominio: Estudiantes"]
        msEstudiante["ms-estudiante<br/>:8085"]
        msMatriculas["ms-matriculas<br/>:8083"]
    end

    subgraph dominioFinanciero["Dominio: Financiero"]
        msAranceles["ms-aranceles<br/>:8081"]
    end

    subgraph dominioAcademico2["Dominio: Notas y Asistencia"]
        msNotas["ms-notas<br/>:8086"]
        msAsistencia["ms-asistencia<br/>:8088"]
    end

    subgraph dominioPracticas["Dominio: Prácticas"]
        msPracticas["ms-practicas<br/>:8087"]
        msEmpresas["ms-empresas<br/>:8089"]
    end

    Cliente -->|REST| msCarreras
    Cliente -->|REST| msAsignaturas
    Cliente -->|REST| msDocente
    Cliente -->|REST| msEstudiante
    Cliente -->|REST| msMatriculas
    Cliente -->|REST| msAranceles
    Cliente -->|REST| msNotas
    Cliente -->|REST| msAsistencia
    Cliente -->|REST| msPracticas
    Cliente -->|REST| msEmpresas

    msEstudiante -->|GET matriculas| msMatriculas
    msPracticas -->|validar requisitos| msAranceles
    msPracticas -->|validar empresa| msEmpresas

    classDef ms fill:#e3f2fd,stroke:#1976d2,stroke-width:2px,color:#0d47a1
    classDef cli fill:#fff3e0,stroke:#f57c00,stroke-width:2px,color:#e65100
    class msCarreras,msAsignaturas,msDocente,msEstudiante,msMatriculas,msAranceles,msNotas,msAsistencia,msPracticas,msEmpresas ms
    class Cliente cli
```

---

## Microservicios y puertos

| # | Microservicio | Puerto | Path base | Responsabilidad |
|---|---|---|---|---|
| 1 | `ms-carreras` | 8084 | `/api/v1/carreras` | CRUD carreras universitarias |
| 2 | `ms-asignaturas` | 8080 | `/api/v1/asignaturas` | CRUD asignaturas y prerrequisitos |
| 3 | `ms-docente` | 8082 | `/api/v1/docentes` | CRUD docentes, especialidades y contratos |
| 4 | `ms-empresas` | 8089 | `/api/v1/empresas` | CRUD empresas y convenios |
| 5 | `ms-estudiante` | 8085 | `/api/v1/estudiantes` | CRUD estudiantes y detalle académico |
| 6 | `ms-matriculas` | 8083 | `/api/v1/matriculas` | CRUD matrículas |
| 7 | `ms-aranceles` | 8081 | `/api/v1/aranceles` | CRUD aranceles, pagos y validación R5 |
| 8 | `ms-notas` | 8086 | `/api/v1/notas` | CRUD notas, promedios y avance 80% |
| 9 | `ms-asistencia` | 8088 | `/api/v1/asistencias` | CRUD asistencia y resumen R2 |
| 10 | `ms-practicas` | 8087 | `/api/v1/practicas` | CRUD prácticas, verificación R5 |

---

## Reglas de negocio implementadas

Las **5 reglas obligatorias** del caso están codificadas en la capa `Service` de los MS correspondientes y son verificables con Postman.

### R1 — Disponibilidad de carrera
- **MS responsable:** `ms-carreras`
- Validar que la carrera existe y está disponible.

### R2 — Asistencia mínima 75%
- **MS responsable:** `ms-asistencia` (`AsistenciaService.calcularResumen`)
- Calcular el porcentaje de asistencia del estudiante por sección.

### R3 — Promedio mínimo 5.0
- **MS responsable:** `ms-notas` (`NotaService.calcularPromedio`)
- Calcular el promedio global del estudiante.

### R4 — Empresa con convenio vigente
- **MS responsable:** `ms-empresas` (`EmpresaService.tieneConvenioVigente`)
- Verificar que la empresa tenga un convenio activo (fechaInicio <= fechaActual <= fechaFin).

### R5 — Requisitos para prácticas profesionales
- **MS responsable:** `ms-practicas` (`PracticaService.verificarRequisitos`)
- Validar antes de crear una práctica:
  - Estudiante sin deuda vencida (ms-aranceles)
  - Estudiante con avance >= 80% en notas (ms-notas)
  - Empresa con convenio vigente (ms-empresas)

### Diagrama de secuencia — Crear práctica profesional

```mermaid
sequenceDiagram
    actor Cliente
    participant MP as ms-practicas<br/>:8087
    participant MA as ms-aranceles<br/>:8081
    participant MN as ms-notas<br/>:8086
    participant ME as ms-empresas<br/>:8089

    Cliente->>+MP: POST /api/v1/practicas<br/>{estudianteId, empresaId}

    Note over MP,ME: R4 — verificar convenio vigente
    MP->>+ME: WebClient GET /empresas/{id}/tiene-convenio-vigente
    alt empresa sin convenio
        ME-->>MP: {tieneConvenio: false}
        MP-->>Cliente: 422 R4 violada
    end

    Note over MP,MA: R5 — verificar deuda
    MP->>+MA: WebClient GET /aranceles/estudiante/{id}/tiene-deuda-vencida
    alt tiene deuda
        MA-->>MP: {tieneDeuda: true}
        MP-->>Cliente: 422 R5 violada
    end

    Note over MP,MN: R5 — verificar avance
    MP->>+MN: WebClient GET /notas/estudiante/{id}/avance
    alt avance < 80%
        MN-->>MP: {avance: 0.6}
        MP-->>Cliente: 422 R5 violada
    end

    MP-->>-Cliente: 201 Created {practica}
```

---

## Fases de ejecución

### Comunicación inter-microservicios (WebClient)

| MS Origen | MS Destino | Endpoint consumido | Propósito |
|---|---|---|---|
| `ms-estudiante` | `ms-matriculas` | `GET /api/v1/matriculas/estudiante/{id}` | Obtener matrículas |
| `ms-practicas` | `ms-aranceles` | `GET /api/v1/aranceles/estudiante/{id}/tiene-deuda-vencida` | Validar R5 |
| `ms-practicas` | `ms-notas` | `GET /api/v1/notas/estudiante/{id}/avance` | Validar R5 |
| `ms-practicas` | `ms-empresas` | `GET /api/v1/empresas/{id}/tiene-convenio-vigente` | Validar R4/R5 |

---

### FASE 1: Carreras (Puerto 8084)

#### POST Crear carrera
- **URL:** `http://localhost:8084/api/v1/carreras`
- **Request Body:**
```json
{
  "nombre": "Ingenieria en Sistemas",
  "descripcion": "Carrera de ingenieria",
  "duracionSemestres": 8,
  "sede": "Santiago"
}
```
- **Response:** Carrera creada con `id`, `nombre`, `descripcion`, `duracionSemestres`, `sede`.
- **Captura:** ![img_1.png](INFORME/img_1.png)

---

#### GET Listar carreras
- **URL:** `http://localhost:8084/api/v1/carreras`
- **Response:** Array con todas las carreras.
- **Captura:** ![img_2.png](INFORME/img_2.png)

---

#### GET Obtener carrera por ID
- **URL:** `http://localhost:8084/api/v1/carreras/{carreraId}`
- **Response:** Datos de la carrera específica.
- **Captura:** ![img_3.png](INFORME/img_3.png)

---

#### PUT Actualizar carrera
- **URL:** `http://localhost:8084/api/v1/carreras/{carreraId}`
- **Request Body:**
```json
{
  "nombre": "Ingenieria Actualizada",
  "descripcion": "Carrera actualizada",
  "duracionSemestres": 10,
  "sede": "Santiago"
}
```
- **Captura:** ![img_4.png](INFORME/img_4.png)

---

#### DELETE Eliminar carrera
- **URL:** `http://localhost:8084/api/v1/carreras/{carreraId}`
- **Response:** 204 No Content.
- **Captura:** ![img_5.png](INFORME/img_5.png)

---

### FASE 2: Asignaturas + Prerrequisitos (Puerto 8080)

#### POST Crear asignatura 1
- **URL:** `http://localhost:8080/api/v1/asignaturas`
- **Request Body:**
```json
{
  "nombre": "Programacion I",
  "creditos": 6
}
```
- **Captura:** ![img_6.png](INFORME/img_6.png)

---

#### POST Crear asignatura 2
- **URL:** `http://localhost:8080/api/v1/asignaturas`
- **Request Body:**
```json
{
  "nombre": "Programacion II",
  "creditos": 6
}
```
- **Captura:** ![img_7.png](INFORME/img_7.png)

---

#### GET Listar asignaturas
- **URL:** `http://localhost:8080/api/v1/asignaturas`
- **Captura:** ![img_8.png](INFORME/img_8.png)

---

#### GET Obtener prerrequisitos de una asignatura
- **URL:** `http://localhost:8080/api/v1/asignaturas/{asignatura2Id}/prerequisitos`
- **Captura:** ![img_9.png](INFORME/img_9.png)

---

#### POST Asignar prerrequisito
- **URL:** `http://localhost:8080/api/v1/prerequisitos`
- **Request Body:**
```json
{
  "asignaturaPrincipal": {"id": "{asignatura2Id}"},
  "asignaturaRequisito": {"id": "{asignatura1Id}"}
}
```
- **Captura:** ![img_10.png](INFORME/img_10.png)

---

#### GET Listar creditos
- **URL:** `http://localhost:8080/api/v1/creditos`
- **Captura:** ![img_11.png](INFORME/img_11.png)

---

### FASE 3: Docente + Especialidad + Contrato (Puerto 8082)

#### POST Crear especialidad
- **URL:** `http://localhost:8082/api/v1/especialidades`
- **Request Body:**
```json
{
  "nombre": "Especialidad Test",
  "descripcion": "Especialidad de prueba"
}
```
- **Captura:** ![img_12.png](INFORME/img_12.png)

---

#### GET Listar especialidades
- **URL:** `http://localhost:8082/api/v1/especialidades`
- **Captura:** ![img_13.png](INFORME/img_13.png)

---

#### POST Crear docente
- **URL:** `http://localhost:8082/api/v1/docentes`
- **Request Body:**
```json
{
  "nombre": "Maria",
  "apellido": "Gonzalez",
  "email": "maria@test.cl",
  "especialidadId": "{especialidadId}"
}
```
- **Captura:** ![img_14.png](INFORME/img_14.png)

---

#### GET Listar docentes
- **URL:** `http://localhost:8082/api/v1/docentes`
- **Captura:** ![img_15.png](INFORME/img_15.png)

---

#### GET Obtener docente por ID
- **URL:** `http://localhost:8082/api/v1/docentes/{docenteId}`
- **Captura:** ![img_16.png](INFORME/img_16.png)

---

#### POST Crear contrato
- **URL:** `http://localhost:8082/api/contratos`
- **Request Body:**
```json
{
  "tipoContrato": "Full Time",
  "fechaInicio": "2024-01-01",
  "fechaFin": "2024-12-31",
  "sueldoBase": 800000,
  "docente": {"id": "{docenteId}"}
}
```
- **Captura:** ![img_17.png](INFORME/img_17.png)

---

#### GET Listar contratos
- **URL:** `http://localhost:8082/api/contratos`
- **Captura:** ![img_18.png](INFORME/img_18.png)

---

### FASE 4: Empresas (Puerto 8089)

#### POST Crear empresa
- **URL:** `http://localhost:8089/api/v1/empresas`
- **Request Body:**
```json
{
  "nombre": "TechCorp",
  "rut": "12345678-9",
  "rubro": "Tecnologia"
}
```
- **Captura:** ![img_19.png](INFORME/img_19.png)

---

#### PUT Actualizar empresa (agregar convenio)
- **URL:** `http://localhost:8089/api/v1/empresas/{empresaId}`
- **Request Body:**
```json
{
  "nombre": "TechCorp",
  "rut": "12345678-9",
  "rubro": "Tecnologia",
  "fechaInicioConvenio": "2024-01-01",
  "fechaFinConvenio": "2028-12-31"
}
```
- **Captura:** ![img_20.png](INFORME/img_20.png)

---

#### GET Obtener empresa por ID
- **URL:** `http://localhost:8089/api/v1/empresas/{empresaId}`
- **Captura:** ![img_21.png](INFORME/img_21.png)

---

#### GET Verificar convenio vigente (R4)
- **URL:** `http://localhost:8089/api/v1/empresas/{empresaId}/tiene-convenio-vigente`
- **Captura:** ![img_22.png](INFORME/img_22.png)

---

#### GET Listar empresas
- **URL:** `http://localhost:8089/api/v1/empresas`
- **Captura:** ![img_23.png](INFORME/img_23.png)

---

### FASE 5: Estudiantes (Puerto 8085)

#### POST Crear estudiante
- **URL:** `http://localhost:8085/api/v1/estudiantes`
- **Request Body:**
```json
{
  "nombre": "Juan Perez",
  "rut": "12345678-9",
  "email": "juan@test.cl",
  "telefono": "+56912345678",
  "direccion": "Calle 123"
}
```
- **Captura:** ![img_24.png](INFORME/img_24.png)

---

#### GET Listar estudiantes
- **URL:** `http://localhost:8085/api/v1/estudiantes`
- **Captura:** ![img_25.png](INFORME/img_25.png)

---

#### GET Obtener estudiante por ID
- **URL:** `http://localhost:8085/api/v1/estudiantes/{estudianteId}`
- **Captura:** ![img_26.png](INFORME/img_26.png)

---

#### GET Obtener estudiante por RUT
- **URL:** `http://localhost:8085/api/v1/estudiantes/rut/{rut}`
- **Captura:** *(similar a img_26)*

---

#### GET Obtener detalle del estudiante
- **URL:** `http://localhost:8085/api/v1/estudiantes/{estudianteId}/detalle`
- **Captura:** *(no incluida)*

---

#### PUT Actualizar estudiante
- **URL:** `http://localhost:8085/api/v1/estudiantes/{estudianteId}`
- **Request Body:**
```json
{
  "nombre": "Juan Actualizado",
  "rut": "12345678-9",
  "email": "juan_act@test.cl",
  "telefono": "+56987654321",
  "direccion": "Nueva Calle 456"
}
```
- **Captura:** ![img_27.png](INFORME/img_27.png)

---

### FASE 6: Matrículas (Puerto 8083)

#### POST Crear matrícula (ACTIVA)
- **URL:** `http://localhost:8083/api/v1/matriculas`
- **Request Body:**
```json
{
  "estudianteId": "{estudianteId}",
  "seccionId": "{asignatura1Id}",
  "fechaMatricula": "2024-03-01",
  "estado": "ACTIVA"
}
```
- **Captura:** ![img_28.png](INFORME/img_28.png)

---

#### GET Obtener matrícula por ID
- **URL:** `http://localhost:8083/api/v1/matriculas/{matriculaId}`
- **Captura:** ![img_29.png](INFORME/img_29.png)

---

#### GET Listar matrículas
- **URL:** `http://localhost:8083/api/v1/matriculas`
- **Captura:** ![img_30.png](INFORME/img_30.png)

---

### FASE 7: Aranceles + Pagos (Puerto 8081)

#### POST Crear arancel
- **URL:** `http://localhost:8081/api/v1/aranceles`
- **Request Body:**
```json
{
  "estudianteId": "{estudianteId}",
  "concepto": "Matricula 2024-1",
  "monto": 150000,
  "fechaEmision": "2024-01-01",
  "fechaVencimiento": "2024-03-15"
}
```
- **Captura:** ![img_31.png](INFORME/img_31.png)

---

#### POST Pagar arancel
- **URL:** `http://localhost:8081/api/v1/aranceles/{arancelId}/pagar`
- **Captura:** ![img_32.png](INFORME/img_32.png)

---

#### GET Obtener aranceles por estudiante
- **URL:** `http://localhost:8081/api/v1/aranceles/estudiante/{estudianteId}`
- **Captura:** ![img_33.png](INFORME/img_33.png)

---

#### GET Verificar deuda vencida (R5)
- **URL:** `http://localhost:8081/api/v1/aranceles/estudiante/{estudianteId}/tiene-deuda-vencida`
- **Captura:** ![img_34.png](INFORME/img_34.png)

---

#### GET Verificar puede continuar (R5)
- **URL:** `http://localhost:8081/api/v1/aranceles/estudiante/{estudianteId}/puede-continuar`
- **Captura:** ![img_35.png](INFORME/img_35.png)

---

#### GET Listar aranceles
- **URL:** `http://localhost:8081/api/v1/aranceles`
- **Captura:** ![img_36.png](INFORME/img_36.png)

---

### FASE 8: Notas (Puerto 8086)

#### POST Crear nota
- **URL:** `http://localhost:8086/api/v1/notas`
- **Request Body:**
```json
{
  "estudianteId": "{estudianteId}",
  "seccionId": "{asignatura1Id}",
  "nota": 6.5,
  "tipo": "EXAMEN",
  "ponderacion": 0.30,
  "fecha": "2024-05-01"
}
```
- **Captura:** ![img_37.png](INFORME/img_37.png)

---

#### GET Obtener nota por ID
- **URL:** `http://localhost:8086/api/v1/notas/{notaId}`
- **Captura:** ![img_38.png](INFORME/img_38.png)

---

#### PUT Actualizar nota
- **URL:** `http://localhost:8086/api/v1/notas/{notaId}`
- **Request Body:**
```json
{
  "estudianteId": "{estudianteId}",
  "seccionId": "{asignatura1Id}",
  "nota": 7.0,
  "tipo": "EXAMEN",
  "ponderacion": 0.30,
  "fecha": "2024-05-15"
}
```
- **Captura:** ![img_39.png](INFORME/img_39.png)

---

#### GET Listar notas
- **URL:** `http://localhost:8086/api/v1/notas`
- **Captura:** ![img_40.png](INFORME/img_40.png)

---

#### GET Obtener promedio global (R3)
- **URL:** `http://localhost:8086/api/v1/notas/estudiante/{estudianteId}/promedio`
- **Captura:** ![img_41.png](INFORME/img_41.png)

---

#### GET Obtener promedio por sección
- **URL:** `http://localhost:8086/api/v1/notas/estudiante/{estudianteId}/promedio/seccion/{asignatura1Id}`
- **Captura:** ![img_42.png](INFORME/img_42.png)

---

#### GET Verificar avance 80% (R5)
- **URL:** `http://localhost:8086/api/v1/notas/estudiante/{estudianteId}/avance`
- **Captura:** ![img_43.png](INFORME/img_43.png)

---

### FASE 9: Asistencia (Puerto 8088)

#### POST Registrar asistencia (PRESENTE)
- **URL:** `http://localhost:8088/api/v1/asistencias`
- **Request Body:**
```json
{
  "estudianteId": "{estudianteId}",
  "seccionId": "{asignatura1Id}",
  "fecha": "2024-05-16",
  "tipo": "PRESENTE",
  "observacion": "Asistio normalmente"
}
```
- **Response:** Estructura: `{ asistencia: { id }, resumenR2: {...} }`
- **Captura:** ![img_44.png](INFORME/img_44.png)

---

#### GET Obtener asistencia por ID
- **URL:** `http://localhost:8088/api/v1/asistencias/{asistenciaId}`
- **Captura:** ![img_45.png](INFORME/img_45.png)

---

#### PUT Actualizar asistencia
- **URL:** `http://localhost:8088/api/v1/asistencias/{asistenciaId}`
- **Request Body:**
```json
{
  "estudianteId": "{estudianteId}",
  "seccionId": "{asignatura1Id}",
  "fecha": "2024-05-16",
  "tipo": "JUSTIFICADO",
  "observacion": "Justificado"
}
```
- **Captura:** ![img_46.png](INFORME/img_46.png)

---

#### GET Resumen de asistencia (R2)
- **URL:** `http://localhost:8088/api/v1/asistencias/estudiante/{estudianteId}/seccion/{asignatura1Id}/resumen`
- **Captura:** ![img_47.png](INFORME/img_47.png)

---

#### GET Asistencias por sección
- **URL:** `http://localhost:8088/api/v1/asistencias/seccion/{asignatura1Id}`
- **Captura:** ![img_48.png](INFORME/img_48.png)

---

#### GET Asistencias por estudiante
- **URL:** `http://localhost:8088/api/v1/asistencias/estudiante/{estudianteId}`
- **Captura:** ![img_49.png](INFORME/img_49.png)

---

### FASE 10: Prácticas (Puerto 8087)

#### GET Verificar requisitos R5
- **URL:** `http://localhost:8087/api/v1/practicas/verificar?estudianteId={estudianteId}&empresaId={empresaId}`
- **Captura:** ![img_50.png](INFORME/img_50.png)

---

#### POST Crear práctica
- **URL:** `http://localhost:8087/api/v1/practicas`
- **Request Body:**
```json
{
  "estudianteId": "{estudianteId}",
  "empresaId": "{empresaId}",
  "supervisorNombre": "Carlos Lopez",
  "fechaInicio": "2024-06-01"
}
```
- **Captura:** ![img_51.png](INFORME/img_51.png)

---

#### GET Obtener práctica por ID
- **URL:** `http://localhost:8087/api/v1/practicas/{practicaId}`
- **Captura:** ![img_52.png](INFORME/img_52.png)

---

#### PUT Finalizar práctica
- **URL:** `http://localhost:8087/api/v1/practicas/{practicaId}/finalizar`
- **Request Body:**
```json
{
  "fechaFin": "2024-12-01",
  "estado": "REPROBADA",
  "observaciones": "No cumplio objetivos"
}
```
- **Captura:** ![img_53.png](INFORME/img_53.png)

---

#### GET Listar prácticas
- **URL:** `http://localhost:8087/api/v1/practicas`
- **Captura:** ![img_54.png](INFORME/img_54.png)

---

### FASE 11: Eliminación

> **Importante:** Eliminar en orden correcto para evitar errores de constraints/cascada.

#### DELETE Eliminar arancel
- **URL:** `http://localhost:8081/api/v1/aranceles/{arancelId}`
- **Response:** 204 No Content
- **Captura:** ![img_55.png](INFORME/img_55.png)

---

#### DELETE Eliminar nota
- **URL:** `http://localhost:8086/api/v1/notas/{notaId}`
- **Response:** 204 No Content
- **Captura:** ![img_56.png](INFORME/img_56.png)

---

#### DELETE Eliminar asistencia
- **URL:** `http://localhost:8088/api/v1/asistencias/{asistenciaId}`
- **Response:** 204 No Content
- **Captura:** ![img_57.png](INFORME/img_57.png)

---

#### DELETE Eliminar práctica
- **URL:** `http://localhost:8087/api/v1/practicas/{practicaId}`
- **Response:** 204 No Content
- **Nota:** Solo funciona si la práctica está finalizada (APROBADA/REPROBADA)
- **Captura:** ![img_58.png](INFORME/img_58.png)

---

#### DELETE Eliminar matrícula
- **URL:** `http://localhost:8083/api/v1/matriculas/{matriculaId}`
- **Response:** 204 No Content
- **Captura:** ![img_59.png](INFORME/img_59.png)

---

#### DELETE Eliminar estudiante
- **URL:** `http://localhost:8085/api/v1/estudiantes/{estudianteId}`
- **Response:** 204 No Content
- **Captura:** ![img_60.png](INFORME/img_60.png)

---

#### DELETE Eliminar empresa
- **URL:** `http://localhost:8089/api/v1/empresas/{empresaId}`
- **Response:** 204 No Content
- **Captura:** ![img_61.png](INFORME/img_61.png)

---

#### DELETE Eliminar docente
- **URL:** `http://localhost:8082/api/v1/docentes/{docenteId}`
- **Response:** 204 No Content
- **Captura:** ![img_62.png](INFORME/img_62.png)

---

#### DELETE Eliminar asignatura 2 (PRIMERO)
- **URL:** `http://localhost:8080/api/v1/asignaturas/{asignatura2Id}`
- **Response:** 204 No Content
- **Nota:** Eliminar primero porque tiene el prerrequisito hacia asignatura1
- **Captura:** ![img_63.png](INFORME/img_63.png)

---

#### DELETE Eliminar asignatura 1 (SEGUNDO)
- **URL:** `http://localhost:8080/api/v1/asignaturas/{asignatura1Id}`
- **Response:** 204 No Content
- **Captura:** ![img_64.png](INFORME/img_64.png)

---

#### DELETE Eliminar carrera
- **URL:** `http://localhost:8084/api/v1/carreras/{carreraId}`
- **Response:** 204 No Content
- **Captura:** *(similar a img_5)*

---

## Notas importantes

1. **Orden de eliminación:** Siempre eliminar entidades dependientes primero (arancel, nota, asistencia, practica, matricula) antes de las principales (estudiante, empresa, docente, asignatura, carrera).

2. **Prerrequisitos de práctica (R5):** Para crear una práctica, el estudiante debe:
   - No tener deudas vencidas
   - Tener avance >= 80% en notas
   - La empresa debe tener convenio vigente

3. **Estados de práctica:** Usar "REPROBADA" o "APROBADA" al finalizar para poder eliminar la práctica posteriormente.

4. **IDs dinámicos:** En Postman, los IDs se capturan automáticamente desde las respuestas de los POST y se almacenan en variables de entorno para usarse en requests subsiguientes.

5. **Colección Postman:** Se recomienda usar `InstitutoPacifico.postman_collection.json` con Collection Runner para ejecutar todos los endpoints en orden.

---

## Códigos HTTP y formato de errores

```json
{
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "R5 violada: el estudiante tiene deuda vencida",
  "path": "/api/v1/practicas",
  "timestamp": "2026-05-17T10:32:18.4521",
  "detalles": null
}
```

| Código | Cuándo |
|---|---|
| `200 OK` | GET / PUT / PATCH exitoso |
| `201 Created` | POST exitoso |
| `204 No Content` | DELETE exitoso |
| `400 Bad Request` | Bean Validation fallida |
| `404 Not Found` | Recurso no encontrado |
| `422 Unprocessable Entity` | Regla de negocio violada (R1-R5) |
| `500 Internal Server Error` | Excepción inesperada |

---

## Licencia y créditos

DuocUC — 2026.
Caso de negocio: **Instituto Pacifico** (caso ficticio para fines docentes).
