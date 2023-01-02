package rich.pwd.serv;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import rich.pwd.bean.dto.proj.StFileFdProj;
import rich.pwd.bean.po.StFileFd;
import rich.pwd.config.jwt.JwtUtils;
import rich.pwd.config.jwt.UserDetailsImpl;
import rich.pwd.repo.StFileFdDao;
import rich.pwd.serv.intf.StFileFdServ;
import rich.pwd.util.Key;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class StFileFdServImpl extends BaseServImpl<StFileFd, Long, StFileFdDao> implements StFileFdServ {

  private final JwtUtils jwtUtils;

  public StFileFdServImpl(StFileFdDao repository, JwtUtils jwtUtils) {
    super(repository);
    this.jwtUtils = jwtUtils;
  }

  @Override
  public void storeAll(String symb, LocalDateTime c8tDtm, MultipartFile[] multipartFiles) {
    // 建立歸檔資料夾 ../resources/uploads/userId_userName/symb/yyMMdd/HHmmss/..
    UserDetailsImpl userDtl = jwtUtils.getAuthentication();

    Path dirPath = Key.RESOURCES_FILE_FOLDER
            .resolve(userDtl.getId() + "_" + userDtl.getUsername())
            .resolve(symb)
            .resolve(c8tDtm.format(Key.yyMMdd_fmt))
            .resolve(c8tDtm.format(Key.HHmmss_fmt));
    try {
      Files.createDirectories(dirPath);
    } catch (IOException e) {
      throw new RuntimeException("Can't create user directory!");
    }

    // 依次複製檔案及儲存檔案資訊
    for (int i = 0; i < multipartFiles.length; i++) {
      String fileName = c8tDtm.format(Key.yyMMDD_HHmmss_fmt) + "_" + (i + 1) + "_" + multipartFiles[i].getOriginalFilename();
      try {
        Files.copy(multipartFiles[i].getInputStream(), dirPath.resolve(fileName));
      } catch (Exception e) {
        throw new RuntimeException("檔案儲存失敗: " + e.getMessage());
      }
      StFileFd fileFd = StFileFd.builder()
              .userId(userDtl.getId())
              .symb(symb)
              .c8tDtm(c8tDtm)
              .fdFileNm(fileName)
              .fdFileTy(multipartFiles[i].getContentType())
              .build();
      super.getRepository().save(fileFd);
    }
  }

  @Override
  public List<StFileFdProj> findAllActiveFdFileInfo(String symb, LocalDateTime c8tDtm) {
    return super.getRepository()
            .findAllByUserIdAndSymbAndC8tDtm(jwtUtils.getUserIdFromAuthentication(), symb, c8tDtm);
  }
}
