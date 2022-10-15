package rich.pwd.config.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import rich.pwd.bean.dto.payload.ErrorMessage;
import rich.pwd.ex.TokenRefreshException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class TokenContrAdvice {

  @ExceptionHandler(value = TokenRefreshException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ErrorMessage handleTokenRefreshException(TokenRefreshException ex, WebRequest request) {
    return new ErrorMessage(
            HttpStatus.FORBIDDEN.value(),
            LocalDateTime.now(),
            ex.getMessage(),
            request.getDescription(false));
  }
}
