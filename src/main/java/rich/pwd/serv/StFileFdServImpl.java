package rich.pwd.serv;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import rich.pwd.bean.po.StFileFd;
import rich.pwd.bean.vo.StFileVo;
import rich.pwd.repo.StFileFdDao;
import rich.pwd.serv.intf.StFileFdServ;
import rich.pwd.util.Key;

import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
  public List<StFileVo> findAllActiveFdFile(String symb, LocalDateTime c8tDtm) {
    return super.getRepository().findAllBySymbAndC8tDtm(symb, c8tDtm)
            .stream().map(fdFile -> {
              Path file = Key.RESOURCES_FILE_FOLDER.resolve(fdFile.getFdFileNm());
              long contentLength = -1;
              try {
                Resource resource = new UrlResource(file.toUri());
                contentLength = resource.contentLength();
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
              String fileUrl = ServletUriComponentsBuilder
                      .fromCurrentContextPath()
                      .path("/entry/filefd/").path(Long.toString(fdFile.getUid()))
                      .toUriString();
              FileNameMap fileNameMap = URLConnection.getFileNameMap();
              String mimeType = fileNameMap.getContentTypeFor(fdFile.getFdFileNm());
              return StFileVo.builder()
                      .name(fdFile.getFdFileNm())
                      .url(fileUrl)
                      .type(mimeType)
                      .size(contentLength)
                      .build();
            }).collect(Collectors.toList());
  }
}
