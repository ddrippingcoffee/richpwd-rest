package rich.pwd;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RichpwdRestApplication {

  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(RichpwdRestApplication.class);
    app.setBannerMode(Banner.Mode.OFF);
    app.run(args);
  }

}
