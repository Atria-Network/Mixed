package network.atria.Util;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import network.atria.Mixed;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class KillEffectsConfig {

  private static FileConfiguration config = null;
  private static File configFile;
  private static String file;

  public KillEffectsConfig(String fileName) {
    file = fileName;
    configFile = new File(Mixed.get().getDataFolder(), file);
  }

  public static void reloadConfig() {
    config = YamlConfiguration.loadConfiguration(configFile);

    InputStream defConfigStream = Mixed.get().getResource(file);
    if (defConfigStream == null) {
      return;
    }

    config.setDefaults(
        YamlConfiguration.loadConfiguration(
            new InputStreamReader(defConfigStream, StandardCharsets.UTF_8)));
  }

  public static FileConfiguration getCustomConfig() {
    if (config == null) {
      reloadConfig();
    }
    return config;
  }
}
