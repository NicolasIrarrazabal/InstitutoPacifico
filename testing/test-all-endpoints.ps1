#!/usr/bin/env pwsh

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Instituto Pacifico - Test de Endpoints" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$baseUrl = "http://localhost"
$runTag = Get-Date -Format "HHmmssfff"
$rutNum = ("1" + $runTag).Substring(0,8)
$rutFull = "$rutNum-9"

function Test-Endpoint {
    param($name, $method, $url, $body = $null, $saveIdAs = $null, $idExtractor = $null)
    
    try {
        $params = @{
            Uri = $url
            Method = $method
            ContentType = "application/json"
            TimeoutSec = 15
        }
        if ($body) {
            $params.Body = $body
        }
        
        $response = Invoke-RestMethod @params -ErrorAction Stop
        
        if ($saveIdAs) {
            if ($idExtractor) {
                $id = & $idExtractor $response
            } else {
                $id = $response.id
            }
            if ($id) {
                Set-Variable -Name $saveIdAs -Value $id -Scope Script
            }
        }
        
        Write-Host "[OK]   " -ForegroundColor Green -NoNewline
        Write-Host "$method $name" -ForegroundColor White
        return $true
    }
    catch {
        $statusCode = if ($_.Exception.Response) { 
            [int]$_.Exception.Response.StatusCode 
        } else { 
            "ERR" 
        }
        Write-Host "[FAIL]" -ForegroundColor Red -NoNewline
        Write-Host " $method $name - $statusCode" -ForegroundColor Yellow
        return $false
    }
}

function Test-Endpoint-Simple {
    param($name, $method, $url, $body = $null)
    
    try {
        $params = @{
            Uri = $url
            Method = $method
            ContentType = "application/json"
            TimeoutSec = 15
        }
        if ($body) {
            $params.Body = $body
        }
        
        $null = Invoke-RestMethod @params -ErrorAction Stop
        Write-Host "[OK]   " -ForegroundColor Green -NoNewline
        Write-Host "$method $name" -ForegroundColor White
        return $true
    }
    catch {
        $statusCode = if ($_.Exception.Response) { 
            [int]$_.Exception.Response.StatusCode 
        } else { 
            "ERR" 
        }
        Write-Host "[FAIL]" -ForegroundColor Red -NoNewline
        Write-Host " $method $name - $statusCode" -ForegroundColor Yellow
        return $false
    }
}

function ExtractNestedId($response) {
    if ($response.asistencia -and $response.asistencia.id) {
        return $response.asistencia.id
    }
    return $response.id
}

$total = 0
$exitos = 0
$fallidos = 0

Write-Host "Run tag: $runTag | RUT: $rutFull" -ForegroundColor DarkGray

Write-Host "`n=== FASE 1: Crear carrera ===" -ForegroundColor Yellow
$body = '{"nombre":"Carrera __TAG__","descripcion":"Carrera de prueba","duracionSemestres":8,"sede":"Santiago"}' -replace '__TAG__', $runTag
$total++; if (Test-Endpoint "Crear carrera" "POST" "$baseUrl`:8084/api/v1/carreras" $body "carreraId") { $exitos++ } else { $fallidos++ }
$total++; if (Test-Endpoint-Simple "Listar carreras" "GET" "$baseUrl`:8084/api/v1/carreras") { $exitos++ } else { $fallidos++ }
$total++; if (Test-Endpoint-Simple "Get carrera" "GET" "$baseUrl`:8084/api/v1/carreras/$script:carreraId") { $exitos++ } else { $fallidos++ }

Write-Host "`n=== FASE 2: Crear asignaturas ===" -ForegroundColor Yellow
$body = '{"nombre":"Programacion I - __TAG__","creditos":6}' -replace '__TAG__', $runTag
$total++; if (Test-Endpoint "Crear asignatura 1" "POST" "$baseUrl`:8080/api/v1/asignaturas" $body "asignatura1Id") { $exitos++ } else { $fallidos++ }
$body = '{"nombre":"Programacion II - __TAG__","creditos":6}' -replace '__TAG__', $runTag
$total++; if (Test-Endpoint "Crear asignatura 2" "POST" "$baseUrl`:8080/api/v1/asignaturas" $body "asignatura2Id") { $exitos++ } else { $fallidos++ }
$total++; if (Test-Endpoint-Simple "Listar asignaturas" "GET" "$baseUrl`:8080/api/v1/asignaturas") { $exitos++ } else { $fallidos++ }
$total++; if (Test-Endpoint-Simple "Get prerrequisitos" "GET" "$baseUrl`:8080/api/v1/asignaturas/$script:asignatura2Id/prerequisitos") { $exitos++ } else { $fallidos++ }
$body = '{"asignaturaPrincipal":{"id":"' + $script:asignatura2Id + '"},"asignaturaRequisito":{"id":"' + $script:asignatura1Id + '"}}'
$total++; if (Test-Endpoint "Crear prerrequisito" "POST" "$baseUrl`:8080/api/v1/prerequisitos" $body) { $exitos++ } else { $fallidos++ }

