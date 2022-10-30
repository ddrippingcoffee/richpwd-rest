package rich.pwd.serv;

import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.web.multipart.MultipartFile;
import rich.pwd.bean.po.StFileDb;
import rich.pwd.bean.vo.StFileVo;
import rich.pwd.repo.StFileDbDao;
import rich.pwd.serv.intf.StFileDbServ;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StFileDbServImpl extends BaseServImpl<StFileDb, Long, StFileDbDao> implements StFileDbServ {

  private static final String IMAGE_TYPE = "image";

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

  @Override
  public List<StFileVo> findAllActiveDbFile(String symb, LocalDateTime c8tDtm) {
    return super.getRepository().findAllBySymbAndC8tDtm(symb, c8tDtm)
            .stream().map(dbFile -> {
              String base64ImgStr = null;
              if (IMAGE_TYPE.equals(dbFile.getDbFileTy().substring(0, 5))) {
                base64ImgStr = "data:" +
                        dbFile.getDbFileTy() +
                        ";base64," +
                        Base64Utils.encodeToString(dbFile.getDbFileData());
              }
              return StFileVo.builder()
                      .fileUid(String.valueOf(dbFile.getUid()))
                      .name(dbFile.getDbFileNm())
                      .type(dbFile.getDbFileTy())
                      .size(dbFile.getDbFileData().length)
                      .base64ImgStr(base64ImgStr)
                      .build();
            }).collect(Collectors.toList());
  }
}
