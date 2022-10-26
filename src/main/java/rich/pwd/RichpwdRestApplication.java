package rich.pwd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import rich.pwd.repo.RefreshTokenDao;

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
  public void run(String... args) throws Exception {
    // 啟動 Server 後刪除留存 Refresh Token
    refreshTokenDao.deleteAll();
  }
}
