package rich.pwd.serv;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import rich.pwd.bean.po.ComInfo;
import rich.pwd.bean.po.StEntry;
import rich.pwd.bean.vo.StEntryVo;
import rich.pwd.bean.vo.StFileVo;
import rich.pwd.config.AppProperties;
import rich.pwd.config.jwt.JwtUtils;
import rich.pwd.ex.BadRequestException;
import rich.pwd.ex.ResourceNotFoundException;
import rich.pwd.repo.StEntryDao;
import rich.pwd.serv.intf.ComInfoServ;
import rich.pwd.serv.intf.StEntryServ;
import rich.pwd.serv.intf.StFileDbServ;
import rich.pwd.serv.intf.StFileFdServ;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StEntryServImpl extends BaseServImpl<StEntry, Long, StEntryDao> implements StEntryServ {

  private final JwtUtils jwtUtils;
  private final ComInfoServ comInfoServ;
  private final StFileDbServ stFileDbServ;
  private final StFileFdServ stFileFdServ;

  public StEntryServImpl(JwtUtils jwtUtils,
                         StEntryDao repository,
                         ComInfoServ comInfoServ,
                         StFileDbServ stFileDbServ,
                         StFileFdServ stFileFdServ) {
    super(repository);
    this.jwtUtils = jwtUtils;
    this.comInfoServ = comInfoServ;
    this.stFileDbServ = stFileDbServ;
    this.stFileFdServ = stFileFdServ;
  }

  @Override
  @Transactional
  public LocalDateTime c8tStEntry(StEntry entry,
                                  MultipartFile[] fileDbs,
                                  MultipartFile[] fileFds) {

    entry.getStDtlList().forEach(dtl -> {
      if ("date".equals(dtl.getDtlTy())) {
        if ("".equals(dtl.getDtlBrf()) || "".equals(dtl.getDtlInfo())) {
          throw new BadRequestException("日期 Brief 及 Information 未填");
        }
      }
      if ("link".equals(dtl.getDtlTy())) {
        if ("".equals(dtl.getDtlBrf())) {
          throw new BadRequestException("連結 Brief 未填");
        }
        if (!dtl.getDtlInfo().startsWith("http")) {
          throw new BadRequestException("連結格式不正確");
        }
      }
    });
    entry.setUserId(jwtUtils.getUserIdFromAuthentication());
    entry.setC8tDtm(LocalDateTime.now());
    this.save(entry);
    if (null != fileDbs) {
      stFileDbServ.storeAll(entry.getSymb(), entry.getC8tDtm(), fileDbs);
    }
    if (null != fileFds) {
      stFileFdServ.storeAll(entry.getSymb(), entry.getC8tDtm(), fileFds);
    }
    return entry.getC8tDtm();
  }

  @Override
  public Map<String, Object> getAllActiveEntry() {
    List<StEntryVo> stEntryList = super.getRepository()
            .findAllByUserIdAndDelDtmIsNullOrderByC8tDtmDesc(jwtUtils.getUserIdFromAuthentication())
            .stream().map(entry -> {
              ComInfo comInfo = comInfoServ.findOneBySymb(entry.getSymb());
              List<StFileVo> fileDbVos = stFileDbServ.findAllActiveDbFile(entry.getSymb(), entry.getC8tDtm());
              List<StFileVo> fileFdVos = stFileFdServ.findAllActiveFdFile(entry.getSymb(), entry.getC8tDtm());
              return StEntryVo.builder()
                      .stEntry(entry)
                      .comNm(comInfo.getComNm())
                      .comType(comInfo.getComType())
                      .comIndus(comInfo.getComIndus())
                      .fileDbVos(fileDbVos)
                      .fileFdVos(fileFdVos).build();
            }).collect(Collectors.toList());

    Map<String, Object> resultMap = new HashMap<>(16);
    resultMap.put("stEntryList", stEntryList);
    resultMap.put("limitPerFile", AppProperties.MaxUploadSizePerFile);
    resultMap.put("limitPerReq", AppProperties.MaxUploadSizePerRequest);
    return resultMap;
  }

  @Override
  @Transactional
  public int updateDeleteTimeByUserIdAndSymbAndC8tDtm(String symb, LocalDateTime c8tDtm) {
    int rslt = super.getRepository()
            .updateDeleteTimeByUserIdAndSymbAndC8tDtm(
                    jwtUtils.getUserIdFromAuthentication(), symb, c8tDtm, LocalDateTime.now());
    if (0 == rslt) {
      throw new ResourceNotFoundException("查無該筆資料\nBy Company Symbol And Create DateTime");
    }
    return rslt;
  }
}
