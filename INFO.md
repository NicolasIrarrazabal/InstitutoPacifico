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
| 1 | ms-asignaturas | 8080   | Asignaturas, prerrequisitos y créditos. |
| 2 | ms-docentes   | 8082   | Docentes, especialidades y contratos. |
| 3 | ms-matriculas | 8083   | Gestión de matrículas. |
| 4 | ms-carreras   | 8084   | Carreras, mallas curriculares y sedes. |
| 5 | ms-estudiantes| 8085   | Registro y perfil de estudiantes. |

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
