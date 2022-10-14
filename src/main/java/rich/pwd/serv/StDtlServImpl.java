package rich.pwd.serv;

import org.springframework.stereotype.Service;
import rich.pwd.bean.po.StDtl;
import rich.pwd.repo.StDtlDao;
import rich.pwd.serv.intf.StDtlServ;

@Service
public class StDtlServImpl extends BaseServImpl<StDtl, Long, StDtlDao> implements StDtlServ {

  public StDtlServImpl(StDtlDao repository) {
    super(repository);
  }
}
