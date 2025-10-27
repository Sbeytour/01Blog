package blog.Config;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.jsonwebtoken.security.SecurityException;
import java.util.stream.*;

import blog.dto.response.ErrorResponseDto;
import blog.exceptions.DuplicateReportException;
import blog.exceptions.InvalidCredentialsException;
import blog.exceptions.InvalidTokenException;
import blog.exceptions.ReportNotFoundException;
import blog.exceptions.SuccessException;
import blog.exceptions.UserAlreadyExistsException;
import blog.exceptions.UserNotFoundException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    //LOGIC EXCEPRIONS
    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleUserAlreadyExists(UserAlreadyExistsException exception) {
        logger.warn("user already exist: {}", exception.getMessage());

        return new ErrorResponseDto(exception.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleUserNotFound(UserNotFoundException exception) {
        logger.warn("user not found: {}", exception.getMessage());

        return new ErrorResponseDto(exception.getMessage(), HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler(ReportNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleReportNotFound(ReportNotFoundException exception) {
        logger.warn("report not found: {}", exception.getMessage());

        return new ErrorResponseDto(exception.getMessage(), HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler(DuplicateReportException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleDuplicateReport(DuplicateReportException exception) {
        logger.warn("duplicate report: {}", exception.getMessage());

        return new ErrorResponseDto(exception.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    //AUTHENTICATION && AUTHORIZATION EXCEPTIONS
    @ExceptionHandler({ BadCredentialsException.class, InvalidCredentialsException.class })
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponseDto handleInvalidCredentils(InvalidCredentialsException exception) {
        logger.warn("Invalid Credentials: {}", exception.getMessage());

        return new ErrorResponseDto(exception.getMessage(), HttpStatus.UNAUTHORIZED.value());
    }

    //VALIDATION EXCEPTION
    @ExceptionHandler(SuccessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleSuccessException(SuccessException exception) {
        logger.warn("Invalid Credentials: {}", exception.getMessage());

        return new ErrorResponseDto(exception.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleValidationError(MethodArgumentNotValidException exception) {
        String fieldsErrors = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage()).collect(Collectors.joining("; "));

        return new ErrorResponseDto(fieldsErrors, HttpStatus.BAD_REQUEST.value());
    }

    //GENERIC EXCEPTIONS
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleGeniricExceptions(Exception exception) {
        logger.warn("Generic Exception Error: {}", exception.getMessage());

        return new ErrorResponseDto(exception.getMessage() + ": An unexpected error occurred. Please try again later",
                HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    // JWT EXCEPTIONS:
    @ExceptionHandler({ ExpiredJwtException.class })
    public ErrorResponseDto handleExpiredJwtException(ExpiredJwtException exception) {

        logger.warn("JWT token expired: {}", exception.getMessage());

        return new ErrorResponseDto("JWT token has expired", HttpStatus.UNAUTHORIZED.value());
    }

    @ExceptionHandler({ MalformedJwtException.class, SecurityException.class, InvalidTokenException.class })
    public ErrorResponseDto handleInvalidJwtException(Exception exception) {

        logger.warn("Invalid JWT token: {}", exception.getMessage());

        return new ErrorResponseDto("Invalid token", HttpStatus.UNAUTHORIZED.value());
    }
}
