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
| 1 | ms-estudiantes| --  | Registro y perfil de estudiantes. |
| 2 | ms-docentes   | --   | Docentes, especialidades y contratos. |
| 3 | ms-carreras   | --   | Carreras, mallas curriculares y sedes. |
| 4 | ms-asignaturas| --   | Asignaturas, prerrequisitos y créditos. |

## Tecnologías Utilizadas
- Java 21 / Spring Boot 3.x
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
