package rich.pwd.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.time.LocalDateTime;
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
  private final ObjectMapper objectMapper;

  @Autowired
  public StEntryContr(StEntryServ stEntryServ,
                      StFileDbServ stFileDbServ,
                      StFileFdServ stFileFdServ,
                      ObjectMapper objectMapper) {
    this.stEntryServ = stEntryServ;
    this.stFileDbServ = stFileDbServ;
    this.stFileFdServ = stFileFdServ;
    this.objectMapper = objectMapper;
  }

  @PostMapping("/stores")
  public ResponseEntity<?> storeAll(@RequestParam("entryStr") String entryStr,
                                    @RequestParam("fileDbs") MultipartFile[] fileDbs,
                                    @RequestParam("fileFds") MultipartFile[] fileFds)
          throws JsonProcessingException {
    StEntry entry = objectMapper.readValue(entryStr, StEntry.class);
    entry.setC8tDtm(LocalDateTime.now());
    stEntryServ.saveAndFlush(entry);
    try {
      stFileDbServ.storeAll(entry.getSymb(), entry.getC8tDtm(), fileDbs);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
    }
    stFileFdServ.storeAll(entry.getSymb(), entry.getC8tDtm(), fileFds);
    return new ResponseEntity<>(entry.getC8tDtm(), HttpStatus.CREATED);
  }

  @PostMapping("/")
  public ResponseEntity<?> save(@RequestBody StEntry entry) {
    // 自增主鍵會自動填入
    entry.setC8tDtm(LocalDateTime.now());
    stEntryServ.saveAndFlush(entry);
    return new ResponseEntity<>(entry.getC8tDtm(), HttpStatus.CREATED);
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
  public ResponseEntity<byte[]> getFile(@PathVariable String uid) {
    StFileDb fileDb = stFileDbServ.findById(Long.parseLong(uid)).orElseThrow();

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

    String headerValue = "attachment; filename=" +
            java.net.URLEncoder.encode(Objects.requireNonNull(resource.getFilename()), StandardCharsets.UTF_8);
    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
            .body(resource);
  }
}
