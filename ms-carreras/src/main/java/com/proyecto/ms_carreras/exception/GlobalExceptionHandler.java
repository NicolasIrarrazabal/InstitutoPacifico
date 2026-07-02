package com.proyecto.ms_carreras.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException ex) {
        String errores = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining(", "));
        log.error("Violación de constraint: {}", errores);
        return buildResponse(HttpStatus.BAD_REQUEST, "CONSTRAINT_VIOLATION", errores);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEntityNotFound(EntityNotFoundException ex) {
        log.error("Recurso no encontrado: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, "RECURSO_NO_ENCONTRADO", ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        log.error("Argumento inválido: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "ARGUMENTO_INVALIDO", ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException ex) {
        log.error("Conflicto de estado: {}", ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, "CONFLICTO_DE_ESTADO", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errores.put(error.getField(), error.getDefaultMessage());
        }
        log.error("Error de validación en campos: {}", errores);

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.UNPROCESSABLE_CONTENT.value());
        body.put("error", "ERROR_DE_VALIDACION");
        body.put("detalle", errores);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleNotReadable(HttpMessageNotReadableException ex) {
        log.error("JSON del request ilegible o mal formado: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "JSON_INVALIDO", "El cuerpo de la petición no es un JSON válido o no coincide con el formato esperado");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.error("Tipo de argumento inválido en '{}': {}", ex.getName(), ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "TIPO_INVALIDO",
                "El valor recibido para '" + ex.getName() + "' no tiene el formato esperado");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
        Throwable causa = ex.getMostSpecificCause();
        log.error("Violación de integridad de datos: {}", causa.getMessage(), ex);
        return buildResponse(HttpStatus.CONFLICT, "VIOLACION_DE_INTEGRIDAD", causa.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        // Se loguea la excepción real con stacktrace completo para diagnóstico en el servidor.
        log.error("Error interno no controlado [{}]: {}", ex.getClass().getName(), ex.getMessage(), ex);

        // Se incluye el tipo de excepción en la respuesta para poder diagnosticar rápido
        // errores no mapeados sin depender solo de los logs del servidor.
        // TODO: quitar "tipoExcepcion" del body antes de exponer esto en un entorno público final,
        // una vez identificada y resuelta la causa raíz del POST.
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "ERROR_INTERNO");
        body.put("mensaje", "Ocurrió un error interno en el servidor");
        body.put("tipoExcepcion", ex.getClass().getSimpleName());
        body.put("detalle", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String error, String mensaje) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", error);
        body.put("mensaje", mensaje);
        return ResponseEntity.status(status).body(body);
    }
}