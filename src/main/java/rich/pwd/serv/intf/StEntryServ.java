package rich.pwd.serv.intf;

import rich.pwd.bean.po.StEntry;
import rich.pwd.bean.vo.StComEntryVo;

import java.time.LocalDateTime;
import java.util.List;

public interface StEntryServ extends BaseServ<StEntry, Long> {

  List<StComEntryVo> getAllActiveComEntry();

  List<StEntry> getAllOldEntry();

  int updateDeleteTimeBySymbAndC8tDtm(String symb, LocalDateTime c8tDtm);
}
