# PLAN DE ACCIÓN — Máxima Nota EP3

## Logrado ✅

| # | Tarea | Detalle |
|---|---|---|
| 1 | **Render** | `ms-carreras` desplegado en [institutopacifico-ms-carreras.onrender.com](https://institutopacifico-ms-carreras.onrender.com) con PostgreSQL. Health check OK, Swagger OK. |
| 2 | **README** | Agregada columna Render URL en tabla de MS + sección "Despliegue" con instrucciones. |
| 3 | **GitHub Flow** | Rama `feature/render-deploy` creada y pusheada. Pendiente: crear PR y mergear desde GitHub web. |
| 4 | **ms-carreras fixes** | Eliminado `@PropertySource(.env)`, agregado `spring-boot-starter-actuator`, optimizado Dockerfile con capa de caché Maven. |
| 5 | **.env** | Creado con credenciales de Render PostgreSQL para referencia local. |

## Pendientes críticos

| # | Tarea | Dónde | Estado | Peso |
|---|---|---|---|---|
| 1 | **Pruebas unitarias + JaCoCo ≥ 80%** en los 7 MS que faltan | `ms-asignaturas`, `ms-aranceles`, `ms-docente`, `ms-matriculas`, `ms-notas`, `ms-practicas`, `ms-asistencia` | ❌ Pendiente | **8%** |
| 2 | **Tablero ClickUp** — tareas, roles asignados, avance visible | ClickUp | ❌ Pendiente | 2% |
| 3 | **Informe profesional** — PDF con diagramas, trazabilidad, estructura formal | `INFORME/` | ⚠️ Solo capturas PNG | Checklist |
| 4 | **PR + merge** de `feature/render-deploy` → `master` | GitHub | ⚠️ Pendiente | Checklist |
| 5 | **Commits distribuidos** — Verificar equilibrio entre integrantes | GitHub | ⚠️ Revisar | 3% |

## Detalle por tarea

### 1. Pruebas unitarias (prioridad máxima — 8%)
- Revisar `src/test/java/` de cada MS
- Implementar tests con JUnit + Mockito (Given–When–Then)
- Ejecutar `mvn verify` para generar reporte JaCoCo
- Confirmar cobertura ≥ 80% línea

### 2. ClickUp
- Ir a [ClickUp](https://app.clickup.com) y crear Workspace
- Crear Lista con columnas: Pendiente / En Progreso / Hecho
- Agregar tareas (cada punto de este plan) y asignar responsable
- Agregar captura del tablero al informe o README

### 3. Informe profesional
- Usar plantilla docente o estructura formal:
  - Portada, Introducción, Arquitectura, Diagramas, Endpoints, Despliegue, Conclusiones
- Incluir diagramas de componentes y secuencia
- Agregar capturas de Swagger, health check, endpoints funcionales

### 4. GitHub Flow — PR final
- Ir a: https://github.com/NicolasIrarrazabal/InstitutoPacifico/pull/new/feature/render-deploy
- Crear PR con descripción de los cambios
- Mergear a `master`

### 5. Commits distribuidos
- Revisar con: `git shortlog -sn`
- Si hay desbalance, los integrantes con menos commits deben hacer los próximos cambios (tests, informe, etc.)
