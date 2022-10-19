package rich.pwd.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rich.pwd.bean.po.ComInfo;
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
}
