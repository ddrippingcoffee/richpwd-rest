package rich.pwd.bean.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import rich.pwd.bean.po.StDtl;
import rich.pwd.bean.po.StEntry;

import java.util.List;

@Data
@AllArgsConstructor
public class StComEntryVo {

  private StEntry stEntry;
  private List<StDtl> stDtlList;

  private String comNm;
  private String comType;
  private String comIndus;
}
