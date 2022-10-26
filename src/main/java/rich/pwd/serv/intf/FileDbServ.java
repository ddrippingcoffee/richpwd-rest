package rich.pwd.serv.intf;

import org.springframework.web.multipart.MultipartFile;
import rich.pwd.bean.po.FileDb;

import java.io.IOException;
import java.util.stream.Stream;

public interface FileDbServ extends BaseServ<FileDb, String> {

  FileDb store(MultipartFile file) throws IOException;

  FileDb getFile(String id);

  Stream<FileDb> getAllFiles();
}
