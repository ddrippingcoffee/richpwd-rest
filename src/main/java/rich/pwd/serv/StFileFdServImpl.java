package rich.pwd.serv;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import rich.pwd.bean.dto.proj.StFileFdProj;
import rich.pwd.bean.po.StFileFd;
import rich.pwd.config.jwt.JwtUtils;
import rich.pwd.repo.StFileFdDao;
import rich.pwd.serv.intf.StFileFdServ;
import rich.pwd.util.Key;

import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class StFileFdServImpl extends BaseServImpl<StFileFd, Long, StFileFdDao> implements StFileFdServ {

  private static final String IMAGE_TYPE = "image";
  private final JwtUtils jwtUtils;

  public StFileFdServImpl(StFileFdDao repository, JwtUtils jwtUtils) {
    super(repository);
    this.jwtUtils = jwtUtils;
  }

  @Override
  public void storeOne(Long userId, String symb, LocalDateTime c8tDtm, MultipartFile multipartFile, int seq) {
    String fileName = c8tDtm.format(Key.yyMMDD_HHmmss_fmt) + "_" + seq + "_" + multipartFile.getOriginalFilename();
    try {
      Files.copy(multipartFile.getInputStream(),
              Key.RESOURCES_FILE_FOLDER.resolve(fileName));
    } catch (Exception e) {
      throw new RuntimeException("檔案儲存失敗: " + e.getMessage());
    }
    StFileFd fileFd = StFileFd.builder()
            .userId(userId)
            .symb(symb)
            .c8tDtm(c8tDtm)
            .fdFileNm(fileName)
            .fdFileTy(multipartFile.getContentType())
            .build();
    super.getRepository().save(fileFd);
  }

  @Override
  public void storeAll(String symb, LocalDateTime c8tDtm, MultipartFile[] multipartFiles) {
    Long userId = jwtUtils.getUserIdFromAuthentication();
    for (int i = 0; i < multipartFiles.length; i++) {
      this.storeOne(userId, symb, c8tDtm, multipartFiles[i], i + 1);
    }
  }

  @Override
  public List<StFileFdProj> findAllActiveFdFileInfo(String symb, LocalDateTime c8tDtm) {
    return super.getRepository()
            .findAllByUserIdAndSymbAndC8tDtm(jwtUtils.getUserIdFromAuthentication(), symb, c8tDtm);
  }
}
