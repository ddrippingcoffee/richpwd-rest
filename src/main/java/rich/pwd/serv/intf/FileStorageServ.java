package rich.pwd.serv.intf;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public interface FileStorageServ {

  void init();

  String save(MultipartFile file);

  List<String> saveAll(MultipartFile[] files);

  Resource load(String filename);

  void deleteAll();

  Stream<Path> loadAll();
}
