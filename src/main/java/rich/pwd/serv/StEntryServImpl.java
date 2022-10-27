package rich.pwd.serv;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import rich.pwd.bean.po.ComInfo;
import rich.pwd.bean.po.StEntry;
import rich.pwd.bean.vo.StComEntryVo;
import rich.pwd.bean.vo.StEntryVo;
import rich.pwd.bean.vo.StFileDbVo;
import rich.pwd.repo.StEntryDao;
import rich.pwd.serv.intf.ComInfoServ;
import rich.pwd.serv.intf.StEntryServ;
import rich.pwd.serv.intf.StFileDbServ;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StEntryServImpl extends BaseServImpl<StEntry, Long, StEntryDao> implements StEntryServ {

  private final ComInfoServ comInfoServ;
  private final StFileDbServ stFileDbServ;

  public StEntryServImpl(StEntryDao repository,
                         ComInfoServ comInfoServ,
                         StFileDbServ stFileDbServ) {
    super(repository);
    this.comInfoServ = comInfoServ;
    this.stFileDbServ = stFileDbServ;
  }

  @Override
  public List<StEntryVo> getAllActiveEntry() {
    return super.getRepository()
            .findAllByDelDtmIsNullOrderByC8tDtmDesc()
            .stream().map(entry -> {
              ComInfo comInfo = comInfoServ.findOneBySymb(entry.getSymb());
              List<StFileDbVo> fileDbVos = stFileDbServ
                      .findAllBySymbAndC8tDtm(entry.getSymb(), entry.getC8tDtm())
                      .stream().map(dbFile -> {
                        String fileUrl = ServletUriComponentsBuilder
                                .fromCurrentContextPath()
                                .path("/entry/filedb/").path(Long.toString(dbFile.getUid()))
                                .toUriString();
                        return StFileDbVo.builder()
                                .name(dbFile.getDbFileNm())
                                .url(fileUrl)
                                .type(dbFile.getDbFileTy())
                                .size(dbFile.getDbFileData().length)
                                .build();
                      }).collect(Collectors.toList());
              return StEntryVo.builder()
                      .stEntry(entry)
                      .stDtlList(entry.getStDtlList())
                      .comNm(comInfo.getComNm())
                      .comType(comInfo.getComType())
                      .comIndus(comInfo.getComIndus())
                      .fileDbVos(fileDbVos).build();
            }).collect(Collectors.toList());
  }

  @Override
  public List<StComEntryVo> getAllActiveComEntry() {
    List<StEntry> entryList = super.getRepository()
            .findAllByDelDtmIsNullOrderByC8tDtmDesc();
    return entryList.stream().map(entry -> {
      ComInfo comInfo = comInfoServ.findOneBySymb(entry.getSymb());
      return new StComEntryVo(entry,
              entry.getStDtlList(),
              comInfo.getComNm(),
              comInfo.getComType(),
              comInfo.getComIndus());
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
