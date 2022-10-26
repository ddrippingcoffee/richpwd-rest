package rich.pwd.serv;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import rich.pwd.bean.po.FileDb;
import rich.pwd.repo.FileDbDao;
import rich.pwd.serv.intf.FileDbServ;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class FileDbServImpl extends BaseServImpl<FileDb, String, FileDbDao> implements FileDbServ {

  public FileDbServImpl(FileDbDao repository) {
    super(repository);
  }

  @Override
  public FileDb store(MultipartFile multipartFile) throws IOException {
    String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
    FileDb fileDb = new FileDb(fileName, multipartFile.getContentType(), multipartFile.getBytes());
    return super.getRepository().save(fileDb);
  }

  @Override
  public FileDb getFile(String id) {
    return super.getRepository().findById(id).get();
  }

  @Override
  public Stream<FileDb> getAllFiles() {
    return super.getRepository().findAll().stream();
  }
}
