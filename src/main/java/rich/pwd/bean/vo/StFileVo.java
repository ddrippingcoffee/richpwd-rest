package rich.pwd.bean.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StFileVo {

  private String fileUid;
  private String name;
  private String type;
  private long size;
  private String base64ImgStr;
}
