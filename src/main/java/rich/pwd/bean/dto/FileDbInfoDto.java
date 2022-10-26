package rich.pwd.bean.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileDbInfoDto {

  private String name;
  private String url;
  private String type;
  private long size;
}
