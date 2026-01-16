package com.achernov.cryptoarb.exception;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.slf4j.MDC;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;

import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @Override
  protected @Nullable ResponseEntity<Object> handleExceptionInternal(
          Exception ex, @Nullable Object body,
          HttpHeaders headers, HttpStatusCode status, WebRequest request) {

    if (status.is5xxServerError()) {
      log.error("Server Error: {}", ex.getMessage(), ex);
    } else {
      log.warn("Client Error: {} - Status: {}", ex.getMessage(), status);
    }

    return super.handleExceptionInternal(ex, body, headers, status, request);
  }

  @Override
  protected ResponseEntity<Object> createResponseEntity(
          @Nullable Object body, HttpHeaders headers,
          HttpStatusCode statusCode, WebRequest request) {

    if (body instanceof ProblemDetail pd) {
      enrichProblemDetail(pd);
    }

    return super.createResponseEntity(body, headers, statusCode, request);
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ProblemDetail> handleAuthenticationException(AuthenticationException ex) {
    log.warn("Authentication failed: {}", ex.getMessage());

    ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
    enrichProblemDetail(pd);

    return ResponseEntity
            .status(pd.getStatus())
            .contentType(APPLICATION_PROBLEM_JSON)
            .body(pd);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ProblemDetail> handleAccessDeniedException(AccessDeniedException ex) {
    log.warn("Access denied: {}", ex.getMessage());

    ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
    enrichProblemDetail(pd);

    return ResponseEntity
            .status(pd.getStatus())
            .contentType(APPLICATION_PROBLEM_JSON)
            .body(pd);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ProblemDetail> handleAllUncaughtException(Exception ex) {
    log.error("Unhandled exception occurred: ", ex);

    ProblemDetail pd = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred. Please contact support.");
    enrichProblemDetail(pd);

    return ResponseEntity
            .status(pd.getStatus())
            .contentType(APPLICATION_PROBLEM_JSON)
            .body(pd);
  }

  private void enrichProblemDetail(ProblemDetail pd) {
    pd.setProperty("timestamp", Instant.now().toEpochMilli());

    String traceId = MDC.get("traceId");
    pd.setProperty("traceId", traceId);
  }
}
