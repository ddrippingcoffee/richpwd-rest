package rich.pwd.bean.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "st_file_db")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StFileDb {

  @Id
  @SequenceGenerator(name = "fileDbSeq", sequenceName = "seq_file_db", allocationSize = 1, initialValue = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fileDbSeq")
  @Column(name = "UID")
  private Long uid;

  @NotNull
  @Column(name = "SYMB")
  private String symb;

  @NotNull
  @Column(name = "C8T_DTM")
  private LocalDateTime c8tDtm;

  @NotNull
  @Column(name = "DB_FILE_NM")
  private String dbFileNm;

  @NotNull
  @Column(name = "DB_FILE_TY")
  private String dbFileTy;

  @Lob
  @NotNull
  @Column(name = "DB_FILE_DATA")
  private byte[] dbFileData;
}
