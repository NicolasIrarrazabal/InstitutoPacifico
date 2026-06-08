# Documento de Especificación - Instituto Pacifico

---

## Portada

**Nombre oficial del proyecto:** Sistema de Gestión Académica - Instituto Pacifico

---

## 1. Introducción

El Sistema de Gestión Académica del Instituto Pacifico es una plataforma backend basada en una arquitectura de microservicios, diseñada para digitalizar y automatizar los procesos administrativos y académicos de la institución educativa ubicada en Puerto Montt, Chile. El sistema se compone de diez microservicios independientes desarrollados en Java 21 con Spring Boot 4.0.5, cada uno responsable de un dominio de negocio específico como estudiantes, asignaturas, matrículas, notas, asistencia, aranceles y prácticas profesionales.

El propósito principal del sistema es eliminar los procesos manuales y las discrepancias de datos asociadas al registro académico tradicional, proporcionando una solución integral basada en API REST que permite la gestión de carreras, docentes, estudiantes, matrículas, calificaciones, control de asistencia, administración de aranceles y la validación de requisitos para prácticas profesionales. La comunicación entre servicios se realiza mediante llamadas HTTP síncronas vía RestTemplate, y cada microservicio cuenta con su propia base de datos PostgreSQL gestionada mediante migraciones Flyway.

---

## 2. Objetivos

### Objetivo General

Desarrollar un sistema de microservicios para la gestión académica del Instituto Pacifico que automatice los procesos administrativos, garantice la integridad de los datos y permita la validación cruzada de requisitos entre dominios académicos.

### Objetivos Específicos

1. **Implementar la gestión de catálogo académico** — Desarrollar los microservicios de carreras, asignaturas y docentes para administrar la oferta académica y el plantel docente mediante APIs REST.
2. **Automatizar los procesos de admisión y control académico** — Implementar los microservicios de estudiantes y matrículas que permitan el registro y la inscripción de alumnos en las carreras ofrecidas.
3. **Garantizar el cumplimiento de reglas de negocio transversales** — Integrar los microservicios de notas, asistencia y aranceles para validar las cinco reglas de negocio (R1-R5) que determinan la elegibilidad de los estudiantes para prácticas profesionales.
4. **Facilitar la gestión de convenios y prácticas empresariales** — Implementar los microservicios de empresas y prácticas que permitan administrar convenios vigentes y validar automáticamente los requisitos de postulación a prácticas.

---

## 3.1 Problemática

El Instituto Pacifico enfrenta ineficiencias operativas derivadas de procesos manuales y sistemas de registro descentralizados para la gestión académica. La información de estudiantes, matrículas, notas, asistencia y aranceles se maneja de forma independiente, lo que genera discrepancias de datos, errores humanos y retrasos en la validación de requisitos críticos como la elegibilidad para prácticas profesionales. No existe un mecanismo automatizado que integre y valide de manera transversal las condiciones académicas (nota mínima de 5.0), de asistencia (mínimo 75%) y financieras (sin deudas pendientes) que los estudiantes deben cumplir para acceder a las prácticas profesionales, lo que obliga a revisiones manuales propensas a errores e inconsistencias.

---

## 4. Alcance del proyecto

### Incluido

- Gestión de carreras y su disponibilidad (R1)
- Administración de asignaturas con prerrequisitos y créditos
- Registro y administración de docentes con especialidades y contratos
- Gestión de estudiantes y sus datos personales
- Proceso de matrícula de estudiantes en carreras
- Registro y cálculo de notas, promedios y avance académico (R3)
- Control de asistencia y cálculo de porcentaje mínimo (R2)
- Administración de aranceles, pagos y validación de deudas (R5)
- Gestión de empresas y validación de convenios vigentes (R4)
- Proceso de postulación y validación de requisitos para prácticas profesionales (R5)
- Comunicación síncrona entre microservicios mediante RestTemplate
- Validación de reglas de negocio (R1-R5) distribuidas entre servicios

### Excluido

