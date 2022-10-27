package rich.pwd.serv;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
