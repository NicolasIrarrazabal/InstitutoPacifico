package com.proyecto.ms_estudiante.exception;

import jakarta.persistence.EntityNotFoundException;
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

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // cuando no se encuentra un registro en la base de datos
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEntityNotFound(EntityNotFoundException ex) {
        log.error("No se encontró el recurso: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, "RECURSO_NO_ENCONTRADO", ex.getMessage());
    }

    // cuando hay errores de lógica o datos inválidos (ej: RUT duplicado, email repetido)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        log.error("Datos inválidos: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "DATO_INVALIDO", ex.getMessage());
    }

    // cuando la operación no se puede hacer por el estado del registro (ej: estudiante inactivo)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException ex) {
        log.error("Estado no válido para la operación: {}", ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, "ESTADO_INVALIDO", ex.getMessage());
    }

    // cuando fallan las validaciones del DTO (@NotBlank, @Email, etc.)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {

        Map<String, String> errores = new HashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errores.put(error.getField(), error.getDefaultMessage());
        }

        log.error("Errores de validación: {}", errores);

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.UNPROCESSABLE_CONTENT.value());
        body.put("error", "ERROR_DE_VALIDACION");
        body.put("detalle", errores);

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(body);
    }

    // cuando algo falla y no lo estamos controlando explícitamente
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Error inesperado en el servidor: {}", ex.getMessage(), ex);
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "ERROR_INTERNO",
                "Ocurrió un error inesperado en el servidor"
        );
    }

    // metodo auxiliar para no repetir código en las respuestas
    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String error, String mensaje) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", error);
        body.put("mensaje", mensaje);

        return ResponseEntity.status(status).body(body);
    }
}