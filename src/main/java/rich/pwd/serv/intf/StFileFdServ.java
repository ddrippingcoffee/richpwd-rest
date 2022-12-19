package rich.pwd.serv.intf;

import org.springframework.web.multipart.MultipartFile;
import rich.pwd.bean.dto.proj.StFileFdProj;
import rich.pwd.bean.po.StFileFd;

import java.time.LocalDateTime;
import java.util.List;

public interface StFileFdServ extends BaseServ<StFileFd, Long> {

  void storeOne(Long userId, String symb, LocalDateTime c8tDtm, MultipartFile multipartFile, int seq);

  void storeAll(String symb, LocalDateTime c8tDtm, MultipartFile[] multipartFiles);

  List<StFileFdProj> findAllActiveFdFileInfo(String symb, LocalDateTime c8tDtm);
}