Write-Host "`n=== FASE 3: Crear docente y especialidad ===" -ForegroundColor Yellow
$body = '{"nombre":"Especialidad __TAG__","descripcion":"Especialidad de prueba"}' -replace '__TAG__', $runTag
$total++; if (Test-Endpoint "Crear especialidad" "POST" "$baseUrl`:8082/api/v1/especialidades" $body "especialidadId") { $exitos++ } else { $fallidos++ }
$body = '{"nombre":"Maria","apellido":"Gonzalez","email":"maria___TAG__@test.cl","especialidadId":"' -replace '__TAG__', $runTag
$body = $body + $script:especialidadId + '"}'
$total++; if (Test-Endpoint "Crear docente" "POST" "$baseUrl`:8082/api/v1/docentes" $body "docenteId") { $exitos++ } else { $fallidos++ }
$total++; if (Test-Endpoint-Simple "Get docente" "GET" "$baseUrl`:8082/api/v1/docentes/$script:docenteId") { $exitos++ } else { $fallidos++ }
$body = '{"tipoContrato":"Full Time","fechaInicio":"2024-01-01","fechaFin":"2024-12-31","sueldoBase":800000,"docente":{"id":"' + $script:docenteId + '"}}'
$total++; if (Test-Endpoint "Crear contrato" "POST" "$baseUrl`:8082/api/contratos" $body) { $exitos++ } else { $fallidos++ }
$total++; if (Test-Endpoint-Simple "Listar contratos" "GET" "$baseUrl`:8082/api/contratos") { $exitos++ } else { $fallidos++ }

Write-Host "`n=== FASE 4: Crear empresa (con convenio para R5) ===" -ForegroundColor Yellow
$body = '{"nombre":"TechCorp __TAG__","rut":"__RUT__","rubro":"Tecnologia"}' -replace '__TAG__', $runTag -replace '__RUT__', $rutFull
$total++; if (Test-Endpoint "Crear empresa" "POST" "$baseUrl`:8089/api/v1/empresas" $body "empresaId") { $exitos++ } else { $fallidos++ }
# Set convenio dates so R5 passes (empresa needs fechaInicioConvenio and fechaFinConvenio)
$body = '{"nombre":"TechCorp __TAG__","rut":"__RUT__","rubro":"Tecnologia","fechaInicioConvenio":"2024-01-01","fechaFinConvenio":"2028-12-31"}' -replace '__TAG__', $runTag -replace '__RUT__', $rutFull
$total++; if (Test-Endpoint "Actualizar empresa con fechas" "PUT" "$baseUrl`:8089/api/v1/empresas/$script:empresaId" $body) { $exitos++ } else { $fallidos++ }
$total++; if (Test-Endpoint-Simple "Get empresa" "GET" "$baseUrl`:8089/api/v1/empresas/$script:empresaId") { $exitos++ } else { $fallidos++ }
$total++; if (Test-Endpoint-Simple "Convenio vigente" "GET" "$baseUrl`:8089/api/v1/empresas/$script:empresaId/tiene-convenio-vigente") { $exitos++ } else { $fallidos++ }

Write-Host "`n=== FASE 5: Crear estudiante ===" -ForegroundColor Yellow
$body = '{"nombre":"Juan Perez","rut":"__RUT__","email":"juan___TAG__@test.cl","telefono":"+56912345678","direccion":"Calle 123"}' -replace '__TAG__', $runTag -replace '__RUT__', $rutFull
$total++; if (Test-Endpoint "Crear estudiante" "POST" "$baseUrl`:8085/api/v1/estudiantes" $body "estudianteId") { $exitos++ } else { $fallidos++ }
$total++; if (Test-Endpoint-Simple "Get estudiante" "GET" "$baseUrl`:8085/api/v1/estudiantes/$script:estudianteId") { $exitos++ } else { $fallidos++ }
$total++; if (Test-Endpoint-Simple "Get by RUT" "GET" "$baseUrl`:8085/api/v1/estudiantes/rut/$rutFull") { $exitos++ } else { $fallidos++ }

