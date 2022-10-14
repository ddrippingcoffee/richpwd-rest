package rich.pwd.serv;

import org.springframework.stereotype.Service;
import rich.pwd.bean.po.ComInfo;
import rich.pwd.repo.ComInfoDao;
import rich.pwd.serv.intf.ComInfoServ;

import java.util.List;

@Service
public class ComInfoServImpl extends BaseServImpl<ComInfo, Long, ComInfoDao> implements ComInfoServ {

  public ComInfoServImpl(ComInfoDao repository) {
    super(repository);
  }

  @Override
  public ComInfo findOneBySymb(String symb) {
    return super.getRepository().findComInfoBySymb(symb);
  }

  @Override
  public ComInfo findOneByComNm(String nm) {
    return super.getRepository().findComInfoByComNm(nm);
  }

  @Override
  public List<ComInfo> findAllByComIndus(String indus) {
    return super.getRepository().findComInfosByComIndus(indus);
  }
}
