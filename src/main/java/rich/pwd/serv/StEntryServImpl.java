package rich.pwd.serv;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import rich.pwd.bean.po.ComInfo;
import rich.pwd.bean.po.StEntry;
import rich.pwd.bean.vo.StEntryVo;
import rich.pwd.bean.vo.StFileVo;
import rich.pwd.repo.StEntryDao;
import rich.pwd.serv.intf.ComInfoServ;
import rich.pwd.serv.intf.StEntryServ;
import rich.pwd.serv.intf.StFileDbServ;
import rich.pwd.serv.intf.StFileFdServ;
import rich.pwd.util.Key;

import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StEntryServImpl extends BaseServImpl<StEntry, Long, StEntryDao> implements StEntryServ {

  private final ComInfoServ comInfoServ;
  private final StFileDbServ stFileDbServ;
  private final StFileFdServ stFileFdServ;

  public StEntryServImpl(StEntryDao repository,
                         ComInfoServ comInfoServ,
                         StFileDbServ stFileDbServ,
                         StFileFdServ stFileFdServ) {
    super(repository);
    this.comInfoServ = comInfoServ;
    this.stFileDbServ = stFileDbServ;
    this.stFileFdServ = stFileFdServ;
  }

  @Override
  public List<StEntryVo> getAllActiveEntry() {
    return super.getRepository()
            .findAllByDelDtmIsNullOrderByC8tDtmDesc()
            .stream().map(entry -> {
              ComInfo comInfo = comInfoServ.findOneBySymb(entry.getSymb());
              List<StFileVo> fileDbVos = stFileDbServ
                      .findAllBySymbAndC8tDtm(entry.getSymb(), entry.getC8tDtm())
                      .stream().map(dbFile -> {
                        String fileUrl = ServletUriComponentsBuilder
                                .fromCurrentContextPath()
                                .path("/entry/filedb/").path(Long.toString(dbFile.getUid()))
                                .toUriString();
                        return StFileVo.builder()
                                .name(dbFile.getDbFileNm())
                                .url(fileUrl)
                                .type(dbFile.getDbFileTy())
                                .size(dbFile.getDbFileData().length)
                                .build();
                      }).collect(Collectors.toList());
              List<StFileVo> fileFdVos = stFileFdServ
                      .findAllBySymbAndC8tDtm(entry.getSymb(), entry.getC8tDtm())
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
              return StEntryVo.builder()
                      .stEntry(entry)
                      .stDtlList(entry.getStDtlList())
                      .comNm(comInfo.getComNm())
                      .comType(comInfo.getComType())
                      .comIndus(comInfo.getComIndus())
                      .fileDbVos(fileDbVos)
                      .fileFdVos(fileFdVos).build();
            }).collect(Collectors.toList());
  }

  @Override
  public List<StEntry> getAllOldEntry() {
    return super.getRepository().findAllByDelDtmIsNotNullOrderByDelDtmDesc();
  }

  @Override
  @Transactional
  public int updateDeleteTimeBySymbAndC8tDtm(String symb, LocalDateTime c8tDtm) {
    return super.getRepository().updateDeleteTimeBySymbAndC8tDtm(symb, c8tDtm, LocalDateTime.now());
  }
}
