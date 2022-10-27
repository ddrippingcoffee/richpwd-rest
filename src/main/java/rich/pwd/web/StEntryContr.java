package rich.pwd.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import rich.pwd.bean.dto.payload.response.MessageResponse;
import rich.pwd.bean.po.StEntry;
import rich.pwd.bean.po.StFileDb;
import rich.pwd.bean.po.StFileFd;
import rich.pwd.ex.ResourceNotFoundException;
import rich.pwd.serv.intf.StEntryServ;
import rich.pwd.serv.intf.StFileDbServ;
import rich.pwd.serv.intf.StFileFdServ;
import rich.pwd.util.Key;

import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("entry")
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasRole('ADMIN')")
public class StEntryContr {

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
  public ResponseEntity<?> storeAll(@RequestParam(value = "entryStr", required = true) String entryStr,
                                    @RequestParam(value = "fileDbs", required = false) MultipartFile[] fileDbs,
                                    @RequestParam(value = "fileFds", required = false) MultipartFile[] fileFds) {
    return new ResponseEntity<>(stEntryServ.c8tStEntry(entryStr, fileDbs, fileFds), HttpStatus.CREATED);
  }

  @GetMapping("/act")
  public ResponseEntity<?> getAllActiveEntry() {
    return new ResponseEntity<>(stEntryServ.getAllActiveEntry(), HttpStatus.OK);
  }

  @GetMapping("/old")
  public ResponseEntity<?> getAllOldEntry() {
    List<StEntry> stEntryList = stEntryServ.getAllOldEntry();
    return new ResponseEntity<>(stEntryList, HttpStatus.OK);
  }

  @PutMapping("/")
  public ResponseEntity<?> updateEntryDeleteTime(@RequestBody StEntry entry) {
    try {
      return new ResponseEntity<>(
              stEntryServ.updateDeleteTimeBySymbAndC8tDtm(entry.getSymb(), entry.getC8tDtm()),
              HttpStatus.OK);
    } catch (ResourceNotFoundException ex) {
      return ResponseEntity
              .badRequest()
              .body(new MessageResponse(ex.getMessage()));
    }
  }

  @GetMapping("/filedb/{uid}")
  public ResponseEntity<?> getFile(@PathVariable String uid) {
    StFileDb fileDb = stFileDbServ.findById(Long.parseLong(uid)).orElseThrow();

    // https://blog.csdn.net/qq_42231437/article/details/107815358
    // 設置了 header 之後，直接用瀏覽器測試，不要用 postman 測試

    String headerValue = "attachment; filename=" +
            java.net.URLEncoder.encode(Objects.requireNonNull(fileDb.getDbFileNm()), StandardCharsets.UTF_8);
    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
            .body(fileDb.getDbFileData());
  }

  @GetMapping("/filefd/{uid}")
  public ResponseEntity<?> getFileFd(@PathVariable String uid) throws MalformedURLException {
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
}
