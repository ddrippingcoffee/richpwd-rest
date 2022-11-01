package rich.pwd.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class YmlProperties {

  public static String MaxUploadSizePerFile;
  public static String MaxUploadSizePerRequest;

  @Autowired
  public YmlProperties(@Value("${spring.servlet.multipart.max-file-size}") String maxSizePerFile,
                       @Value("${spring.servlet.multipart.max-request-size}") String maxSizePerRequest) {
    MaxUploadSizePerFile = maxSizePerFile;
    MaxUploadSizePerRequest = maxSizePerRequest;
  }
}
