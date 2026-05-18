# Instituto Pacífico — DSY1103 Desarrollo FullStack 1

## Descripción
Sistema de gestión académica para el Instituto Pacífico (Puerto Montt), diseñado para digitalizar y automatizar el registro estudiantil, la carga docente, el catálogo de carreras y las asignaturas, eliminando discrepancias de datos y registros manuales.

## Equipo
| Nombre | GitHub |
|--------|--------|
| [ Vicente Herrera ]| @cafesincuchara |
| [ Nicolas Irarrazabal ] | @NicolasIrarrazabal |

## Microservicios Implementados
| # | Microservicio | Puerto | Descripción |
|---|---------------|--------|-------------|
| 1 | ms-asignaturas | 8080 | Asignaturas, prerrequisitos y créditos. |
| 2 | ms-aranceles | 8081 | Aranceles, pagos y validación de deuda. |
| 3 | ms-docente | 8082 | Docentes, especialidades y contratos. |
| 4 | ms-matriculas | 8083 | Gestión de matrículas. |
| 5 | ms-carreras | 8084 | Carreras, mallas curriculares y sedes. |
| 6 | ms-estudiante | 8085 | Registro y perfil de estudiantes. |
| 7 | ms-notas | 8086 | Notas, promedios y verificación de avance. |
| 8 | ms-practicas | 8087 | Prácticas profesionales y verificación de requisitos. |
| 9 | ms-asistencia | 8088 | Asistencia y cálculo de porcentaje mínimo. |
| 10 | ms-empresas | 8089 | Empresas con convenio y verificación de vigencia. |


## Tecnologías Utilizadas
- Java 21 / Spring Boot 4.0.5
- JPA + Hibernate
- PostgreSQL
- WebClient / Feign Client
- SLF4J para logs

## Cómo Ejecutar el Proyecto
1. Clonar el repositorio: `git clone [URL]`
2. Configurar la base de datos en `application.properties`
3. Ejecutar cada microservicio: `./mvnw spring-boot:run`

## Estado del Proyecto
🔄 En desarrollo — EP2 2025