Write-Host "`n=== FASE 6: Crear matricula (PREREQUISITO para notas y asistencia) ===" -ForegroundColor Yellow
$body = '{"estudianteId":"' + $script:estudianteId + '","seccionId":"' + $script:asignatura1Id + '","fechaMatricula":"2024-03-01","estado":"ACTIVA"}'
$total++; if (Test-Endpoint "Crear matricula" "POST" "$baseUrl`:8083/api/v1/matriculas" $body "matriculaId") { $exitos++ } else { $fallidos++ }
$total++; if (Test-Endpoint-Simple "Get matricula" "GET" "$baseUrl`:8083/api/v1/matriculas/$script:matriculaId") { $exitos++ } else { $fallidos++ }

Write-Host "`n=== FASE 7: Crear arancel y pagar (PREREQUISITO para practicas) ===" -ForegroundColor Yellow
$body = '{"estudianteId":"' + $script:estudianteId + '","concepto":"Matricula 2024-1","monto":150000,"fechaEmision":"2024-01-01","fechaVencimiento":"2024-03-15"}'
$total++; if (Test-Endpoint "Crear arancel" "POST" "$baseUrl`:8081/api/v1/aranceles" $body "arancelId") { $exitos++ } else { $fallidos++ }
$total++; if (Test-Endpoint-Simple "Pagar arancel" "POST" "$baseUrl`:8081/api/v1/aranceles/$script:arancelId/pagar") { $exitos++ } else { $fallidos++ }
$total++; if (Test-Endpoint-Simple "Deuda vencida" "GET" "$baseUrl`:8081/api/v1/aranceles/estudiante/$script:estudianteId/tiene-deuda-vencida") { $exitos++ } else { $fallidos++ }
$total++; if (Test-Endpoint-Simple "Puede continuar" "GET" "$baseUrl`:8081/api/v1/aranceles/estudiante/$script:estudianteId/puede-continuar") { $exitos++ } else { $fallidos++ }

Write-Host "`n=== FASE 8: Crear notas ===" -ForegroundColor Yellow
$body = '{"estudianteId":"' + $script:estudianteId + '","seccionId":"' + $script:asignatura1Id + '","nota":6.5,"tipo":"EXAMEN","ponderacion":0.30,"fecha":"2024-05-01"}'
$total++; if (Test-Endpoint "Crear nota" "POST" "$baseUrl`:8086/api/v1/notas" $body "notaId") { $exitos++ } else { $fallidos++ }
$total++; if (Test-Endpoint-Simple "Get nota" "GET" "$baseUrl`:8086/api/v1/notas/$script:notaId") { $exitos++ } else { $fallidos++ }
$body = '{"estudianteId":"' + $script:estudianteId + '","seccionId":"' + $script:asignatura1Id + '","nota":7.0,"tipo":"EXAMEN","ponderacion":0.30,"fecha":"2024-05-15"}'
$total++; if (Test-Endpoint "Actualizar nota" "PUT" "$baseUrl`:8086/api/v1/notas/$script:notaId" $body) { $exitos++ } else { $fallidos++ }
$total++; if (Test-Endpoint-Simple "Listar notas" "GET" "$baseUrl`:8086/api/v1/notas") { $exitos++ } else { $fallidos++ }

Write-Host "`n=== FASE 9: Crear asistencia ===" -ForegroundColor Yellow
$body = '{"estudianteId":"' + $script:estudianteId + '","seccionId":"' + $script:asignatura1Id + '","fecha":"2024-05-16","tipo":"PRESENTE","observacion":"Asistio normalmente"}'
$total++; if (Test-Endpoint "Registrar asistencia" "POST" "$baseUrl`:8088/api/v1/asistencias" $body "asistenciaId" $function:ExtractNestedId) { $exitos++ } else { $fallidos++ }
$total++; if (Test-Endpoint-Simple "Get asistencia" "GET" "$baseUrl`:8088/api/v1/asistencias/$script:asistenciaId") { $exitos++ } else { $fallidos++ }
$total++; if (Test-Endpoint-Simple "Resumen R2" "GET" "$baseUrl`:8088/api/v1/asistencias/estudiante/$script:estudianteId/seccion/$script:asignatura1Id/resumen") { $exitos++ } else { $fallidos++ }
$body = '{"estudianteId":"' + $script:estudianteId + '","seccionId":"' + $script:asignatura1Id + '","fecha":"2024-05-16","tipo":"JUSTIFICADO","observacion":"Justificado"}'
$total++; if (Test-Endpoint "Actualizar asistencia" "PUT" "$baseUrl`:8088/api/v1/asistencias/$script:asistenciaId" $body) { $exitos++ } else { $fallidos++ }

