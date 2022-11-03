package rich.pwd.config.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import rich.pwd.config.jwt.bean.dto.ErrorMessage;
import rich.pwd.config.YmlProperties;
import rich.pwd.ex.ResourceNotFoundException;
import rich.pwd.config.jwt.ex.TokenRefreshException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ContrAdvice {

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
          " Each file max size: " + YmlProperties.MaxUploadSizePerFile +
          ", Request file max size: " + YmlProperties.MaxUploadSizePerRequest;

  @ExceptionHandler(value = MaxUploadSizeExceededException.class)
  @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
  public ErrorMessage handleMaxSizeException(WebRequest request) {
    return new ErrorMessage(
            HttpStatus.EXPECTATION_FAILED.value(),
            LocalDateTime.now(),
            FILE_TOO_LARGE_MESSAGE,
            request.getDescription(false));
  }
}
