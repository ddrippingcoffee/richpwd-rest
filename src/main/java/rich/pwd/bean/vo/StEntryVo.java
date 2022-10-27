package rich.pwd.bean.vo;

import lombok.Builder;
import lombok.Data;
import rich.pwd.bean.po.StDtl;
import rich.pwd.bean.po.StEntry;

import java.util.List;

@Data
@Builder
public class StEntryVo {

  private StEntry stEntry;
  private List<StDtl> stDtlList;

  private String comNm;
  private String comType;
  private String comIndus;

  private List<StFileDbVo> fileDbVos;

  private List<StFileDbVo> fileFdVos;
}
