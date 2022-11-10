package rich.pwd.bean.vo;

import lombok.Builder;
import lombok.Data;
import rich.pwd.bean.dto.proj.StFileDbProj;
import rich.pwd.bean.dto.proj.StFileFdProj;
import rich.pwd.bean.po.StEntry;

import java.util.List;

@Data
@Builder
public class StEntryVo {

  private StEntry stEntry;

  private String comNm;
  private String comType;
  private String comIndus;

  private List<StFileDbProj> fileDbInfoList;
  private List<StFileFdProj> fileFdInfoList;
}
