

## Descripción General
Este repositorio contiene los primeros cinco microservicios que integran la plataforma de digitalización del Instituto Pacífico. El sistema ha sido diseñado bajo una arquitectura de microservicios utilizando Spring Boot 3.x, con el objetivo de centralizar y automatizar los procesos de registro de estudiantes, gestión docente, estructura curricular y procesos de matrícula.

## Requisitos Técnicos Generales
Para el cumplimiento de los estándares de la Evaluación Parcial 2 (DSY1103), todos los microservicios implementados en este proyecto adhieren a las siguientes especificaciones:

* **Persistencia Independiente**: Cada microservicio posee su propia base de datos, garantizando el aislamiento de datos.
* **Validación de Datos**: Implementación de Bean Validation (`@NotNull`, `@Size`, `@Min`, `@Max`) en los Objetos de Transferencia de Datos (DTOs).
* **Gestión de Excepciones**: Uso de `@ControllerAdvice` para capturar y procesar excepciones como `EntityNotFoundException` y `ConstraintViolationException`.
* **Respuestas HTTP Estructuradas**: Retorno de errores en formato JSON con códigos de estado adecuados (400, 404, 409, 422).
* **Trazabilidad**: Implementación de registros mediante SLF4J en operaciones críticas (creación, modificación y errores de negocio).
* **Comunicación Inter-servicios**: Uso de `WebClient` o `Feign Client` para la validación de reglas de negocio que requieren datos de otros dominios.

## Estructura de Microservicios (Fase 1)

### 1. ms-estudiantes
**Responsabilidad:** Gestión del ciclo de vida y perfil del alumnado.
* **Entidades Principales:** Estudiante, Contacto, Estado.
* **Funcionalidades:**
    * Registro de datos personales y académicos.
    * Actualización de información de contacto.
    * Gestión de estados de vigencia estudiantil.
* **Endpoints Principales:**
    * `GET /api/v1/estudiantes/`: Listado con paginación.
    * `POST /api/v1/estudiantes/`: Creación con validación de DTO.
    * `GET /api/v1/estudiantes/{id}/detalle`: Consulta enriquecida con información de matrícula activa.

### 2. ms-docentes
**Responsabilidad:** Administración del personal académico y sus especialidades.
* **Entidades Principales:** Docente, Especialidad, Contrato.
* **Funcionalidades:**
    * Mantenimiento de perfiles docentes.
    * Asignación de áreas de especialización por carrera.
    * Seguimiento administrativo de contratos vigentes.
* **Endpoints Principales:**
    * `GET /api/v1/docentes/{id}`: Detalle del docente.
    * `PUT /api/v1/docentes/{id}`: Actualización de contrato o especialidad.

### 3. ms-carreras
**Responsabilidad:** Definición de la oferta académica y estructura de sedes.
* **Entidades Principales:** Carrera, Malla, Sede.
* **Funcionalidades:**
    * Gestión de las 12 carreras técnicas de nivel superior.
    * Definición de mallas curriculares y semestres.
    * Asignación de carreras por sede (Puerto Montt, Puerto Varas, Osorno).

### 4. ms-asignaturas
**Responsabilidad:** Control del catálogo de asignaturas y sus dependencias académicas.
* **Entidades Principales:** Asignatura, Prerequisito, Créditos.
* **Funcionalidades:**
    * Definición de contenidos y carga de créditos.
    * Gestión de la jerarquía de prerrequisitos necesaria para el flujo académico.
* **Comunicación Externa:** Provee datos críticos al servicio de matrículas para la validación de avance académico.

### 5. ms-matriculas
**Responsabilidad:** Orquestación del proceso de inscripción de estudiantes en secciones académicas.
* **Entidades Principales:** Matricula, Seccion, Estudiante.
* **Lógica de Negocio (Regla R1):** * El servicio implementa una validación obligatoria donde no se permite la inscripción si el estudiante no cuenta con los prerrequisitos aprobados.
    * Realiza llamadas sincrónicas al `ms-asignaturas` para verificar la malla y al `ms-notas` (Fase 2) para validar el historial académico.
* **Endpoints Principales:**
    * `POST /api/v1/matriculas/`: Inicia el proceso de inscripción validando disponibilidad de cupos y prerrequisitos.

---

## Estructura del Código Fuente
Cada microservicio sigue el patrón de diseño por capas:
1.  **Controller**: Definición de endpoints REST y manejo de peticiones.
2.  **Service**: Implementación de la lógica de negocio y reglas obligatorias.
3.  **Repository**: Interfaz de acceso a datos utilizando Spring Data JPA.
4.  **Model**: Definición de entidades JPA y mapeo relacional.
5.  **DTO**: Objetos para la transferencia de datos con anotaciones de validación.
6.  **Exception**: Manejadores de errores personalizados.

## Instrucciones de Despliegue
1.  Configurar las variables de entorno para las bases de datos individuales.
2.  Ejecutar cada servicio de manera independiente (puertos configurados en `application.properties`).
3.  Asegurar la visibilidad de red entre servicios para el funcionamiento de `WebClient/Feign`.
"""

with open("README.md", "w", encoding="utf-8") as f:
    f.write(readme_content)

print("README.md file generated successfully.")
