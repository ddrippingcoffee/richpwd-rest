package rich.pwd.bean.dto.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorMessage {

  private int statusCode;
  private LocalDateTime timestamp;
  private String message;
  private String description;
}
