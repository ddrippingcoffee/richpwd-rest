package rich.pwd.config.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import rich.pwd.config.AppProperties;
import rich.pwd.config.jwt.bean.dto.ErrorMessage;
import rich.pwd.config.jwt.ex.TokenRefreshException;
import rich.pwd.ex.BadRequestException;
import rich.pwd.ex.ResourceNotFoundException;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;

@RestControllerAdvice
public class ContrAdvice {

  private static final StringBuffer ERROR_STRING_BUFFER = new StringBuffer(16);

  /*
    @RestControllerAdvice
    類似攔截器
    通過 @ExceptionHandler 指定 Exception
    Controller 可共同使用

    回傳物件會自動序列化為 JSON 並傳給 ResponseBody
  */

  @ExceptionHandler(value = TokenRefreshException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ErrorMessage handleTokenRefreshException(TokenRefreshException ex, WebRequest request) {
    return new ErrorMessage(
            HttpStatus.FORBIDDEN.value(),
            LocalDateTime.now(),
            ex.getMessage(),
            request.getDescription(false));
  }

  @ExceptionHandler(value = ResourceNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorMessage handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
    return new ErrorMessage(
            HttpStatus.NOT_FOUND.value(),
            LocalDateTime.now(),
            ex.getMessage(),
            request.getDescription(false));
  }

  private final String FILE_TOO_LARGE_MESSAGE = "Error:" +
          " Each file max size: " + AppProperties.MaxUploadSizePerFile +
          ", Request file max size: " + AppProperties.MaxUploadSizePerRequest;

  @ExceptionHandler(value = MaxUploadSizeExceededException.class)
  @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
  public ErrorMessage handleMaxSizeException(WebRequest request) {
    return new ErrorMessage(
            HttpStatus.EXPECTATION_FAILED.value(),
            LocalDateTime.now(),
            FILE_TOO_LARGE_MESSAGE,
            request.getDescription(false));
  }

  @ExceptionHandler(value = BadRequestException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorMessage handleBadRequestException(BadRequestException ex, WebRequest request) {
    ERROR_STRING_BUFFER.setLength(0);
    ERROR_STRING_BUFFER.append("ERROR: ");
    ERROR_STRING_BUFFER.append(System.lineSeparator());
    ERROR_STRING_BUFFER.append(ex.getMessage());
    return new ErrorMessage(
            HttpStatus.BAD_REQUEST.value(),
            LocalDateTime.now(),
            ERROR_STRING_BUFFER.toString(),
            request.getDescription(false));
  }

  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorMessage handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
    ERROR_STRING_BUFFER.setLength(0);
    ERROR_STRING_BUFFER.append("ERROR: ");
    ex.getFieldErrors().forEach(err -> {
      ERROR_STRING_BUFFER.append(System.lineSeparator());
      ERROR_STRING_BUFFER.append(err.getField()).append(" : ");
      ERROR_STRING_BUFFER.append(err.getDefaultMessage());
    });
    return new ErrorMessage(
            HttpStatus.BAD_REQUEST.value(),
            LocalDateTime.now(),
            ERROR_STRING_BUFFER.toString(),
            request.getDescription(false));
  }

  @ExceptionHandler(value = ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorMessage handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
    ERROR_STRING_BUFFER.setLength(0);
    ERROR_STRING_BUFFER.append("ERROR: ");
    ex.getConstraintViolations().forEach(v -> {
      ERROR_STRING_BUFFER.append(System.lineSeparator());
      ERROR_STRING_BUFFER.append(v.getMessageTemplate());
    });
    return new ErrorMessage(
            HttpStatus.BAD_REQUEST.value(),
            LocalDateTime.now(),
            ERROR_STRING_BUFFER.toString(),
            request.getDescription(false));
  }
}
