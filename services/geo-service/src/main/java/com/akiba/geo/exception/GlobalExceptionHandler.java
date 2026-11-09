package com.akiba.geo.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.core.PropertyReferenceException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String ERROR_BASE_URI = "https://api.akiba.com/errors/";

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        ProblemDetail problem = build(HttpStatus.NOT_FOUND, "resource-not-found",
            "Ressource introuvable", ex.getMessage(), request);
        problem.setProperty("resource", ex.getResource());
        problem.setProperty("identifier", ex.getIdentifier());
        return problem;
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ProblemDetail handleDuplicate(DuplicateResourceException ex, HttpServletRequest request) {
        ProblemDetail problem = build(HttpStatus.CONFLICT, "duplicate-resource",
            "Ressource déjà existante", ex.getMessage(), request);
        problem.setProperty("resource", ex.getResource());
        problem.setProperty("field", ex.getField());
        return problem;
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ProblemDetail handleBusinessRule(BusinessRuleException ex, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, "business-rule-violation",
            "Règle métier non respectée", ex.getMessage(), request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest request) {
        log.warn("Violation d'intégrité en base : {}", ex.getMostSpecificCause().getMessage());
        return build(HttpStatus.CONFLICT, "data-integrity-violation",
            "Conflit de données",
            "L'opération viole une contrainte d'intégrité des données", request);
    }

    @ExceptionHandler(PropertyReferenceException.class)
    public ProblemDetail handleInvalidSort(PropertyReferenceException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "invalid-parameter",
            "Paramètre invalide",
            "Propriété de tri inconnue : " + ex.getPropertyName(), request);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex, HttpServletRequest request) {
        log.error("Erreur inattendue sur {}", request.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "internal-error",
            "Erreur interne",
            "Une erreur inattendue est survenue", request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex, HttpHeaders headers,
        HttpStatusCode status, WebRequest request) {

        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
            .forEach(error -> errors.putIfAbsent(error.getField(), error.getDefaultMessage()));

        ProblemDetail problem = ex.getBody();
        problem.setType(URI.create(ERROR_BASE_URI + "validation-error"));
        problem.setTitle("Données invalides");
        problem.setDetail("Un ou plusieurs champs sont invalides");
        problem.setProperty("errors", errors);
        problem.setProperty("timestamp", Instant.now());

        return handleExceptionInternal(ex, problem, headers, status, request);
    }

    private ProblemDetail build(
        HttpStatus status,
        String type,
        String title,
        String detail,
        HttpServletRequest request) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setType(URI.create(ERROR_BASE_URI + type));
        problem.setTitle(title);
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("timestamp", Instant.now());

        return problem;
    }

}
