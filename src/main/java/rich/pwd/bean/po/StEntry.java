package rich.pwd.bean.po;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ST_ENTRY")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StEntry implements Serializable {
  /**
   * 流水號
   */
  @Id
  @SequenceGenerator(name = "entrySeq", sequenceName = "seq_entry", allocationSize = 1, initialValue = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "entrySeq")
  @Column(name = "UID")
  private Long uid;
  /**
   * Stock Symbol 股市代號
   */
  @NotNull
  @Column(name = "SYMB")
  private String symb;
  /**
   * 建立時間
   */
  @NotNull
  @Column(name = "C8T_DTM")
  private LocalDateTime c8tDtm;
  /**
   * 刪除時間
   */
  @Column(name = "DEL_DTM")
  private LocalDateTime delDtm;
  /**
   * 個股註記
   */
  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, mappedBy = "stEntry")
  @JsonManagedReference
  private List<StDtl> stDtlList;
}
