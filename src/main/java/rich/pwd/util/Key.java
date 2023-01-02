package rich.pwd.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;

public class Key {
  private Key() {
  }

  public static final String MODULE_NAME = "richpwd-rest";
  public static final String PROJECT_PATH = System.getProperty("user.dir");

  public static final Path BUILD_PATH = Paths.get(PROJECT_PATH).resolve(MODULE_NAME);
  public static final Path RESOURCES_FILE_FOLDER = BUILD_PATH.resolve("src")
          .resolve("main").resolve("resources").resolve("uploads");

  public static final String yyMMDD_HHmmss = "yyMMdd@@HHmmss";
  public static final DateTimeFormatter yyMMDD_HHmmss_fmt = DateTimeFormatter.ofPattern(yyMMDD_HHmmss);

  public static final String yyMMdd = "yyMMdd";
  public static final DateTimeFormatter yyMMdd_fmt = DateTimeFormatter.ofPattern(yyMMdd);
  public static final String HHmmss = "HHmmss";
  public static final DateTimeFormatter HHmmss_fmt = DateTimeFormatter.ofPattern(HHmmss);
}
