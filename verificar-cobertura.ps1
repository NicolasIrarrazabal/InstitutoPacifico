# verificar-cobertura.ps1
# Ejecuta mvn clean verify en cada microservicio y resume si paso o no el 80% de JaCoCo.
# Uso: parate en la carpeta InstitutoPacifico/ y ejecuta:  .\verificar-cobertura.ps1
#
# Todo lo que pase (incluidos errores) queda guardado en verificar-cobertura.log
# aunque la terminal se cierre o se trabe, para poder revisarlo despues.

$ErrorActionPreference = "Continue"
$logFile = "verificar-cobertura.log"

try {
    Start-Transcript -Path $logFile -Append -ErrorAction Stop
} catch {
    Write-Host "No se pudo iniciar el log, se continua sin transcript." -ForegroundColor Yellow
}

Write-Host "=== INICIO $(Get-Date) ===" -ForegroundColor Magenta
Write-Host "Directorio actual: $(Get-Location)" -ForegroundColor Magenta
Write-Host "Log completo se esta guardando en: $logFile" -ForegroundColor Magenta

$microservicios = @(
    "ms-aranceles",
    "ms-asignaturas",
    "ms-asistencia",
    "ms-carreras",
    "ms-docente",
    "ms-empresas",
    "ms-estudiante",
    "ms-matriculas",
    "ms-notas",
    "ms-practicas"
)

$resultados = @()

foreach ($ms in $microservicios) {
    Write-Host "`n=== Verificando $ms ===" -ForegroundColor Cyan

    if (-not (Test-Path $ms)) {
        Write-Host "  Carpeta no encontrada, se omite." -ForegroundColor Yellow
        $resultados += [PSCustomObject]@{ Microservicio = $ms; Resultado = "NO ENCONTRADO"; Detalle = "-" }
        continue
    }

    try {
        Push-Location $ms

        # Usa el wrapper si existe, si no, mvn del sistema
        if (Test-Path ".\mvnw.cmd") {
            $salida = & .\mvnw.cmd clean verify 2>&1
        } else {
            $salida = & mvn clean verify 2>&1
        }

        $exitoso = $LASTEXITCODE -eq 0

        if ($exitoso) {
            Write-Host "  BUILD SUCCESS - cobertura OK (>=80%)" -ForegroundColor Green
            $resultados += [PSCustomObject]@{ Microservicio = $ms; Resultado = "OK"; Detalle = "Cobertura >= 80%" }
        } else {
            $lineaRegla = $salida | Select-String "Rule violated" | Select-Object -First 1
            $detalle = if ($lineaRegla) { $lineaRegla.ToString().Trim() } else { "Revisar log completo, build fallo" }
            Write-Host "  BUILD FAILURE" -ForegroundColor Red
            Write-Host "  $detalle" -ForegroundColor Red
            $resultados += [PSCustomObject]@{ Microservicio = $ms; Resultado = "FALLA"; Detalle = $detalle }
        }
    }
    catch {
        Write-Host "  ERROR INESPERADO al procesar $ms : $($_.Exception.Message)" -ForegroundColor Red
        $resultados += [PSCustomObject]@{ Microservicio = $ms; Resultado = "ERROR SCRIPT"; Detalle = $_.Exception.Message }
    }
    finally {
        # Se asegura de volver a la carpeta raiz pase lo que pase, para no perder el resto del loop
        if ((Get-Location).Path -like "*$ms") {
            Pop-Location
        }
    }
}

Write-Host "`n`n========== RESUMEN FINAL ==========" -ForegroundColor Magenta
$resultados | Format-Table -AutoSize -Wrap

$fallidos = $resultados | Where-Object { $_.Resultado -ne "OK" }
if ($fallidos.Count -eq 0) {
    Write-Host "`nTodos los microservicios pasaron el 80% de cobertura." -ForegroundColor Green
} else {
    Write-Host "`n$($fallidos.Count) microservicio(s) NO pasaron el 80%. Revisa el detalle de arriba y el reporte HTML en cada target\site\jacoco\index.html" -ForegroundColor Yellow
}

Write-Host "`n=== FIN $(Get-Date) ===" -ForegroundColor Magenta
Write-Host "Revisa el log completo en: $logFile" -ForegroundColor Magenta

try { Stop-Transcript | Out-Null } catch {}
