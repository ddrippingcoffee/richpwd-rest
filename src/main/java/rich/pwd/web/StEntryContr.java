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
import rich.pwd.bean.po.StFileDb;
import rich.pwd.bean.po.StFileFd;
import rich.pwd.config.jwt.bean.payload.response.MessageResponse;
import rich.pwd.ex.ResourceNotFoundException;
import rich.pwd.serv.intf.StEntryServ;
import rich.pwd.serv.intf.StFileDbServ;
import rich.pwd.serv.intf.StFileFdServ;
import rich.pwd.util.Key;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
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

  private final StEntryServ stEntryServ;
  private final StFileDbServ stFileDbServ;
  private final StFileFdServ stFileFdServ;

  @Autowired
  public StEntryContr(StEntryServ stEntryServ,
                      StFileDbServ stFileDbServ,
                      StFileFdServ stFileFdServ) {
    this.stEntryServ = stEntryServ;
    this.stFileDbServ = stFileDbServ;
    this.stFileFdServ = stFileFdServ;
  }

  @PostMapping("/stores")
  public ResponseEntity<?> storeAll(@Valid @RequestPart(value = "entryJsonStr", required = true) StEntry entry,
                                    @RequestParam(value = "fileDbs", required = false) MultipartFile[] fileDbs,
                                    @RequestParam(value = "fileFds", required = false) MultipartFile[] fileFds) {
    return new ResponseEntity<>(stEntryServ.c8tStEntry(entry, fileDbs, fileFds), HttpStatus.CREATED);
  }

  @GetMapping("/s/pg/act")
  public ResponseEntity<?> findAllActiveEntryByPage(
          @Min(value = 0, message = "頁數輸入錯誤") @RequestParam int page,
          @Min(value = 1, message = "最少 1 筆") @RequestParam int size,
          @Pattern(regexp = "(^asc$|^desc$)", message = "排序輸入錯誤") @RequestParam String desc) {
    return new ResponseEntity<>(
            stEntryServ.findAllActiveEntryPage(page, size, desc), HttpStatus.OK);
  }

  /**
   * 無使用
   */
  @GetMapping("/s/pg/old")
  public ResponseEntity<?> findAllOldEntryPage(
          @Min(value = 0, message = "頁數輸入錯誤") @RequestParam int page,
          @Min(value = 1, message = "最少 1 筆") @RequestParam int size,
          @Pattern(regexp = "(^asc$|^desc$)", message = "排序輸入錯誤") @RequestParam String desc) {
    return new ResponseEntity<>(
            stEntryServ.findAllOldEntryPage(page, size, desc), HttpStatus.OK);
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

  @GetMapping("/s/sl/symb")
  public ResponseEntity<?> findAllBySymbListSlice(
          @NotBlank(message = "個股代號必填") @RequestParam String symb,
          @Min(value = 0, message = "頁數輸入錯誤") @RequestParam int page,
          @Min(value = 1, message = "最少 1 筆") @RequestParam int size,
          @Pattern(regexp = "(^asc$|^desc$)", message = "排序輸入錯誤") @RequestParam String desc) {
    return new ResponseEntity<>(
            stEntryServ.findAllBySymbSlice(symb, page, size, desc), HttpStatus.OK);
  }

  @GetMapping("/s/sl/comNm")
  public ResponseEntity<?> findAllByComNmSlice(
          @NotBlank(message = "公司名必填") @RequestParam String comNm,
          @Min(value = 0, message = "頁數輸入錯誤") @RequestParam int page,
          @Min(value = 1, message = "最少 1 筆") @RequestParam int size,
          @Pattern(regexp = "(^asc$|^desc$)", message = "排序輸入錯誤") @RequestParam String desc) {
    return new ResponseEntity<>(
            stEntryServ.findAllByComNmSlice(comNm, page, size, desc), HttpStatus.OK);
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

  /*
    目前無使用
  @GetMapping("/filedb/{uid}")
  public ResponseEntity<?> downloadFileDb(@PathVariable String uid) {
    StFileDb fileDb = stFileDbServ.findById(Long.parseLong(uid)).orElseThrow();

    // https://blog.csdn.net/qq_42231437/article/details/107815358
    // 設置了 header 之後，直接用瀏覽器測試，不要用 postman 測試

    String headerValue = "attachment; filename=" +
            java.net.URLEncoder.encode(Objects.requireNonNull(fileDb.getDbFileNm()), StandardCharsets.UTF_8);
    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
            .body(fileDb.getDbFileData());
  }
   */

  @GetMapping("/filefd/{uid}")
  public ResponseEntity<?> downloadFileFd(@PathVariable String uid) throws MalformedURLException {
    StFileFd fileFd = stFileFdServ.findById(Long.parseLong(uid)).orElseThrow();
    Path file = Key.RESOURCES_FILE_FOLDER.resolve(fileFd.getFdFileNm());
    Resource resource = new UrlResource(file.toUri());

    // https://blog.csdn.net/qq_42231437/article/details/107815358
    // 設置了 header 之後，直接用瀏覽器測試，不要用 postman 測試

    String headerValue = "attachment; filename=" +
            java.net.URLEncoder.encode(Objects.requireNonNull(resource.getFilename()), StandardCharsets.UTF_8);
    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
            .body(resource);
  }

  @GetMapping("/filedb64/{uid}")
  public ResponseEntity<?> getFileDbImg64(@PathVariable String uid) {
    StFileDb fileDb = stFileDbServ.findById(Long.parseLong(uid)).orElseThrow();
    String base64ImgStr = null;
    if (IMAGE_TYPE.equals(fileDb.getDbFileTy().substring(0, 5))) {
      base64ImgStr = "data:" + fileDb.getDbFileTy() + ";base64," +
              Base64Utils.encodeToString(fileDb.getDbFileData());
    }
    return ResponseEntity.ok().body(base64ImgStr);
  }

  @GetMapping("/filefd64/{uid}")
  public ResponseEntity<?> getFileFdImg64(@PathVariable String uid) {
    StFileFd fileFd = stFileFdServ.findById(Long.parseLong(uid)).orElseThrow();
    Path file = Key.RESOURCES_FILE_FOLDER.resolve(fileFd.getFdFileNm());
    String base64ImgStr = null;
    try {
      if (IMAGE_TYPE.equals(fileFd.getFdFileTy().substring(0, 5))) {
        Resource resource = new UrlResource(file.toUri());
        base64ImgStr = "data:" + fileFd.getFdFileTy() + ";base64," +
                Base64Utils.encodeToString(resource.getInputStream().readAllBytes());
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return ResponseEntity.ok().body(base64ImgStr);
  }
}
