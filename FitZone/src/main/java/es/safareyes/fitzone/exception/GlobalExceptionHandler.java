package es.safareyes.fitzone.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> manejarEntidadNoEncontrada(
            EntityNotFoundException exception
    ) {
        return respuesta(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> manejarEstadoIlegal(
            IllegalStateException exception
    ) {
        return respuesta(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );
    }

    private ResponseEntity<Map<String, Object>> respuesta(
            HttpStatus estado,
            String mensaje
    ) {
        Map<String, Object> cuerpo = Map.of(
                "timestamp", Instant.now().toString(),
                "status", estado.value(),
                "error", estado.getReasonPhrase(),
                "message", mensaje
        );

        return ResponseEntity
                .status(estado)
                .body(cuerpo);
    }
}