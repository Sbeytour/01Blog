package blog.Config;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import blog.dto.response.ErrorResponseDto;
import blog.exceptions.DuplicateReportException;
import blog.exceptions.InvalidCredentialsException;
import blog.exceptions.InvalidFileSizeException;
import blog.exceptions.InvalidTokenException;
import blog.exceptions.ReportNotFoundException;
import blog.exceptions.ResourceNotFoundException;
import blog.exceptions.UserAlreadyExistsException;
import blog.exceptions.UserNotFoundException;
import blog.exceptions.UserisBannedException;
import blog.exceptions.ValidationException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SecurityException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //LOGIC EXCEPRIONS
    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleUserAlreadyExists(UserAlreadyExistsException exception) {

        return new ErrorResponseDto(exception.getMessage(), HttpStatus.BAD_REQUEST.value(), "Bad Request, User already exist");
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleUserNotFound(UserNotFoundException exception) {

        return new ErrorResponseDto(exception.getMessage(), HttpStatus.NOT_FOUND.value(), "User not found");
    }

    @ExceptionHandler(ReportNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleReportNotFound(ReportNotFoundException exception) {

        return new ErrorResponseDto(exception.getMessage(), HttpStatus.NOT_FOUND.value(), "Report not found");
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleResourceNotFound(ResourceNotFoundException exception) {

        return new ErrorResponseDto(exception.getMessage(), HttpStatus.NOT_FOUND.value(), "Resource not found");
    }

    @ExceptionHandler(DuplicateReportException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleDuplicateReport(DuplicateReportException exception) {

        return new ErrorResponseDto(exception.getMessage(), HttpStatus.BAD_REQUEST.value(), "You Already report this content");
    }

    @ExceptionHandler(InvalidFileSizeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleInvalidFileSize(InvalidFileSizeException exception) {

        return new ErrorResponseDto(exception.getMessage(), HttpStatus.BAD_REQUEST.value(), "Invalid file Size");
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleMaxUploadSizeExceeded(MaxUploadSizeExceededException exception) {

        return new ErrorResponseDto("File size Error", HttpStatus.BAD_REQUEST.value(), "Max Size must be less than 50MB");
    }

    //AUTHENTICATION && AUTHORIZATION EXCEPTIONS
    @ExceptionHandler({BadCredentialsException.class, InvalidCredentialsException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponseDto handleInvalidCredentils(InvalidCredentialsException exception) {

        return new ErrorResponseDto(exception.getMessage(), HttpStatus.UNAUTHORIZED.value(), "Your credentials are invalid");
    }

    @ExceptionHandler(UserisBannedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponseDto handleUserisBanned(UserisBannedException exception) {

        return new ErrorResponseDto(exception.getMessage(), HttpStatus.FORBIDDEN.value(), "Your acount has been banned");
    }

    @ExceptionHandler(DisabledException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponseDto handleDisabled(DisabledException exception) {

        return new ErrorResponseDto(exception.getMessage(), HttpStatus.FORBIDDEN.value(), "Your account has been banned");
    }

    //VALIDATION EXCEPTION
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleValidationException(ValidationException exception) {

        return new ErrorResponseDto(exception.getMessage(), HttpStatus.BAD_REQUEST.value(), "Validation failed");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleValidationError(MethodArgumentNotValidException exception) {
        String fieldsErrors = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage()).collect(Collectors.joining("; "));

        return new ErrorResponseDto(fieldsErrors, HttpStatus.BAD_REQUEST.value(), "there is an error at your credentiel");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleTypeMismatch(MethodArgumentTypeMismatchException exception) {
        return new ErrorResponseDto(
                String.format("Invalid value for parameter"),
                HttpStatus.BAD_REQUEST.value(),
                "Invalid parameter type"
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ErrorResponseDto handleMethodNotAllowed(HttpRequestMethodNotSupportedException exception) {
        return new ErrorResponseDto(
                String.format("Method Not Allowed for this endpoint."), HttpStatus.METHOD_NOT_ALLOWED.value(), "Method Not Allowed"
        );
    }

    //GENERIC EXCEPTIONS
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleGeniricExceptions(Exception exception) {

        return new ErrorResponseDto("Internal server error",
                HttpStatus.INTERNAL_SERVER_ERROR.value(), ": An unexpected error occurred. Please try again later");
    }

    // JWT EXCEPTIONS:
    @ExceptionHandler({ExpiredJwtException.class})
    public ErrorResponseDto handleExpiredJwtException(ExpiredJwtException exception) {

        return new ErrorResponseDto("JWT Expiration", HttpStatus.UNAUTHORIZED.value(), "Token has been expired");
    }

    @ExceptionHandler({MalformedJwtException.class, SecurityException.class, InvalidTokenException.class})
    public ErrorResponseDto handleInvalidJwtException(Exception exception) {

        return new ErrorResponseDto("Invalid token", HttpStatus.UNAUTHORIZED.value(), "Token is invalid");
    }
}
