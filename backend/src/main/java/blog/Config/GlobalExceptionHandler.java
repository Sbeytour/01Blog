package blog.Config;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.stream.*;

import blog.dto.ErrorResponseDto;
import blog.exceptions.InvalidCredentials;
import blog.exceptions.SuccessException;
import blog.exceptions.UserAlreadyExists;
import blog.exceptions.UserNotFound;

public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserAlreadyExists.class)
    public ErrorResponseDto handleUserAlreadyExists(UserAlreadyExists exception) {
        logger.warn("user already exist: {}", exception.getMessage());

        return new ErrorResponseDto(exception.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(UserNotFound.class)
    public ErrorResponseDto handleUserNotFound(UserNotFound exception) {
        logger.warn("user not found: {}", exception.getMessage());

        return new ErrorResponseDto(exception.getMessage(), HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler({ BadCredentialsException.class, InvalidCredentials.class })
    public ErrorResponseDto handleInvalidCredentils(InvalidCredentials exception) {
        logger.warn("Invalid Credentials: {}", exception.getMessage());

        return new ErrorResponseDto(exception.getMessage(), HttpStatus.UNAUTHORIZED.value());
    }

    @ExceptionHandler(SuccessException.class)
    public ErrorResponseDto handleSuccessException(SuccessException exception){
        logger.warn("Invalid Credentials: {}", exception.getMessage());

        return new ErrorResponseDto(exception.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponseDto handleValidationError(MethodArgumentNotValidException exception) {
        String fieldsErrors = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage()).collect(Collectors.joining("; "));

        return new ErrorResponseDto(fieldsErrors, HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(Exception.class)
    public ErrorResponseDto handleGeniricExceptions(Exception exception) {
        logger.warn("Generic Exception Error: {}", exception.getMessage());

        return new ErrorResponseDto(exception.getMessage() + ": An unexpected error occurred. Please try again later",
                HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
