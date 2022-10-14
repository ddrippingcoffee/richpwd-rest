package rich.pwd.serv.intf;

import rich.pwd.bean.po.ComInfo;

import java.util.List;

public interface ComInfoServ extends BaseServ<ComInfo, Long> {

  ComInfo findOneBySymb(String symb);

  ComInfo findOneByComNm(String nm);

  List<ComInfo> findAllByComIndus(String indus);
}