- Interfaz de usuario o frontend web/móvil
- API Gateway o punto de entrada único
- Descubrimiento de servicios (Service Discovery)
- Autenticación y autorización de usuarios
- Sistema de notificaciones (email, SMS)
- Panel de administración o dashboard
- Reportería avanzada o business intelligence
- Integración con sistemas externos (bancos, plataformas gubernamentales)
- Despliegue en infraestructura cloud
- Balanceo de carga o alta disponibilidad

---

## 4.1 Requisitos Funcionales

| Código | Nombre del requisito | Descripción |
|--------|----------------------|-------------|
| RF-01 | Gestión de carreras | Permitir CRUD de carreras, incluyendo activación/desactivación y consulta de disponibilidad (R1) |
| RF-02 | Gestión de asignaturas | Permitir CRUD de asignaturas con prerrequisitos, créditos y horas |
| RF-03 | Gestión de docentes | Permitir CRUD de docentes con especialidades y contratos |
| RF-04 | Gestión de estudiantes | Permitir CRUD de estudiantes y consulta de datos personales |
| RF-05 | Gestión de matrículas | Permitir registrar matrículas de estudiantes en carreras y consultar matrículas por estudiante |
| RF-06 | Gestión de notas | Permitir registro de notas, cálculo de promedios y determinación de avance académico >= 80% (R3) |
| RF-07 | Control de asistencia | Permitir registro de asistencia y cálculo de porcentaje de asistencia por estudiante/sección (R2) |
| RF-08 | Gestión de aranceles | Permitir administración de aranceles, registro de pagos y validación de deudas pendientes (R5) |
| RF-09 | Gestión de empresas | Permitir CRUD de empresas y validación de convenios vigentes (R4) |
| RF-10 | Gestión de prácticas | Permitir postulación a prácticas con validación cruzada de requisitos: deuda, avance académico >= 80% y convenio vigente (R5) |
| RF-11 | Validación nota mínima | Validar que el estudiante tenga un promedio global >= 5.0 como requisito de egreso (R3) |
| RF-12 | Validación asistencia mínima | Validar que el estudiante tenga al menos 75% de asistencia (R2) |
| RF-13 | Consulta de disponibilidad de carrera | Validar que una carrera esté disponible para matrícula (R1) |
| RF-14 | Comunicación entre servicios | Implementar llamadas HTTP síncronas entre microservicios mediante RestTemplate |

---

## 4.2 Requisitos No Funcionales

| Código | Nombre del requisito | Descripción |
|--------|----------------------|-------------|
| RNF-01 | Arquitectura de microservicios | El sistema debe implementar una arquitectura de microservicios donde cada dominio de negocio sea un servicio independiente con su propia base de datos y ciclo de vida |
| RNF-02 | Tiempo de respuesta | Las APIs REST deben responder en menos de 2 segundos para consultas simples y menos de 5 segundos para operaciones con validación cruzada entre servicios |
| RNF-03 | Disponibilidad | El sistema debe estar disponible en horario académico (08:00 - 22:00) con un uptime mínimo del 99% |
| RNF-04 | Persistencia de datos | Cada microservicio debe utilizar PostgreSQL como base de datos y gestionar los esquemas mediante migraciones Flyway |
| RNF-05 | Consistencia de datos | Las operaciones de validación cruzada entre servicios deben verificar la consistencia de los datos antes de aceptar o rechazar una operación |
| RNF-06 | Mantenibilidad | Cada microservicio debe seguir la arquitectura CSR (Controller-Service-Repository) con separación clara de capas y responsabilidades |
| RNF-07 | Escalabilidad | Los microservicios deben poder ejecutarse de forma independiente en puertos diferentes, permitiendo el escalado horizontal por servicio |
| RNF-08 | Seguridad | Las contraseñas y credenciales de base de datos deben configurarse mediante variables de entorno, sin valores hardcodeados en el código fuente |
| RNF-09 | Documentación de API | Todos los endpoints REST deben estar documentados y ser testeables mediante herramientas como Postman |
| RNF-10 | Portabilidad | El sistema debe ejecutarse en cualquier sistema operativo con Java 21+ y Maven 3.9+, utilizando variables de entorno para la configuración específica del entorno |
