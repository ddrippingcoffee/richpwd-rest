package rich.pwd.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Base64Utils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import rich.pwd.bean.po.StEntry;
import rich.pwd.bean.po.StFileFd;
import rich.pwd.config.jwt.JwtUtils;
import rich.pwd.config.jwt.UserDetailsImpl;
import rich.pwd.config.jwt.bean.payload.response.MessageResponse;
import rich.pwd.ex.ResourceNotFoundException;
import rich.pwd.serv.intf.StEntryServ;
import rich.pwd.serv.intf.StFileFdServ;
import rich.pwd.util.Key;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Objects;

@RestController
@RequestMapping("entry")
@CrossOrigin(origins = "*", maxAge = 3600)
@Validated
public class StEntryContr {

  private static final String IMAGE_TYPE = "image";
  private static final String PDF_TYPE = "application/pdf";

  private final JwtUtils jwtUtils;
  private final StEntryServ stEntryServ;
  private final StFileFdServ stFileFdServ;

  @Autowired
  public StEntryContr(JwtUtils jwtUtils,
                      StEntryServ stEntryServ,
                      StFileFdServ stFileFdServ) {
    this.jwtUtils = jwtUtils;
    this.stEntryServ = stEntryServ;
    this.stFileFdServ = stFileFdServ;
  }

  @PostMapping("/stores")
  public ResponseEntity<?> storeAll(@Valid @RequestPart(value = "entryJsonStr", required = true) StEntry entry,
                                    @RequestParam(value = "fileFds", required = false) MultipartFile[] fileFds) {
    return new ResponseEntity<>(stEntryServ.c8tStEntry(entry, fileFds), HttpStatus.CREATED);
  }

  @GetMapping("/s/pg/tot")
  public ResponseEntity<?> getTotalEntryPage(
          @Min(value = 0, message = "頁數輸入錯誤") @RequestParam int page,
          @Min(value = 1, message = "最少 1 筆") @RequestParam int size) {
    return new ResponseEntity<>(
            stEntryServ.getTotalEntryPage(page, size), HttpStatus.OK);
  }

  @GetMapping("/s/pg/tot/symb")
  public ResponseEntity<?> getTotalEntryByFuzzySymbSlice(
          @NotBlank(message = "個股代號必填") @RequestParam String symb,
          @Min(value = 0, message = "頁數輸入錯誤") @RequestParam int page,
          @Min(value = 1, message = "最少 1 筆") @RequestParam int size) {
    return new ResponseEntity<>(
            stEntryServ.getTotalEntryByFuzzySymbSlice(symb, page, size), HttpStatus.OK);
  }

  @GetMapping("/s/pg/tot/comNm")
  public ResponseEntity<?> getTotalEntryByFuzzyComNmSlice(
          @NotBlank(message = "公司名必填") @RequestParam String comNm,
          @Min(value = 0, message = "頁數輸入錯誤") @RequestParam int page,
          @Min(value = 1, message = "最少 1 筆") @RequestParam int size) {
    return new ResponseEntity<>(
            stEntryServ.getTotalEntryByFuzzyComNmSlice(comNm, page, size), HttpStatus.OK);
  }

  @GetMapping("/s/pg/one")
  public ResponseEntity<?> getOneEntryPage(
          @NotBlank(message = "個股代號必填") @RequestParam String symb,
          @Min(value = 0, message = "頁數輸入錯誤") @RequestParam int page,
          @Min(value = 1, message = "最少 1 筆") @RequestParam int size) {
    return new ResponseEntity<>(
            stEntryServ.getOneEntryPage(symb, page, size), HttpStatus.OK);
  }

  @GetMapping("/{symb}/file")
  public ResponseEntity<?> getFileFdList(
          @PathVariable String symb,
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime c8tDtm) {
    try {
      return new ResponseEntity<>(
              stEntryServ.getEntryFileList(symb, c8tDtm), HttpStatus.OK);
    } catch (ResourceNotFoundException ex) {
      return ResponseEntity
              .badRequest()
              .body(new MessageResponse(ex.getMessage()));
    }
  }

  @PutMapping("/")
  public ResponseEntity<?> updateEntryDeleteTime(@RequestBody StEntry entry) {
    try {
      return new ResponseEntity<>(
              stEntryServ.updateDeleteTimeByUserIdAndSymbAndC8tDtm(entry.getSymb(), entry.getC8tDtm()),
              HttpStatus.OK);
    } catch (ResourceNotFoundException ex) {
      return ResponseEntity
              .badRequest()
              .body(new MessageResponse(ex.getMessage()));
    }
  }

  @GetMapping("/filefd/{uid}")
  public ResponseEntity<?> downloadFileFd(@PathVariable String uid) throws MalformedURLException {
    StFileFd fileFd = stFileFdServ.findById(Long.parseLong(uid)).orElseThrow();
    UserDetailsImpl userDtl = jwtUtils.getAuthentication();
    Path file = Key.RESOURCES_FILE_FOLDER
            .resolve(userDtl.getId() + "_" + userDtl.getUsername())
            .resolve(fileFd.getSymb())
            .resolve(fileFd.getC8tDtm().format(Key.yyMMdd_fmt))
            .resolve(fileFd.getC8tDtm().format(Key.HHmmss_fmt))
            .resolve(fileFd.getFdFileNm());
    Resource resource = new UrlResource(file.toUri());

    // https://blog.csdn.net/qq_42231437/article/details/107815358
    // 設置了 header 之後，直接用瀏覽器測試，不要用 postman 測試

    String headerValue = "attachment; filename=" +
            java.net.URLEncoder.encode(Objects.requireNonNull(resource.getFilename()), StandardCharsets.UTF_8);
    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
            .body(resource);
  }

  @GetMapping("/filefd64/{uid}")
  public ResponseEntity<?> getFileFd64(@PathVariable String uid) {
    StFileFd fileFd = stFileFdServ.findById(Long.parseLong(uid)).orElseThrow();
    String base64ImgStr = "";
    if (IMAGE_TYPE.equals(fileFd.getFdFileTy().substring(0, 5))) {
      base64ImgStr = c8tBase64Str(fileFd);
    } else if (PDF_TYPE.equals(fileFd.getFdFileTy())) {
      base64ImgStr = c8tBase64Str(fileFd);
    }
    return ResponseEntity.ok().body(base64ImgStr);
  }

  private String c8tBase64Str(StFileFd fileFd) {
    UserDetailsImpl userDtl = jwtUtils.getAuthentication();
    Path file = Key.RESOURCES_FILE_FOLDER
            .resolve(userDtl.getId() + "_" + userDtl.getUsername())
            .resolve(fileFd.getSymb())
            .resolve(fileFd.getC8tDtm().format(Key.yyMMdd_fmt))
            .resolve(fileFd.getC8tDtm().format(Key.HHmmss_fmt))
            .resolve(fileFd.getFdFileNm());
    try {
      Resource resource = new UrlResource(file.toUri());
      return "data:" + fileFd.getFdFileTy() + ";base64," +
              Base64Utils.encodeToString(resource.getInputStream().readAllBytes());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
