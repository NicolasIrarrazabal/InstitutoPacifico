# Sistema de Gestion Academica - Instituto Pacifico

## Comandos de Gestion de Repositorio

### Actualizacion y Sincronizacion
Para asegurar que el entorno local se encuentre alineado con el repositorio remoto, se deben utilizar los siguientes comandos:

* **Sincronizacion con Master**: `git pull origin master --rebase`
    (Atrae los cambios de la rama principal evitando commits de merge innecesarios).
* **Creacion de Rama de Integracion**: `git checkout -b develop`
    (Rama principal de desarrollo donde se integran las funcionalidades antes de pasar a produccion).

### Gestion de Ramas por Funcionalidad
Cada microservicio debe desarrollarse en su propia rama para mantener la independencia del codigo:

* `git checkout feature/ms-asignaturas`
* `git checkout feature/ms-docente`
* `git checkout feature/ms-estudiante`
* `git checkout feature/ms-matriculas`

---

## Descripcion General
Este repositorio contiene la arquitectura base de los microservicios que integran la plataforma de digitalizacion del Instituto Pacifico. El sistema utiliza Spring Boot 3.x para automatizar los procesos criticos de registro estudiantil, gestion de personal docente, estructura curricular y el flujo de matricula, resolviendo la problematica de registros manuales y falta de trazabilidad detectada en auditorias previas.

## Requisitos Tecnicos Generales
Para el cumplimiento de los estandares de la Evaluacion Parcial 2 (DSY1103), todos los microservicios implementados adhieren a las siguientes especificaciones:

* **Persistencia Independiente**: Cada microservicio posee su propia base de datos, garantizando el aislamiento de datos y la escalabilidad.
* **Validacion de Datos**: Implementacion de Bean Validation (@NotNull, @Size, @Min, @Max) en los Objetos de Transferencia de Datos (DTOs).
* **Gestion de Excepciones**: Uso de @ControllerAdvice para capturar y procesar excepciones de forma centralizada.
* **Respuestas HTTP Estructuradas**: Retorno de errores en formato JSON con codigos de estado estandarizados (400, 404, 409, 422).
* **Trazabilidad**: Implementacion de registros mediante SLF4J para el monitoreo de operaciones criticas y auditoria de cambios.
* **Comunicacion Inter-servicios**: Implementacion de llamadas sincronas mediante OpenFeign o WebClient para la validacion de reglas de negocio complejas.

## Estructura de Microservicios (Fase 1)

### 1. ms-estudiantes
**Responsabilidad:** Gestion del ciclo de vida y perfil del alumnado.
* **Entidades:** Estudiante, Contacto, Estado.
* **Dependencias Clave:**
    * Spring Data JPA (Persistencia)
    * Validation (Validacion de entradas)
    * MapStruct (Mapeo optimizado entre Entidades y DTOs)
    * SpringDoc OpenAPI (Documentacion Swagger para pruebas de endpoints)

### 2. ms-docentes
**Responsabilidad:** Administracion del personal academico y sus especialidades.
* **Entidades:** Docente, Especialidad, Contrato.
* **Dependencias Clave:**
    * Spring Data JPA
    * Lombok (Reduccion de codigo repetitivo)
    * MySQL/PostgreSQL Connector
    * Spring Boot Actuator (Monitoreo del estado del servicio)

### 3. ms-carreras
**Responsabilidad:** Definicion de la oferta academica y gestion de sedes regionales.
* **Entidades:** Carrera, Malla, Sede.
* **Dependencias Clave:**
    * Spring Data JPA
    * Hibernate Envers (Para auditoria y trazabilidad de cambios en mallas curriculares)
    * Spring Web

### 4. ms-asignaturas
**Responsabilidad:** Control del catalogo de asignaturas y jerarquia de prerrequisitos.
* **Entidades:** Asignatura, Prerequisito, Creditos.
* **Dependencias Clave:**
    * Spring Data JPA
    * Jackson Dataformat (Manejo eficiente de estructuras jerarquicas/recursivas de prerrequisitos)
    * Validation

### 5. ms-matriculas
**Responsabilidad:** Orquestacion del proceso de inscripcion y validacion de Regla de Negocio R1.
* **Entidades:** Matricula, Seccion, Estudiante.
* **Lógica de Negocio (Regla R1):** El servicio bloquea la inscripcion si no se cumplen los prerrequisitos, consultando al ms-asignaturas.
* **Dependencias de Alto Nivel:**
    * Spring Cloud OpenFeign (Comunicacion declarativa entre servicios)
    * Resilience4j (Circuit Breaker para manejar fallos en llamadas a otros microservicios)
    * Spring Web

---

## Estructura del Codigo Fuente
Cada modulo sigue un patron de diseño por capas estrictamente separado para maximizar la cohesividad:

1.  **Controller**: Gestion de endpoints REST y mapeo de solicitudes.
2.  **Service**: Implementacion de la logica de negocio y validacion de reglas obligatorias.
3.  **Repository**: Abstraccion de acceso a datos mediante Spring Data JPA.
4.  **Model**: Definicion de entidades persistentes y relaciones.
5.  **DTO**: Objetos de transferencia de datos con anotaciones de validacion.
6.  **Exception**: Configuracion de manejadores globales de errores.

## Instrucciones de Despliegue
1.  Configurar las variables de entorno para las bases de datos independientes de cada servicio.
2.  Asegurar que el entorno cuente con Java 21 y Maven instalado.
3.  Ejecutar cada servicio de manera independiente respetando la asignacion de puertos en los archivos `application.properties`.
4.  Verificar la visibilidad de red entre servicios para habilitar las llamadas inter-microservicio.
