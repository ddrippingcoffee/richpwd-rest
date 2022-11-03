package rich.pwd.serv;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.web.multipart.MultipartFile;
import rich.pwd.bean.po.StFileFd;
import rich.pwd.bean.vo.StFileVo;
import rich.pwd.config.jwt.JwtUtils;
import rich.pwd.repo.StFileFdDao;
import rich.pwd.serv.intf.StFileFdServ;
import rich.pwd.util.Key;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StFileFdServImpl extends BaseServImpl<StFileFd, Long, StFileFdDao> implements StFileFdServ {

  private static final String IMAGE_TYPE = "image";
  private final JwtUtils jwtUtils;

  public StFileFdServImpl(StFileFdDao repository, JwtUtils jwtUtils) {
    super(repository);
    this.jwtUtils = jwtUtils;
  }

  @Override
  public void storeOne(Long userId, String symb, LocalDateTime c8tDtm, MultipartFile multipartFile) {
    String fileName = c8tDtm.format(Key.yyMMDD_HHmmss_fmt) + "_" + multipartFile.getOriginalFilename();
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
    Arrays.stream(multipartFiles).forEach(file -> {
      this.storeOne(userId, symb, c8tDtm, file);
    });
  }

  @Override
  public List<StFileVo> findAllActiveFdFile(String symb, LocalDateTime c8tDtm) {
    return super.getRepository()
            .findAllByUserIdAndSymbAndC8tDtm(jwtUtils.getUserIdFromAuthentication(), symb, c8tDtm)
            .stream().map(fdFile -> {
              Path file = Key.RESOURCES_FILE_FOLDER.resolve(fdFile.getFdFileNm());
              long contentLength = -1;
              String base64ImgStr = null;
              try {
                Resource resource = new UrlResource(file.toUri());
                contentLength = resource.contentLength();
                if (IMAGE_TYPE.equals(fdFile.getFdFileTy().substring(0, 5))) {
                  base64ImgStr = "data:" +
                          fdFile.getFdFileTy() +
                          ";base64," +
                          Base64Utils.encodeToString(resource.getInputStream().readAllBytes());
                }
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
              return StFileVo.builder()
                      .fileUid(String.valueOf(fdFile.getUid()))
                      .name(fdFile.getFdFileNm().substring(15))
                      .type(fdFile.getFdFileTy())
                      .size(contentLength)
                      .base64ImgStr(base64ImgStr)
                      .build();
            }).collect(Collectors.toList());
  }
}
