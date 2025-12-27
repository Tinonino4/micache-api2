package com.micache.mi_cache.security.exception;

import com.micache.mi_cache.career.domain.exception.InvalidDatesExperienceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalSecurityExceptionHandler {

    // Java 25: Agrupamos todas las excepciones de negocio en un solo manejador potente
    @ExceptionHandler({
            InvalidPasswordException.class,
            InvalidTokenException.class,
            EmailAlreadyExistsException.class,
            InvalidDatesExperienceException.class,
            ResourceNotFoundException.class, // Incluimos esta para manejarla con ProblemDetail también
            Exception.class // Catch-all
    })
    public ResponseEntity<ProblemDetail> handleExceptions(Exception ex) {

        // Pattern Matching moderno: Asigna variables y tipos en el switch
        ProblemDetail problem = switch (ex) {
            case InvalidPasswordException ipe ->
                    buildProblem(ipe, HttpStatus.BAD_REQUEST, "Invalid Password");

            case InvalidTokenException ite ->
                    buildProblem(ite, HttpStatus.UNAUTHORIZED, "Invalid or Expired Token");

            case EmailAlreadyExistsException eae ->
                    buildProblem(eae, HttpStatus.CONFLICT, "Email Conflict"); // 409 es mejor que 400 para conflictos

            case InvalidDatesExperienceException ide ->
                    buildProblem(ide, HttpStatus.BAD_REQUEST, "Invalid Date Range");

            case ResourceNotFoundException rnf ->
                    buildProblem(rnf, HttpStatus.NOT_FOUND, "Resource Not Found");

            // Default case para errores no controlados
            case Exception e -> {
                // En Java 25 podríamos usar String Templates para logs: STR."Error inesperado: \{e.getMessage()}"
                ProblemDetail p = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "An internal error occurred.");
                p.setTitle("Internal Server Error");
                yield p;
            }
        };

        // Añadimos metadata estándar de observabilidad
        problem.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(problem.getStatus()).body(problem);
    }

    private ProblemDetail buildProblem(Exception ex, HttpStatus status, String title) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        problem.setTitle(title);
        // Podemos añadir un 'type' que apunte a la documentación del error
        problem.setType(URI.create("https://api.micache.com/errors/" + title.toLowerCase().replace(" ", "-")));
        return problem;
    }
}
