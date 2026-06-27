# PLAN DE ACCIÓN — Máxima Nota EP3

## Pendientes críticos

| # | Tarea | Dónde | Estado |
|---|---|---|---|
| 1 | **Pruebas unitarias + JaCoCo ≥ 80%** en los 7 MS que faltan | `ms-asignaturas`, `ms-aranceles`, `ms-docente`, `ms-matriculas`, `ms-notas`, `ms-practicas`, `ms-asistencia` | ❌ Pendiente |
| 2 | **Tablero ClickUp** — tareas, roles asignados, avance visible | ClickUp | ❌ Pendiente |
| 3 | **Informe profesional** — PDF con diagramas, trazabilidad, estructura formal | `INFORME/` | ⚠️ Solo capturas PNG |
| 4 | **GitHub Flow** — PRs y merges visibles desde ramas feature | GitHub | ⚠️ Mejorar |
| 5 | **Commits distribuidos** — Verificar equilibrio entre integrantes | GitHub | ⚠️ Revisar |

## Detalle por tarea

### 1. Pruebas unitarias
- Revisar `src/test/java/` de cada MS
- Implementar tests con JUnit + Mockito (Given–When–Then)
- Ejecutar `mvn verify` para generar reporte JaCoCo
- Confirmar cobertura ≥ 80% línea

### 2. ClickUp
- Crear tablero con columnas: Pendiente / En Progreso / Hecho
- Asignar tareas por integrante
- Agregar captura del tablero al informe o README

### 3. Informe
- Usar plantilla docente o estructura formal:
  - Portada, Introducción, Arquitectura, Diagramas, Endpoints, Despliegue, Conclusiones
- Incluir diagramas de componentes y secuencia

### 4. GitHub Flow
- Crear PRs desde ramas `feature/*` → `master`
- Hacer merge con `--no-ff` para mantener historial limpio

### 5. Commits
- Verificar `git shortlog -sn` para ver distribución
- Balancear commits si es necesario
