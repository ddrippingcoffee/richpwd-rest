package rich.pwd.serv;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import rich.pwd.serv.intf.FileStorageServ;
import rich.pwd.util.Key;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FileStorageServImpl implements FileStorageServ {

  @Override
  public void init() {
    if (!Files.exists(Key.RESOURCES_FILE_FOLDER)) {
      try {
        Files.createDirectory(Key.RESOURCES_FILE_FOLDER);
      } catch (IOException e) {
        throw new RuntimeException("新增資料夾失敗");
      }
    }
  }

  @Override
  public String save(MultipartFile file) {
    try {
      Files.copy(file.getInputStream(),
              Key.RESOURCES_FILE_FOLDER.resolve(Objects.requireNonNull(file.getOriginalFilename())));
      return file.getOriginalFilename();
    } catch (Exception e) {
      throw new RuntimeException("檔案儲存失敗: " + e.getMessage());
    }
  }

  @Override
  public List<String> saveAll(MultipartFile[] files) {
    return Arrays.stream(files).map(this::save).collect(Collectors.toList());
  }

  @Override
  public Resource load(String filename) {
    try {
      Path file = Key.RESOURCES_FILE_FOLDER.resolve(filename);
      Resource resource = new UrlResource(file.toUri());

      if (resource.exists() || resource.isReadable()) {
        return resource;
      } else {
        throw new RuntimeException("檔案讀取失敗");
      }
    } catch (MalformedURLException e) {
      throw new RuntimeException("Error: " + e.getMessage());
    }
  }

  @Override
  public void deleteAll() {
    FileSystemUtils.deleteRecursively(Key.RESOURCES_FILE_FOLDER.toFile());
  }

  @Override
  public Stream<Path> loadAll() {
    try {
      return Files.walk(Key.RESOURCES_FILE_FOLDER, 1)
              .filter(path -> !path.equals(Key.RESOURCES_FILE_FOLDER))
              .map(Key.RESOURCES_FILE_FOLDER::relativize);
    } catch (IOException e) {
      throw new RuntimeException("檔案讀取失敗");
    }
  }
}
