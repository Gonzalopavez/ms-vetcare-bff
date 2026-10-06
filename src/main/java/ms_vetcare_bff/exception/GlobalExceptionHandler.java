package ms_vetcare_bff.exception;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /*
     * Errores HTTP devueltos por los microservicios.
     *
     * Ejemplos:
     *
     * 400 Bad Request
     * 404 Not Found
     * 409 Conflict
     * 500 Internal Server Error
     *
     * El BFF conserva el código HTTP original.
     */
    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<String> handleDownstreamHttpError(
            RestClientResponseException exception
    ) {

        String body =
            exception.getResponseBodyAsString();

        if (body == null || body.isBlank()) {

            body = """
                {
                  "error": "DOWNSTREAM_ERROR",
                  "message": "El microservicio rechazó la solicitud."
                }
                """;
        }

        return ResponseEntity
            .status(
                exception.getStatusCode()
            )
            .contentType(
                MediaType.APPLICATION_JSON
            )
            .body(body);
    }


    /*
     * El BFF no pudo conectarse físicamente
     * con uno de los microservicios.
     *
     * Ejemplo:
     *
     * localhost:8081 apagado
     * localhost:8082 apagado
     */
    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<Map<String, Object>>
        handleServiceUnavailable(
            ResourceAccessException exception,
            HttpServletRequest request
        ) {

        Map<String, Object> response =
            new LinkedHashMap<>();

        response.put(
            "timestamp",
            Instant.now().toString()
        );

        response.put(
            "status",
            HttpStatus.SERVICE_UNAVAILABLE.value()
        );

        response.put(
            "error",
            "Service Unavailable"
        );

        response.put(
            "message",
            "Uno de los servicios internos de VetCare no se encuentra disponible."
        );

        response.put(
            "path",
            request.getRequestURI()
        );

        return ResponseEntity
            .status(
                HttpStatus.SERVICE_UNAVAILABLE
            )
            .body(response);
    }


    /*
     * Error inesperado no controlado.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>>
        handleUnexpectedError(
            Exception exception,
            HttpServletRequest request
        ) {

        Map<String, Object> response =
            new LinkedHashMap<>();

        response.put(
            "timestamp",
            Instant.now().toString()
        );

        response.put(
            "status",
            HttpStatus.INTERNAL_SERVER_ERROR.value()
        );

        response.put(
            "error",
            "Internal Server Error"
        );

        response.put(
            "message",
            "Se produjo un error interno al procesar la solicitud."
        );

        response.put(
            "path",
            request.getRequestURI()
        );

        return ResponseEntity
            .status(
                HttpStatus.INTERNAL_SERVER_ERROR
            )
            .body(response);
    }
}