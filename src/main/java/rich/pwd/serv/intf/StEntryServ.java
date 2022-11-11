package rich.pwd.serv.intf;

import org.springframework.web.multipart.MultipartFile;
import rich.pwd.bean.po.StEntry;

import java.time.LocalDateTime;
import java.util.Map;

public interface StEntryServ extends BaseServ<StEntry, Long> {

  LocalDateTime c8tStEntry(StEntry entry,
                           MultipartFile[] fileDbs,
                           MultipartFile[] fileFds);

  Map<String, Object> getAllActiveEntry();

  Map<String, Object> getAllOldEntry();

  int updateDeleteTimeByUserIdAndSymbAndC8tDtm(String symb, LocalDateTime c8tDtm);
}
