package rich.pwd.util;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Key {
  private Key() {
  }

  public static final String MODULE_NAME = "richpwd-rest";
  public static final String PROJECT_PATH = System.getProperty("user.dir");

  public static final Path BUILD_PATH = Paths.get(PROJECT_PATH).resolve(MODULE_NAME);
  public static final Path RESOURCES_FILE_FOLDER = BUILD_PATH.resolve("src")
          .resolve("main").resolve("resources").resolve("uploads");
}
