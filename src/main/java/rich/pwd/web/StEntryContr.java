package rich.pwd.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rich.pwd.bean.dto.payload.response.MessageResponse;
import rich.pwd.bean.po.StEntry;
import rich.pwd.ex.ResourceNotFoundException;
import rich.pwd.serv.intf.StEntryServ;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("entry")
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasRole('ADMIN')")
public class StEntryContr {

  private final StEntryServ stEntryServ;

  @Autowired
  public StEntryContr(StEntryServ stEntryServ) {
    this.stEntryServ = stEntryServ;
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
    List<StEntry> stEntryList = stEntryServ.getAllActiveEntry();
    return new ResponseEntity<>(stEntryList, HttpStatus.OK);
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
}
