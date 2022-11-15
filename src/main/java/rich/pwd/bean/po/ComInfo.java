package rich.pwd.bean.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Entity
@Table(name = "COM_INFO")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ComInfo {
  /**
   * 流水號
   */
  @Id
  @SequenceGenerator(name = "comInfoSeq", sequenceName = "seq_cominfo", allocationSize = 1, initialValue = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comInfoSeq")
  @Column(name = "UID", unique = true)
  private Long uid;
  /**
   * Stock Symbol 股市代號
   */
  @NotEmpty(message = "股市代號不得為空")
  @Column(name = "SYMB", unique = true)
  private String symb;
  /**
   * 名稱
   */
  @Column(name = "COM_NM")
  private String comNm;
  /**
   * 市場別
   */
  @NotEmpty(message = "市場別必填")
  @Column(name = "COM_TYPE")
  private String comType;
  /**
   * Industry 產業別
   */
  @Pattern(regexp = "^([^\\x00-\\xff]+|N/A)$", message = "產業別輸入錯誤")
  @Column(name = "COM_INDUS")
  private String comIndus;
  /**
   * Main Business 主要業務
   */
  @Column(name = "COM_MAIN")
  private String comMain;
  /**
   * Correlated Industry 相關產業
   */
  @Column(name = "COM_COTED")
  private String comCoted;
  /**
   * Correlated Concept 相關概念
   */
  @Column(name = "COM_CEP")
  private String comCep;
  /**
   * 官方網站
   */
  @Column(name = "COM_OFCL")
  private String comOfcl;
}
