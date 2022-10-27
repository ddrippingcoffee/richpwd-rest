package rich.pwd.bean.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StFileVo {

  private String name;
  private String url;
  private String type;
  private long size;
}
