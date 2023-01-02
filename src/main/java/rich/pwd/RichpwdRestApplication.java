package rich.pwd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import rich.pwd.config.jwt.repo.RefreshTokenDao;
import rich.pwd.util.Key;

import java.io.IOException;
import java.nio.file.Files;

@SpringBootApplication
public class RichpwdRestApplication implements CommandLineRunner {

  private final RefreshTokenDao refreshTokenDao;

  @Autowired
  public RichpwdRestApplication(RefreshTokenDao refreshTokenDao) {
    this.refreshTokenDao = refreshTokenDao;
  }

  public static void main(String[] args) {
    SpringApplication.run(RichpwdRestApplication.class, args);
  }

  @Override
  public void run(String... args) {
    // 建立檔案資料夾
    try {
      Files.createDirectories(Key.RESOURCES_FILE_FOLDER);
    } catch (IOException e) {
      throw new RuntimeException("Can't create uploads directory!");
    }

    // 啟動 Server 後刪除留存 Refresh Token
    refreshTokenDao.deleteAll();
  }
}
