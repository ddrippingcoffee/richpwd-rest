package rich.pwd.serv;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import rich.pwd.bean.po.ComInfo;
import rich.pwd.bean.po.StEntry;
import rich.pwd.bean.vo.StEntryVo;
import rich.pwd.bean.vo.StFileVo;
import rich.pwd.repo.StEntryDao;
import rich.pwd.serv.intf.ComInfoServ;
import rich.pwd.serv.intf.StEntryServ;
import rich.pwd.serv.intf.StFileDbServ;
import rich.pwd.serv.intf.StFileFdServ;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StEntryServImpl extends BaseServImpl<StEntry, Long, StEntryDao> implements StEntryServ {

  private final ComInfoServ comInfoServ;
  private final StFileDbServ stFileDbServ;
  private final StFileFdServ stFileFdServ;

  private final ObjectMapper objectMapper;

  public StEntryServImpl(StEntryDao repository,
                         ComInfoServ comInfoServ,
                         StFileDbServ stFileDbServ,
                         StFileFdServ stFileFdServ,
                         ObjectMapper objectMapper) {
    super(repository);
    this.comInfoServ = comInfoServ;
    this.stFileDbServ = stFileDbServ;
    this.stFileFdServ = stFileFdServ;
    this.objectMapper = objectMapper;
  }

  @Override
  @Transactional
  public LocalDateTime c8tStEntry(String entryStr,
                                  MultipartFile[] fileDbs,
                                  MultipartFile[] fileFds) {
    StEntry entry = null;
    try {
      entry = objectMapper.readValue(entryStr, StEntry.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    entry.setC8tDtm(LocalDateTime.now());
    this.save(entry);
    if (null != fileDbs) {
      stFileDbServ.storeAll(entry.getSymb(), entry.getC8tDtm(), fileDbs);
    }
    if (null != fileFds) {
      stFileFdServ.storeAll(entry.getSymb(), entry.getC8tDtm(), fileFds);
    }
    return entry.getC8tDtm();
  }

  @Override
  public List<StEntryVo> getAllActiveEntry() {
    return super.getRepository()
            .findAllByDelDtmIsNullOrderByC8tDtmDesc()
            .stream().map(entry -> {
              ComInfo comInfo = comInfoServ.findOneBySymb(entry.getSymb());
              List<StFileVo> fileDbVos = stFileDbServ.findAllActiveDbFile(entry.getSymb(), entry.getC8tDtm());
              List<StFileVo> fileFdVos = stFileFdServ.findAllActiveFdFile(entry.getSymb(), entry.getC8tDtm());
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
