package rich.pwd.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import rich.pwd.bean.dto.FileDbInfoDto;
import rich.pwd.bean.po.FileDb;
import rich.pwd.serv.intf.FileDbServ;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("filedb")
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasRole('ADMIN')")
public class FileDbContr {

  private final FileDbServ fileDbServ;

  @Autowired
  public FileDbContr(FileDbServ fileDbServ) {
    this.fileDbServ = fileDbServ;
  }

  @PostMapping("/upload")
  public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
    try {
      fileDbServ.store(file);
      return new ResponseEntity<>(file.getOriginalFilename(), HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
    }
  }

  @GetMapping("/names")
  public ResponseEntity<List<FileDbInfoDto>> getListFiles() {
    List<FileDbInfoDto> files = fileDbServ.getAllFiles().map(dbFile -> {
      String fileDownloadUri = ServletUriComponentsBuilder
              .fromCurrentContextPath()
              .path("/filedb/").path(dbFile.getId()).toUriString();
      return new FileDbInfoDto(dbFile.getName(), fileDownloadUri, dbFile.getType(), dbFile.getData().length);
    }).collect(Collectors.toList());
    return ResponseEntity.status(HttpStatus.OK).body(files);
  }

  @GetMapping("/{id}")
  public ResponseEntity<byte[]> getFile(@PathVariable String id) {
    FileDb fileDb = fileDbServ.getFile(id);

    String headerValue = "attachment; filename=" +
            java.net.URLEncoder.encode(Objects.requireNonNull(fileDb.getName()), StandardCharsets.UTF_8);
    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
            .body(fileDb.getData());
  }
}
