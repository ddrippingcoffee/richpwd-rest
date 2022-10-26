package rich.pwd.bean.po;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@Entity
@Table(name = "files")
@NoArgsConstructor
public class FileDb {

  @Id
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  @GeneratedValue(generator = "uuid")
  private String id;

  private String name;

  private String type;

  @Lob
  private byte[] data;

  public FileDb(String name, String type, byte[] data) {
    this.name = name;
    this.type = type;
    this.data = data;
  }
}
