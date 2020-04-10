package ch.uzh.ifi.seal.soprafs20.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice(annotations = RestController.class)
public class GlobalExceptionAdvice extends ResponseEntityExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(GlobalExceptionAdvice.class);

    @ExceptionHandler(value = {IllegalArgumentException.class, IllegalStateException.class})
    protected ResponseEntity<Object> handleConflictException(RuntimeException ex, WebRequest request) {
        String bodyOfResponse = "This should be application specific";
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
        String message = "AuthenticationException raise UNAUTHORIZED: %s";
        return formatHelper(ex, message, HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(NotFoundException ex, WebRequest request) {
        String message = "NotFoundException raise NOT_FOUND: %s";
        return formatHelper(ex, message, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<Object> handleBadRequestException(ServiceException ex, WebRequest request) {
        String message = "ServiceException raise BAD_REQUEST: %s";
        return formatHelper(ex, message, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseStatusException handleTransactionSystemException(Exception ex, HttpServletRequest request) {
        log.error("Request: {} raised: {}", request.getRequestURL(), ex.getMessage());
        return new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage(), ex);
    }

    // Keep this one disable for all testing purposes -> it shows more detail with this one disabled
    @ExceptionHandler(HttpServerErrorException.InternalServerError.class)
    public ResponseStatusException handleException(Exception ex) {
        log.error("Default Exception Handler -> caught:", ex);
        return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex);
    }

    private ResponseEntity<Object> formatHelper(Exception ex, String message, HttpStatus status, WebRequest request) {
        String exceptionMessage = String.format(message, ex.getMessage());
        log.error("Request: {} raised {}", request.getDescription(false), exceptionMessage);
        return handleExceptionInternal(ex, exceptionMessage, new HttpHeaders(), status, request);
    }
}
