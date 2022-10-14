package rich.pwd.serv.intf;

import rich.pwd.bean.po.StEntry;

import java.util.List;

public interface StEntryServ extends BaseServ<StEntry, Long> {

  List<StEntry> getAllActiveEntry();
  List<StEntry> getAllOldEntry();
}
