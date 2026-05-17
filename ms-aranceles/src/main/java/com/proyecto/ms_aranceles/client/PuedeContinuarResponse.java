package com.proyecto.ms_aranceles.client;

// Respuesta que este microservicio entrega cuando otros lo consultan
// para saber si un estudiante puede continuar con la R5
public record PuedeContinuarResponse(
        Boolean puedeContinuar
) {}
