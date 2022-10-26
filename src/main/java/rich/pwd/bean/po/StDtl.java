package rich.pwd.bean.po;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "ST_DTL")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "stEntry")
public class StDtl implements Serializable {
  /**
   * 流水號
   */
  @Id
  @SequenceGenerator(name = "dtlSeq", sequenceName = "seq_dtl", allocationSize = 1, initialValue = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dtlSeq")
  @Column(name = "UID")
  private Long uid;
  /**
   * 註記類型
   */
  @NotNull
  @Column(name = "DTL_TY")
  private String dtlTy;
  /**
   * 註記簡述
   */
  @Column(name = "DTL_BRF", columnDefinition = "TEXT")
  private String dtlBrf;
  /**
   * 註記資料
   */
  @Column(name = "DTL_INFO", columnDefinition = "TEXT")
  private String dtlInfo;
  /**
   * 註記其他說明
   */
  @Column(name = "DTL_DES", columnDefinition = "TEXT")
  private String dtlDes;

  @ManyToOne(cascade = {}, targetEntity = StEntry.class, optional = false)
  @JoinColumns({
          @JoinColumn(name = "SYMB", referencedColumnName = "SYMB"),
          @JoinColumn(name = "C8T_DTM", referencedColumnName = "C8T_DTM")
  })
  @JsonBackReference
  private StEntry stEntry;
}
