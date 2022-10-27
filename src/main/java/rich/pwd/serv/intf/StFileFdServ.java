package rich.pwd.serv.intf;

import org.springframework.web.multipart.MultipartFile;
import rich.pwd.bean.po.StFileFd;

import java.time.LocalDateTime;
import java.util.List;

public interface StFileFdServ extends BaseServ<StFileFd, Long> {

  void storeOne(String symb, LocalDateTime c8tDtm, MultipartFile multipartFile);

  void storeAll(String symb, LocalDateTime c8tDtm, MultipartFile[] multipartFiles);

  List<StFileFd> findAllBySymbAndC8tDtm(String symb, LocalDateTime c8tDtm);
}
