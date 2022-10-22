package rich.pwd.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rich.pwd.bean.dto.payload.response.MessageResponse;
import rich.pwd.bean.po.ComInfo;
import rich.pwd.ex.ResourceNotFoundException;
import rich.pwd.serv.intf.ComInfoServ;

@RestController
@RequestMapping("cominfo")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ComInfoContr {

  private final ComInfoServ comInfoServ;

  @Autowired
  public ComInfoContr(ComInfoServ comInfoServ) {
    this.comInfoServ = comInfoServ;
  }

  @PostMapping("/")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> save(@RequestBody ComInfo comInfo) {
    // 自增主鍵會自動填入
    comInfoServ.save(comInfo);
    return new ResponseEntity<>(null, HttpStatus.CREATED);
  }

  @GetMapping("/")
  public ResponseEntity<?> getAll() {
    return new ResponseEntity<>(comInfoServ.findAll(), HttpStatus.OK);
  }

  @GetMapping("/{symb}")
  public ResponseEntity<?> getBySymb(@PathVariable String symb) {
    return new ResponseEntity<>(comInfoServ.findOneBySymb(symb), HttpStatus.OK);
  }

  @GetMapping("/s/nm")
  public ResponseEntity<?> getByComNm(@RequestParam String nm) {
    return new ResponseEntity<>(comInfoServ.findOneByComNm(nm), HttpStatus.OK);
  }

  @GetMapping("/s/indus")
  public ResponseEntity<?> getByComIndus(@RequestParam String indus) {
    return new ResponseEntity<>(comInfoServ.findAllByComIndus(indus), HttpStatus.OK);
  }

  @PutMapping("/{symb}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> updBySymb(@PathVariable String symb, @RequestBody ComInfo comInfo) {
    try {
      comInfoServ.updateBySymb(symb, comInfo);
      return new ResponseEntity<>(HttpStatus.OK);
    } catch (ResourceNotFoundException ex) {
      return ResponseEntity
              .badRequest()
              .body(new MessageResponse(ex.getMessage()));
    }
  }

  @DeleteMapping("/{symb}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> delByComSymb(@PathVariable String symb) {
    try {
      return new ResponseEntity<>(comInfoServ.deleteComInfoBySymb(symb), HttpStatus.OK);
    } catch (ResourceNotFoundException ex) {
      return ResponseEntity
              .badRequest()
              .body(new MessageResponse(ex.getMessage()));
    }

  }
}
