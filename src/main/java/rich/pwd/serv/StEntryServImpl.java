package rich.pwd.serv;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rich.pwd.bean.po.ComInfo;
import rich.pwd.bean.po.StEntry;
import rich.pwd.bean.vo.StComEntryVo;
import rich.pwd.repo.StEntryDao;
import rich.pwd.serv.intf.ComInfoServ;
import rich.pwd.serv.intf.StEntryServ;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StEntryServImpl extends BaseServImpl<StEntry, Long, StEntryDao> implements StEntryServ {

  private final ComInfoServ comInfoServ;

  public StEntryServImpl(StEntryDao repository, ComInfoServ comInfoServ) {
    super(repository);
    this.comInfoServ = comInfoServ;
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
