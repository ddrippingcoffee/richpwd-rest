package rich.pwd.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import rich.pwd.bean.dto.FileInfo;
import rich.pwd.serv.intf.FileStorageServ;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("file")
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasRole('ADMIN')")
public class FileStorageContr {

  private final FileStorageServ fileStorageServ;

  @Autowired
  public FileStorageContr(FileStorageServ fileStorageServ) {
    this.fileStorageServ = fileStorageServ;
  }

  @PostMapping("/upload")
  public ResponseEntity<?> uploadFiles(@RequestParam("files") MultipartFile[] files) {
    try {
      return new ResponseEntity<>(fileStorageServ.saveAll(files), HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
    }
  }

  @GetMapping("/names")
  public ResponseEntity<List<FileInfo>> getListFiles() {
    List<FileInfo> fileInfos = fileStorageServ.loadAll().map(path -> {
      String filename = path.getFileName().toString();
      String url = MvcUriComponentsBuilder
              .fromMethodName(FileStorageContr.class, "getFile", path.getFileName().toString())
              .build().toString();
      return new FileInfo(filename, url);
    }).collect(Collectors.toList());
    return ResponseEntity.status(HttpStatus.OK).body(fileInfos);
  }

  @GetMapping(path = "/{filename:.+}")
  public ResponseEntity<?> getFile(@PathVariable String filename) {
    Resource file = fileStorageServ.load(filename);

    // https://blog.csdn.net/qq_42231437/article/details/107815358
    // 設置了 header 之後，直接用瀏覽器測試，不要用 postman 測試

    String headerValue = "attachment; filename=" +
            java.net.URLEncoder.encode(Objects.requireNonNull(file.getFilename()), StandardCharsets.UTF_8);
    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, headerValue).body(file);
  }
}
