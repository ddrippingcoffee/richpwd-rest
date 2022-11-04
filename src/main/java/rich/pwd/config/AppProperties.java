package rich.pwd.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppProperties {

  public static String JwtSecret;
  public static int JwtExpirationSecond;
  public static Long RefreshTokenDurationSecond;

  public static String MaxUploadSizePerFile;
  public static String MaxUploadSizePerRequest;

  @Autowired
  public AppProperties(@Value("${richpwd.app.jwtSecret}") String jwtSecret,
                       @Value("${richpwd.app.jwtExpirationSec}") int jwtExpirationSec,
                       @Value("${richpwd.app.jwtRefreshExpirationSec}") Long refreshTokenDurationSec,
                       @Value("${spring.servlet.multipart.max-file-size}") String maxSizePerFile,
                       @Value("${spring.servlet.multipart.max-request-size}") String maxSizePerRequest) {
    JwtSecret = jwtSecret;
    JwtExpirationSecond = jwtExpirationSec;
    RefreshTokenDurationSecond = refreshTokenDurationSec;

    MaxUploadSizePerFile = maxSizePerFile;
    MaxUploadSizePerRequest = maxSizePerRequest;
  }
}
