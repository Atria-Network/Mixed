package network.atria.Manager;

import com.google.common.collect.Sets;
import java.util.Set;
import network.atria.Effects.Particles.Effect;
import network.atria.UserProfile.UserProfile;
import network.atria.Util.KillEffectsConfig;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EffectManager {

  private static Set<Effect> kill_effects;
  private static Set<Effect> projectiles;
  private static Set<Effect> sounds;

  public EffectManager() {
    kill_effects = Sets.newHashSet();
    projectiles = Sets.newHashSet();
    sounds = Sets.newHashSet();
    setEffectsList();
  }

  private void setEffectsList() {
    FileConfiguration config = KillEffectsConfig.getCustomConfig();
    config
        .getConfigurationSection("KILL_EFFECT")
        .getKeys(false)
        .forEach(
            effect -> {
              String ROOT = "KILL_EFFECT." + effect + ".";
              ItemStack item =
                  new ItemStack(
                      Material.valueOf(config.getString(ROOT + "material").toUpperCase()));
              ItemMeta meta = item.getItemMeta();
              meta.setDisplayName(config.getString(ROOT + "name"));
              item.setItemMeta(meta);
              kill_effects.add(
                  Effect.of(
                      config.getString(ROOT + "name"),
                      item,
                      config.getInt(ROOT + "slot"),
                      config.getInt(ROOT + "points"),
                      config.getBoolean(ROOT + "donor"),
                      config.getBoolean(ROOT + "donor-only")));
            });
    config
        .getConfigurationSection("PROJECTILE_TRAILS")
        .getKeys(false)
        .forEach(
            projectile -> {
              String ROOT = "PROJECTILE_TRAILS." + projectile + ".";
              ItemStack item =
                  new ItemStack(
                      Material.valueOf(config.getString(ROOT + "material").toUpperCase()));
              ItemMeta meta = item.getItemMeta();
              meta.setDisplayName(config.getString(ROOT + "name"));
              item.setItemMeta(meta);
              projectiles.add(
                  Effect.of(
                      config.getString(ROOT + "name"),
                      item,
                      config.getInt(ROOT + "slot"),
                      config.getInt(ROOT + "points"),
                      config.getBoolean(ROOT + "donor"),
                      config.getBoolean(ROOT + "donor-only")));
            });
    config
        .getConfigurationSection("KILL_SOUND")
        .getKeys(false)
        .forEach(
            sound -> {
              String ROOT = "KILL_SOUND." + sound + ".";
              ItemStack item =
                  new ItemStack(
                      Material.valueOf(config.getString(ROOT + "material").toUpperCase()));
              ItemMeta meta = item.getItemMeta();
              meta.setDisplayName(config.getString(ROOT + "name"));
              item.setItemMeta(meta);
              sounds.add(
                  Effect.of(
                      config.getString(ROOT + "name"),
                      item,
                      config.getInt(ROOT + "slot"),
                      config.getInt(ROOT + "points"),
                      config.getBoolean(ROOT + "donor"),
                      config.getBoolean(ROOT + "donor-only")));
            });
  }

  public boolean hasRequirePoint(Effect effect, UserProfile profile) {
    return profile.getPoints() >= effect.getPoint();
  }

  public boolean isNone(Effect effect) {
    return effect.getName().equals("NONE");
  }

  public static Set<Effect> getKillEffects() {
    return kill_effects;
  }

  public static Set<Effect> getProjectiles() {
    return projectiles;
  }

  public static Set<Effect> getSounds() {
    return sounds;
  }
}
