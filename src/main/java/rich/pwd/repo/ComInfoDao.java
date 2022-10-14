package rich.pwd.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rich.pwd.bean.po.ComInfo;

import java.util.List;

@Repository
public interface ComInfoDao extends JpaRepository<ComInfo, Long> {

  ComInfo findComInfoBySymb(String symb);

  ComInfo findComInfoByComNm(String nm);

  List<ComInfo> findComInfosByComIndus(String indus);
}
