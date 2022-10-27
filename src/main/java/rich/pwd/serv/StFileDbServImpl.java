package rich.pwd.serv;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import rich.pwd.bean.po.StFileDb;
import rich.pwd.repo.StFileDbDao;
import rich.pwd.serv.intf.StFileDbServ;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;

@Service
public class StFileDbServImpl extends BaseServImpl<StFileDb, Long, StFileDbDao> implements StFileDbServ {

  public StFileDbServImpl(StFileDbDao repository) {
    super(repository);
  }

  @Override
  public void storeOne(String symb, LocalDateTime c8tDtm, MultipartFile multipartFile) {
    try {
      StFileDb stFileDb = StFileDb.builder()
              .symb(symb)
              .c8tDtm(c8tDtm)
              .dbFileNm(multipartFile.getOriginalFilename())
              .dbFileTy(multipartFile.getContentType())
              .dbFileData(multipartFile.getBytes()).build();
      super.getRepository().save(stFileDb);
    } catch (IOException ioEx) {
      throw new RuntimeException("檔案儲存失敗: " + ioEx.getMessage());
    }
  }

  @Override
  public void storeAll(String symb, LocalDateTime c8tDtm, MultipartFile[] multipartFile) {
    Arrays.stream(multipartFile).forEach(file -> {
      this.storeOne(symb, c8tDtm, file);
    });
  }
}
