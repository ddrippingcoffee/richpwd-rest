package rich.pwd.serv.intf;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import rich.pwd.bean.dto.proj.ComInfoProj;
import rich.pwd.bean.po.ComInfo;

import java.util.List;

public interface ComInfoServ extends BaseServ<ComInfo, Long> {

  void store(ComInfo comInfo);

  ComInfo findOneBySymb(String symb);

  List<ComInfoProj> getComIndusList();

  List<ComInfo> findAllBySymbContaining(String symb);

  List<ComInfo> findAllByComNmContaining(String comNm);

  Page<ComInfo> findAllBySymbPage(String symb, int page, int size, String desc);

  Slice<ComInfo> findAllByComNmSlice(String comNm, int page, int size, String desc);

  Page<ComInfo> findAllByComMainPage(String comMain, int page, int size, String desc);

  Page<ComInfo> findAllByComCotedPage(String comCoted, int page, int size, String desc);

  Page<ComInfo> findAllByComCepPage(String comCep, int page, int size, String desc);

  Page<ComInfo> findAllByComIndusPage(String indus, int page, int size, String desc);

  int deleteComInfoBySymb(String symb);

  void updateBySymb(String symb, ComInfo comInfo);
}
