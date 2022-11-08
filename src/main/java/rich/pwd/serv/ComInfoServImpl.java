package rich.pwd.serv;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import rich.pwd.bean.po.ComInfo;
import rich.pwd.ex.ResourceNotFoundException;
import rich.pwd.repo.ComInfoDao;
import rich.pwd.serv.intf.ComInfoServ;

import java.util.List;

@Service
public class ComInfoServImpl extends BaseServImpl<ComInfo, Long, ComInfoDao> implements ComInfoServ {

  public ComInfoServImpl(ComInfoDao repository) {
    super(repository);
  }

  @Override
  public void store(ComInfo comInfo) {
    try {
      super.getRepository().save(comInfo);
    } catch (DataIntegrityViolationException ex) {
      throw new RuntimeException(ex.getMostSpecificCause().getMessage());
    }
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

  @Override
  @Transactional(
          timeout = 20,
          readOnly = false,
          isolation = Isolation.DEFAULT,
          propagation = Propagation.REQUIRED,
          rollbackFor = Exception.class)
  public void updateBySymb(String symb, ComInfo comInfo) {
    // p 54
    // 詳細瞭解 Spring Transaction 的 Propagation
    // https://www.tpisoftware.com/tpu/articleDetails/2741
    int rslt = super.getRepository().deleteComInfoBySymb(symb);
    if (0 == rslt) {
      throw new ResourceNotFoundException("Not Found By Company Symbol.");
    } else {
      super.flush();
      comInfo.setSymb(symb);
      super.save(comInfo);
    }
  }

  @Override
  @Transactional
  public int deleteComInfoBySymb(String symb) {
    int rslt = super.getRepository().deleteComInfoBySymb(symb);
    if (0 == rslt) {
      throw new ResourceNotFoundException("Not Found By Company Symbol.");
    }
    return rslt;
  }
}
