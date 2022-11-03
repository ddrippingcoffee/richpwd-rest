package rich.pwd.serv.intf;

import org.springframework.web.multipart.MultipartFile;
import rich.pwd.bean.po.StFileFd;
import rich.pwd.bean.vo.StFileVo;

import java.time.LocalDateTime;
import java.util.List;

public interface StFileFdServ extends BaseServ<StFileFd, Long> {

  void storeOne(Long userId, String symb, LocalDateTime c8tDtm, MultipartFile multipartFile);

  void storeAll(String symb, LocalDateTime c8tDtm, MultipartFile[] multipartFiles);

  List<StFileVo> findAllActiveFdFile(String symb, LocalDateTime c8tDtm);
}
