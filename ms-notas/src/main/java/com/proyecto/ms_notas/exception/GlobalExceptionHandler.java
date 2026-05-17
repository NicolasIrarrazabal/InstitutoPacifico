package com.proyecto.ms_notas.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

// @RestControllerAdvice: captura todas las excepciones del proyecto y las convierte
// en respuestas JSON con el código HTTP correcto (400, 404, 409, 422, 500)
// Así el cliente siempre recibe JSON, no el error feo por defecto de Spring
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Violación de constraint de base de datos (ej: campo NOT NULL en SQL)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException ex) {
        String errores = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining(", "));
        log.error("Violación de constraint: {}", errores);
        return buildResponse(HttpStatus.BAD_REQUEST, "CONSTRAINT_VIOLATION", errores);
    }

    // Entidad no encontrada en la base de datos (ej: nota con ese ID no existe)
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEntityNotFound(EntityNotFoundException ex) {
        log.error("Recurso no encontrado: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, "RECURSO_NO_ENCONTRADO", ex.getMessage());
    }

    // Argumento inválido (ej: UUID con formato incorrecto)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        log.error("Argumento inválido: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "ARGUMENTO_INVALIDO", ex.getMessage());
    }

    // Estado inválido de negocio (ej: R1 fallida, nota duplicada, nota anulada)
    // Se mapea a HTTP 409 CONFLICT, igual que en ms-matriculas
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException ex) {
        log.error("Conflicto de estado: {}", ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, "CONFLICTO_DE_ESTADO", ex.getMessage());
    }

    // Error de validación del DTO (@NotNull, @DecimalMin, etc.)
    // Spring lanza esta excepción cuando el JSON de entrada no pasa las validaciones
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errores.put(error.getField(), error.getDefaultMessage());
        }
        log.error("Error de validación en campos: {}", errores);

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.UNPROCESSABLE_ENTITY.value());
        body.put("error", "ERROR_DE_VALIDACION");
        body.put("detalle", errores);  // Aquí van los campos que fallaron y el mensaje de error
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(body);
    }

    // Cualquier otro error no controlado → 500 Internal Server Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Error interno no controlado: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "ERROR_INTERNO",
                "Ocurrió un error interno en el servidor");
    }

    // Método auxiliar que construye el JSON de error de forma consistente
    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String error, String mensaje) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", error);
        body.put("mensaje", mensaje);
        return ResponseEntity.status(status).body(body);
    }
}
