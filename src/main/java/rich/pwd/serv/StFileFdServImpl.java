package rich.pwd.serv;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import rich.pwd.bean.po.StFileFd;
import rich.pwd.repo.StFileFdDao;
import rich.pwd.serv.intf.StFileFdServ;
import rich.pwd.util.Key;

import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class StFileFdServImpl extends BaseServImpl<StFileFd, Long, StFileFdDao> implements StFileFdServ {

  public StFileFdServImpl(StFileFdDao repository) {
    super(repository);
  }

  @Override
  public void storeOne(String symb, LocalDateTime c8tDtm, MultipartFile multipartFile) {
    String fileName = c8tDtm.format(Key.yyMMDD_HHmmss_fmt) + "_" + multipartFile.getOriginalFilename();
    StFileFd fileFd = StFileFd.builder().symb(symb).c8tDtm(c8tDtm).fdFileNm(fileName).build();
    super.getRepository().save(fileFd);
    try {
      Files.copy(multipartFile.getInputStream(),
              Key.RESOURCES_FILE_FOLDER.resolve(fileName));
    } catch (Exception e) {
      throw new RuntimeException("檔案儲存失敗: " + e.getMessage());
    }
  }

  @Override
  public void storeAll(String symb, LocalDateTime c8tDtm, MultipartFile[] multipartFiles) {
    Arrays.stream(multipartFiles).forEach(file -> {
      this.storeOne(symb, c8tDtm, file);
    });
  }

  @Override
  public List<StFileFd> findAllBySymbAndC8tDtm(String symb, LocalDateTime c8tDtm) {
    return super.getRepository().findAllBySymbAndC8tDtm(symb, c8tDtm);
  }
}
