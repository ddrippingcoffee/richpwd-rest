package rich.pwd.bean.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "st_file_fd")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StFileFd {

  @Id
  @SequenceGenerator(name = "fileFdSeq", sequenceName = "seq_file_fd", allocationSize = 1, initialValue = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fileFdSeq")
  @Column(name = "UID")
  private Long uid;

  @NotNull
  @Column(name = "SYMB")
  private String symb;

  @NotNull
  @Column(name = "C8T_DTM")
  private LocalDateTime c8tDtm;

  @NotNull
  @Column(name = "FD_FILE_NM")
  private String fdFileNm;

  @NotNull
  @Column(name = "FD_FILE_TY")
  private String fdFileTy;
}
