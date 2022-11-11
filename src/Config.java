import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class Config {
  private File default_file;
  private String name;
  private Path config_path;

  public Config(String config_file) {
    this.config_path = Paths.get(config_file);
    if (config_path.toFile().exists()) {
      HashMap<String, String> map = new HashMap<String, String>();
      try {
        List<String> lines = Files.readAllLines(this.config_path, StandardCharsets.UTF_8);
        lines.forEach(line -> map.put(line.split(":")[0], line.split(":")[1]));
        default_file = new File(map.get("default_file"));
        this.name = map.get("name");
      } catch (IOException e) {
        System.out.println("Could not open config file: "+this.config_path);
      }
    } else {
      this.name = "UnknownUser";
      this.default_file = null;
    }
  }

  public String name() {
    return name;
  }

  public File default_file() {
    return default_file;
  }

  public void set_default_file(File new_default) {
    this.default_file = new_default; 
    write();
  }

  public void set_name(String new_name) {
    this.name = new_name; 
    write();
  }

  public void write() {
    File config_file = this.config_path.toFile();
    if (!config_file.exists()) {
      File parent = config_file.getParentFile();
      if (parent != null) {
        parent.mkdirs();
      }
    }
    List<String> lines = new ArrayList<String>(); 
    lines.add("default_file:"+default_file);
    lines.add("name:"+name);
    try {
      Files.write(this.config_path, lines, StandardCharsets.UTF_8);
    } catch (IOException e) {
      System.out.println("Could not open config file: "+this.config_path);
    }
  }

  public static void main(String[] args) {
    Config c = new Config("test.txt");
    System.out.println("Name: "+c.name());
    System.out.println("Default file: "+c.default_file());
    c.set_name("Dylan");
    c.set_default_file(new File("~/redtongue"));
  }
}
