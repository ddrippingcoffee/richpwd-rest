package rich.pwd.serv;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rich.pwd.bean.po.StEntry;
import rich.pwd.repo.StEntryDao;
import rich.pwd.serv.intf.StEntryServ;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StEntryServImpl extends BaseServImpl<StEntry, Long, StEntryDao> implements StEntryServ {

  public StEntryServImpl(StEntryDao repository) {
    super(repository);
  }

  @Override
  public List<StEntry> getAllActiveEntry() {
    return super.getRepository().findAllByDelDtmIsNullOrderByC8tDtmDesc();
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
