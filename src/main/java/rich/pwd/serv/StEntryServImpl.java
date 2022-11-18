package rich.pwd.serv;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import rich.pwd.bean.dto.proj.StFileDbProj;
import rich.pwd.bean.dto.proj.StFileFdProj;
import rich.pwd.bean.po.ComInfo;
import rich.pwd.bean.po.StEntry;
import rich.pwd.bean.vo.StEntryVo;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    List<StEntry> stEntryList = super.getRepository()
            .findAllByUserIdAndDelDtmIsNullOrderByC8tDtmDesc(jwtUtils.getUserIdFromAuthentication());
    return Map.of("stEntryList", c8tEntryMiscData(stEntryList),
            "limitPerFile", AppProperties.MaxUploadSizePerFile,
            "limitPerReq", AppProperties.MaxUploadSizePerRequest);
  }

  @Override
  public Map<String, Object> getAllOldEntry() {
    List<StEntry> stEntryList = super.getRepository()
            .findAllByUserIdAndDelDtmIsNotNullOrderByDelDtmDesc(jwtUtils.getUserIdFromAuthentication());
    return Map.of("stEntryList", c8tEntryMiscData(stEntryList));
  }

  @Override
  public Page<StEntry> findAllActiveEntryPage(int page, int size, String desc) {
    return super.getRepository().findAllByUserIdAndDelDtmIsNull(
            jwtUtils.getUserIdFromAuthentication(),
            PageRequest.of(page, size, "desc".equals(desc) ? Sort.Direction.DESC : Sort.Direction.ASC, "c8tDtm")
    );
  }

  @Override
  public Page<StEntry> findAllOldEntryPage(int page, int size, String desc) {
    return super.getRepository().findAllByUserIdAndDelDtmIsNotNull(
            jwtUtils.getUserIdFromAuthentication(),
            PageRequest.of(page, size, "desc".equals(desc) ? Sort.Direction.DESC : Sort.Direction.ASC, "delDtm")
    );
  }

  @Override
  public Map<String, Object> getEntryFileList(String symb, LocalDateTime c8tDtm) {
    List<StFileDbProj> fileDbInfoList =
            stFileDbServ.findAllActiveDbFileInfo(symb, c8tDtm);
    List<StFileFdProj> fileFdInfoList =
            stFileFdServ.findAllActiveFdFileInfo(symb, c8tDtm);
    return Map.of("fileDbInfoList", fileDbInfoList,
            "fileFdInfoList", fileFdInfoList);
  }

  @Override
  public Slice<StEntry> findAllBySymbSlice(String symb, int page, int size, String desc) {
    List<String> symbList = comInfoServ.findAllBySymbContaining(symb)
            .stream().map(ComInfo::getSymb).collect(Collectors.toList());
    return super.getRepository()
            .findAllByUserIdAndSymbIn(
                    jwtUtils.getUserIdFromAuthentication(),
                    symbList,
                    PageRequest.of(page, size, "desc".equals(desc) ? Sort.Direction.DESC : Sort.Direction.ASC, "c8tDtm")
            );
  }

  @Override
  public Slice<StEntry> findAllByComNmSlice(String comNm, int page, int size, String desc) {
    List<String> symbList = comInfoServ.findAllByComNmContaining(comNm)
            .stream().map(ComInfo::getSymb).collect(Collectors.toList());
    return super.getRepository()
            .findAllByUserIdAndSymbIn(
                    jwtUtils.getUserIdFromAuthentication(),
                    symbList,
                    PageRequest.of(page, size, "desc".equals(desc) ? Sort.Direction.DESC : Sort.Direction.ASC, "c8tDtm")
            );
  }

  private List<StEntryVo> c8tEntryMiscData(List<StEntry> entryList) {
    List<ComInfo> comInfoList = comInfoServ.findAll();
    return entryList.stream().map(entry -> {
      List<StFileDbProj> fileDbInfoList = stFileDbServ
              .findAllActiveDbFileInfo(entry.getSymb(), entry.getC8tDtm());
      List<StFileFdProj> fileFdInfoList = stFileFdServ
              .findAllActiveFdFileInfo(entry.getSymb(), entry.getC8tDtm());
      StEntryVo entryVo = StEntryVo.builder()
              .stEntry(entry)
              .fileDbInfoList(fileDbInfoList)
              .fileFdInfoList(fileFdInfoList)
              .build();
      Optional<ComInfo> comInfoOp = comInfoList.stream()
              .filter(info -> info.getSymb().equals(entry.getSymb()))
              .findAny();
      comInfoOp.ifPresentOrElse(comInfo -> {
        entryVo.setComNm(comInfo.getComNm());
        entryVo.setComType(comInfo.getComType());
        entryVo.setComIndus(comInfo.getComIndus());
      }, () -> {
        entryVo.setComNm("N.A.");
        entryVo.setComType("N.A.");
        entryVo.setComIndus("N.A.");
      });
      return entryVo;
    }).collect(Collectors.toList());
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