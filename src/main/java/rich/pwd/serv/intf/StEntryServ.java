package rich.pwd.serv.intf;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.web.multipart.MultipartFile;
import rich.pwd.bean.dto.proj.StEntryCountProj;
import rich.pwd.bean.po.StEntry;

import java.time.LocalDateTime;
import java.util.Map;

public interface StEntryServ extends BaseServ<StEntry, Long> {

  LocalDateTime c8tStEntry(StEntry entry, MultipartFile[] fileFds);

  Page<StEntryCountProj> getTotalEntryPage(int page, int size);

  Slice<StEntryCountProj> getTotalEntryByFuzzySymbSlice(String symb, int page, int size);

  Slice<StEntryCountProj> getTotalEntryByFuzzyComNmSlice(String comNm, int page, int size);

  Page<StEntry> getOneEntryPage(String symb, int page, int size);

  Map<String, Object> getEntryFileList(String symb, LocalDateTime c8tDtm);

  int updateDeleteTimeByUserIdAndSymbAndC8tDtm(String symb, LocalDateTime c8tDtm);
}
