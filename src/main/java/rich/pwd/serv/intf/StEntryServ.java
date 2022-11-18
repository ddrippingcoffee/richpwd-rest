package rich.pwd.serv.intf;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
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

  Page<StEntry> findAllActiveEntryPage(int page, int size, String desc);

  Page<StEntry> findAllOldEntryPage(int page, int size, String desc);

  Map<String, Object> getEntryFileList(String symb, LocalDateTime c8tDtm);

  Slice<StEntry> findAllBySymbSlice(String symb, int page, int size, String desc);

  Slice<StEntry> findAllByComNmSlice(String comNm, int page, int size, String desc);

  int updateDeleteTimeByUserIdAndSymbAndC8tDtm(String symb, LocalDateTime c8tDtm);
}
