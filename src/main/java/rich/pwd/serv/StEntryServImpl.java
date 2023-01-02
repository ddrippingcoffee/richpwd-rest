package rich.pwd.serv;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import rich.pwd.bean.dto.proj.StEntryCountProj;
import rich.pwd.bean.po.ComInfo;
import rich.pwd.bean.po.StEntry;
import rich.pwd.config.jwt.JwtUtils;
import rich.pwd.ex.BadRequestException;
import rich.pwd.ex.ResourceNotFoundException;
import rich.pwd.repo.StEntryDao;
import rich.pwd.serv.intf.ComInfoServ;
import rich.pwd.serv.intf.StEntryServ;
import rich.pwd.serv.intf.StFileFdServ;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StEntryServImpl extends BaseServImpl<StEntry, Long, StEntryDao> implements StEntryServ {

  private final JwtUtils jwtUtils;
  private final ComInfoServ comInfoServ;
  private final StFileFdServ stFileFdServ;

  public StEntryServImpl(JwtUtils jwtUtils,
                         StEntryDao repository,
                         ComInfoServ comInfoServ,
                         StFileFdServ stFileFdServ) {
    super(repository);
    this.jwtUtils = jwtUtils;
    this.comInfoServ = comInfoServ;
    this.stFileFdServ = stFileFdServ;
  }

  @Override
  @Transactional
  public LocalDateTime c8tStEntry(StEntry entry, MultipartFile[] fileFds) {

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
    if (null != fileFds) {
      stFileFdServ.storeAll(entry.getSymb(), entry.getC8tDtm(), fileFds);
    }
    return entry.getC8tDtm();
  }

  @Override
  public Page<StEntryCountProj> getTotalEntryPage(int page, int size) {
    return super.getRepository().findTotalEntry(
            jwtUtils.getUserIdFromAuthentication(),
            PageRequest.of(page, size)
    );
  }

  @Override
  public Slice<StEntryCountProj> getTotalEntryByFuzzySymbSlice(String symb, int page, int size) {
    return super.getRepository().findTotalEntryByFuzzySymb(
            jwtUtils.getUserIdFromAuthentication(),
            symb,
            PageRequest.of(page, size)
    );
  }

  @Override
  public Slice<StEntryCountProj> getTotalEntryByFuzzyComNmSlice(String comNm, int page, int size) {
    List<String> symbList = comInfoServ.findAllByComNmContaining(comNm)
            .stream().map(ComInfo::getSymb).collect(Collectors.toList());
    return super.getRepository().findTotalEntryBySymbList(
            jwtUtils.getUserIdFromAuthentication(),
            symbList,
            PageRequest.of(page, size)
    );
  }

  @Override
  public Page<StEntry> getOneEntryPage(String symb, int page, int size) {
    return super.getRepository().findAllByUserIdAndSymbOrderByC8tDtmDesc(
            jwtUtils.getUserIdFromAuthentication(),
            symb,
            PageRequest.of(page, size)
    );
  }

  @Override
  public Map<String, Object> getEntryFileList(String symb, LocalDateTime c8tDtm) {
    return Map.of("fileFdInfoList", stFileFdServ.findAllActiveFdFileInfo(symb, c8tDtm));
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