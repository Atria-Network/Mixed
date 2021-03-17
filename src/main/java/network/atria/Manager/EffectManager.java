package network.atria.Manager;

import static net.kyori.adventure.text.Component.text;

import com.google.common.collect.Sets;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import network.atria.Effects.Particles.Effect;
import network.atria.MySQL;
import network.atria.Util.KillEffectsConfig;
import org.bukkit.configuration.file.FileConfiguration;

public class EffectManager {

  private static Set<Effect> effects;

  public EffectManager() {
    effects = Sets.newHashSet();
    setEffectsList();
  }

  private void setEffectsList() {
    FileConfiguration config = KillEffectsConfig.getCustomConfig();
    config
        .getConfigurationSection("KILL_EFFECT")
        .getKeys(false)
        .forEach(
            effect ->
                effects.add(
                    new Effect(
                        effect,
                        text(effect, NamedTextColor.GREEN, TextDecoration.BOLD),
                        config.getInt("KILL_EFFECT." + effect + ".points"),
                        config.getBoolean("KILL_EFFECT." + effect + ".donor"))));
    config
        .getConfigurationSection("PROJECTILE_TRAILS")
        .getKeys(false)
        .forEach(
            projectile ->
                effects.add(
                    new Effect(
                        projectile,
                        text(projectile, NamedTextColor.GREEN, TextDecoration.BOLD),
                        config.getInt("PROJECTILE_TRAILS." + projectile + ".points"),
                        config.getBoolean("PROJECTILE_TRAILS." + projectile + ".donor"))));
    config
        .getConfigurationSection("KILL_SOUND")
        .getKeys(false)
        .forEach(
            projectile ->
                effects.add(
                    new Effect(
                        projectile,
                        text(projectile, NamedTextColor.GREEN, TextDecoration.BOLD),
                        config.getInt("KILL_SOUND." + projectile + ".points"),
                        config.getBoolean("KILL_SOUND." + projectile + ".donor"))));
  }

  public Optional<Effect> findEffect(String name) {
    return effects.stream().filter(x -> x.getName().equalsIgnoreCase(name)).findFirst();
  }

  public boolean hasRequirePoint(Effect effect, UserProfile profile) {
    return profile.getPoints() >= effect.getPoint();
  }

  public boolean isNone(Effect effect) {
    return effect.getName().equals("NONE");
  }

  public Set<Effect> getEffects() {
    return effects;
  }
}
