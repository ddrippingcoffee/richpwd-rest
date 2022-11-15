package rich.pwd.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import rich.pwd.bean.po.ComInfo;
import rich.pwd.config.jwt.bean.payload.response.MessageResponse;
import rich.pwd.ex.ResourceNotFoundException;
import rich.pwd.serv.intf.ComInfoServ;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@RestController
@RequestMapping("cominfo")
@CrossOrigin(origins = "*", maxAge = 3600)
@Validated
public class ComInfoContr {

  private final ComInfoServ comInfoServ;

  @Autowired
  public ComInfoContr(ComInfoServ comInfoServ) {
    this.comInfoServ = comInfoServ;
  }

  @PostMapping("/")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> save(@Valid @RequestBody ComInfo comInfo) {
    try {
      // 自增主鍵會自動填入
      comInfoServ.store(comInfo);
    } catch (RuntimeException ex) {
      return ResponseEntity
              .badRequest()
              .body(new MessageResponse(ex.getMessage()));
    }
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

  @GetMapping("/r/induslist")
  public ResponseEntity<?> getComIndusList() {
    return new ResponseEntity<>(comInfoServ.getComIndusList(), HttpStatus.OK);
  }

  @GetMapping("/s/pg/symb")
  public ResponseEntity<?> findAllBySymbPage(
          @NotBlank(message = "個股代號必填") @RequestParam String symb,
          @Min(value = 0, message = "頁數輸入錯誤") @RequestParam int page,
          @Min(value = 1, message = "最少 1 筆") @RequestParam int size,
          @Pattern(regexp = "(^asc$|^desc$)", message = "排序輸入錯誤") @RequestParam String desc) {
    return new ResponseEntity<>(
            comInfoServ.findAllBySymbPage(symb, page, size, desc), HttpStatus.OK);
  }

  @GetMapping("/s/sl/nm")
  public ResponseEntity<?> findAllByComNmSlice(
          @NotBlank(message = "公司名必填") @RequestParam String comNm,
          @Min(value = 0, message = "頁數輸入錯誤") @RequestParam int page,
          @Min(value = 1, message = "最少 1 筆") @RequestParam int size,
          @Pattern(regexp = "(^asc$|^desc$)", message = "排序輸入錯誤") @RequestParam String desc) {
    return new ResponseEntity<>(comInfoServ.findAllByComNmSlice(comNm, page, size, desc), HttpStatus.OK);
  }

  @GetMapping("/s/pg/main")
  public ResponseEntity<?> findAllByComMainPage(
          @NotBlank(message = "主要業務必填") @RequestParam String comMain,
          @Min(value = 0, message = "頁數輸入錯誤") @RequestParam int page,
          @Min(value = 1, message = "最少 1 筆") @RequestParam int size,
          @Pattern(regexp = "(^asc$|^desc$)", message = "排序輸入錯誤") @RequestParam String desc) {
    return new ResponseEntity<>(
            comInfoServ.findAllByComMainPage(comMain, page, size, desc), HttpStatus.OK);
  }

  @GetMapping("/s/pg/coted")
  public ResponseEntity<?> findAllByComCotedPage(
          @NotBlank(message = "相關產業必填") @RequestParam String comCoted,
          @Min(value = 0, message = "頁數輸入錯誤") @RequestParam int page,
          @Min(value = 1, message = "最少 1 筆") @RequestParam int size,
          @Pattern(regexp = "(^asc$|^desc$)", message = "排序輸入錯誤") @RequestParam String desc) {
    return new ResponseEntity<>(
            comInfoServ.findAllByComCotedPage(comCoted, page, size, desc), HttpStatus.OK);
  }

  @GetMapping("/s/pg/cep")
  public ResponseEntity<?> findAllByComCepPage(
          @NotBlank(message = "相關概念必填") @RequestParam String comCep,
          @Min(value = 0, message = "頁數輸入錯誤") @RequestParam int page,
          @Min(value = 1, message = "最少 1 筆") @RequestParam int size,
          @Pattern(regexp = "(^asc$|^desc$)", message = "排序輸入錯誤") @RequestParam String desc) {
    return new ResponseEntity<>(
            comInfoServ.findAllByComCepPage(comCep, page, size, desc), HttpStatus.OK);
  }

  @GetMapping("/s/pg/indus")
  public ResponseEntity<?> findAllByComIndusPage(
          @NotBlank(message = "產業別必填") @RequestParam String indus,
          @Min(value = 0, message = "頁數輸入錯誤") @RequestParam int page,
          @Min(value = 1, message = "最少 1 筆") @RequestParam int size,
          @Pattern(regexp = "(^asc$|^desc$)", message = "排序輸入錯誤") @RequestParam String desc) {
    return new ResponseEntity<>(
            comInfoServ.findAllByComIndusPage(indus, page, size, desc), HttpStatus.OK);
  }

  @PutMapping("/{symb}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> updBySymb(@PathVariable String symb,
                                     @Valid @RequestBody ComInfo comInfo) {
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
