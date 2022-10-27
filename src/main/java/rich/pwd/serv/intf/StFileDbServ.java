package rich.pwd.serv.intf;

import org.springframework.web.multipart.MultipartFile;
import rich.pwd.bean.po.StFileDb;

import java.time.LocalDateTime;

public interface StFileDbServ extends BaseServ<StFileDb, Long> {

  void storeOne(String symb, LocalDateTime c8tDtm, MultipartFile multipartFile);

  void storeAll(String symb, LocalDateTime c8tDtm, MultipartFile[] multipartFile);

  // StFileDb getOneDbFile(String symb, LocalDateTime c8tDtm);
  //
  // Stream<StFileDb> getAllFiles();
}
