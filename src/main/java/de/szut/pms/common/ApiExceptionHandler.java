package de.szut.pms.common;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import de.szut.pms.customer.CustomerNotFoundException;
import de.szut.pms.employee.EmployeeNotFoundException;
import de.szut.pms.employee.EmployeeServiceUnauthorizedException;
import de.szut.pms.employee.EmployeeServiceUnavailableException;
import de.szut.pms.hello.HelloNotFoundException;
import de.szut.pms.project.EmployeeAlreadyAssignedException;
import de.szut.pms.project.EmployeeQualificationNotFoundException;
import de.szut.pms.project.ProjectNotFoundException;

/**
 * Zentrale Übersetzung von Ausnahmen in HTTP-Antworten nach RFC 9457
 * (ProblemDetail). Eine Methode je Ausnahmetyp, wie in Tutorial 4, AB 03.
 * <p>
 * Ergänze hier eigene {@code @ExceptionHandler}-Methoden für die fachlichen
 * Ausnahmen deiner Ressourcen — nach demselben Muster wie
 * {@link #handleNotFound}.
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApiExceptionHandler {

    @ExceptionHandler(HelloNotFoundException.class)
    public ProblemDetail handleNotFound(HelloNotFoundException ex) {
        ProblemDetail problem =
                ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Nicht gefunden");
        return problem;
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    public ProblemDetail handleProjectNotFound(ProjectNotFoundException ex) {
        ProblemDetail problem =
                ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Nicht gefunden");
        return problem;
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ProblemDetail handleEmployeeNotFound(EmployeeNotFoundException ex) {
        ProblemDetail problem =
                ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Mitarbeiter nicht gefunden");
        return problem;
    }

    @ExceptionHandler(EmployeeQualificationNotFoundException.class)
    public ProblemDetail handleEmployeeQualificationNotFound(
            EmployeeQualificationNotFoundException ex) {
        ProblemDetail problem =
                ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problem.setTitle("Qualifikation nicht vorhanden");
        return problem;
    }

    @ExceptionHandler(EmployeeAlreadyAssignedException.class)
    public ProblemDetail handleEmployeeAlreadyAssigned(EmployeeAlreadyAssignedException ex) {
        ProblemDetail problem =
                ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Zuordnung bereits vorhanden");
        return problem;
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ProblemDetail handleCustomerNotFound(CustomerNotFoundException ex) {
        ProblemDetail problem =
                ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Kunde nicht gefunden");
        return problem;
    }

    @ExceptionHandler(EmployeeServiceUnauthorizedException.class)
    public ProblemDetail handleEmployeeServiceUnauthorized(EmployeeServiceUnauthorizedException ex) {
        ProblemDetail problem =
                ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        problem.setTitle("Nicht autorisiert");
        return problem;
    }

    @ExceptionHandler(EmployeeServiceUnavailableException.class)
    public ProblemDetail handleEmployeeServiceUnavailable(EmployeeServiceUnavailableException ex) {
        ProblemDetail problem =
                ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
        problem.setTitle("Employee-Service nicht verfügbar");
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(
                error -> errors.put(error.getField(), error.getDefaultMessage()));

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Die Anfrage enthält ungültige Felder.");
        problem.setTitle("Ungültige Eingabe");
        problem.setProperty("errors", errors);
        return problem;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleUnreadable(HttpMessageNotReadableException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Die Anfrage ist nicht lesbar oder enthält ungültige Werte (z. B. ein ungültiges Datum im Format yyyy-MM-dd).");
        problem.setTitle("Ungültige Eingabe");
        return problem;
    }
}
