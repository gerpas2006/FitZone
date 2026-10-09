package es.safareyes.fitzone.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarArgumentosNoValidos(
            MethodArgumentNotValidException exception
    ) {
        String mensaje = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return respuesta(HttpStatus.BAD_REQUEST, mensaje);
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<Map<String, Object>> manejarErrorDeTransaccion(
            TransactionSystemException exception
    ) {
        Throwable causa = exception;

        while (causa != null
                && !(causa instanceof ConstraintViolationException)) {
            causa = causa.getCause();
        }

        if (causa instanceof ConstraintViolationException validationException) {
            String mensaje = validationException.getConstraintViolations()
                    .stream()
                    .map(this::mensajeDeViolacion)
                    .collect(Collectors.joining("; "));

            return respuesta(HttpStatus.BAD_REQUEST, mensaje);
        }

        return respuesta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "No se ha podido completar la operación"
        );
    }

    private String mensajeDeViolacion(
            ConstraintViolation<?> violation
    ) {
        return violation.getPropertyPath()
                + ": "
                + violation.getMessage();
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

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> manejarDuplicado(
            DataIntegrityViolationException exception
    ) {
        String detalle = exception.getMostSpecificCause().getMessage();

        String mensaje;

        if (detalle != null && detalle.contains("socio_matricula_key")) {
            mensaje = "La matrícula ya está registrada";
        } else if (detalle != null && detalle.contains("socio_email_key")) {
            mensaje = "El email ya está registrado";
        } else if (detalle != null && detalle.contains("socio_dni_key")) {
            mensaje = "El DNI ya está registrado";
        } else {
            mensaje = "Ya existe un registro con alguno de los datos indicados";
        }

        return respuesta(HttpStatus.CONFLICT, mensaje);
    }
}