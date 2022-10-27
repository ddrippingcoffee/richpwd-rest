package rich.pwd.serv.intf;

import org.springframework.web.multipart.MultipartFile;
import rich.pwd.bean.po.StFileDb;

import java.time.LocalDateTime;
import java.util.List;

public interface StFileDbServ extends BaseServ<StFileDb, Long> {

  void storeOne(String symb, LocalDateTime c8tDtm, MultipartFile multipartFile);

  void storeAll(String symb, LocalDateTime c8tDtm, MultipartFile[] multipartFile);

  List<StFileDb> findAllBySymbAndC8tDtm(String symb, LocalDateTime c8tDtm);
}