Write-Host "`n=== FASE 10: Crear práctica ===" -ForegroundColor Yellow
$body = '{"estudianteId":"' + $script:estudianteId + '","empresaId":"' + $script:empresaId + '","supervisorNombre":"Carlos Lopez","fechaInicio":"2024-06-01"}'
$total++; if (Test-Endpoint "Crear practica" "POST" "$baseUrl`:8087/api/v1/practicas" $body "practicaId") { $exitos++ } else { $fallidos++ }
$total++; if (Test-Endpoint-Simple "Get practica" "GET" "$baseUrl`:8087/api/v1/practicas/$script:practicaId") { $exitos++ } else { $fallidos++ }
$body = '{"fechaFin":"2024-12-01","estado":"REPROBADA","observaciones":"No cumplio objetivos"}'
$total++; if (Test-Endpoint "Finalizar practica" "PUT" "$baseUrl`:8087/api/v1/practicas/$script:practicaId/finalizar" $body) { $exitos++ } else { $fallidos++ }

Write-Host "`n=== FASE 11: Tests adicionales ===" -ForegroundColor Yellow
try {
    $r5 = Invoke-RestMethod -Uri "$baseUrl`:8087/api/v1/practicas/verificar?estudianteId=$script:estudianteId&empresaId=$script:empresaId" -ErrorAction Stop
    Write-Host "[OK]   Verificar R5: creditos=$($r5.creditosAprobados) arancel=$($r5.arancelAlDia) empresa=$($r5.empresaConConvenio) puedeInscribir=$($r5.puedeInscribir)" -ForegroundColor White
    $total++; $exitos++
} catch {
    $statusCode = if ($_.Exception.Response) { [int]$_.Exception.Response.StatusCode } else { "ERR" }
    Write-Host "[FAIL] GET Verificar R5 - $statusCode" -ForegroundColor Yellow
    $total++; $fallidos++
}
$total++; if (Test-Endpoint-Simple "Avance 80%" "GET" "$baseUrl`:8086/api/v1/notas/estudiante/$script:estudianteId/avance") { $exitos++ } else { $fallidos++ }
$total++; if (Test-Endpoint-Simple "Listar docentes" "GET" "$baseUrl`:8082/api/v1/docentes") { $exitos++ } else { $fallidos++ }
$total++; if (Test-Endpoint-Simple "Listar especialidades" "GET" "$baseUrl`:8082/api/v1/especialidades") { $exitos++ } else { $fallidos++ }
$total++; if (Test-Endpoint-Simple "Listar empresas" "GET" "$baseUrl`:8089/api/v1/empresas") { $exitos++ } else { $fallidos++ }
$total++; if (Test-Endpoint-Simple "Listar creditos" "GET" "$baseUrl`:8080/api/v1/creditos") { $exitos++ } else { $fallidos++ }
$total++; if (Test-Endpoint-Simple "Asistencias seccion" "GET" "$baseUrl`:8088/api/v1/asistencias/seccion/$script:asignatura1Id") { $exitos++ } else { $fallidos++ }
$total++; if (Test-Endpoint-Simple "Asistencias estudiante" "GET" "$baseUrl`:8088/api/v1/asistencias/estudiante/$script:estudianteId") { $exitos++ } else { $fallidos++ }

Write-Host "`n=== FASE 12: Eliminar recursos (DELETE) [SKIPPED - datos preservados] ===" -ForegroundColor Yellow
Write-Host "  [SKIP] Los datos se mantienen para ver en Swagger" -ForegroundColor DarkGray

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "RESULTADO: $exitos OK / $total total" -ForegroundColor Cyan
if ($fallidos -gt 0) {
    Write-Host "FALLIDOS: $fallidos" -ForegroundColor Red
} else {
    Write-Host "TODOS LOS ENDPOINTS FUNCIONANDO" -ForegroundColor Green
}
Write-Host "========================================" -ForegroundColor Cyan