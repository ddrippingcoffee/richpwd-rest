package rich.pwd.serv;

import org.springframework.stereotype.Service;
import rich.pwd.bean.po.StEntry;
import rich.pwd.repo.StEntryDao;
import rich.pwd.serv.intf.StEntryServ;

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
}
