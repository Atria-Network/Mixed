package network.atria.Util;

import static net.kyori.adventure.text.Component.translatable;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.checkerframework.checker.nullness.qual.NonNull;

public class TextFormat {

  public static @NonNull TranslatableComponent message(String key) {
    return translatable().key(key).color(NamedTextColor.DARK_AQUA).build();
  }

  public static @NonNull TranslatableComponent message(String key, Component... args) {
    return translatable().key(key).color(NamedTextColor.DARK_AQUA).args(args).build();
  }

  public static @NonNull TranslatableComponent message(
      String key, NamedTextColor color, Component... args) {
    return translatable().key(key).color(color).args(args).build();
  }

  public static String format(Component component) {
    return LegacyComponentSerializer.legacySection().serialize(component);
  }

  public static String format(String content) {
    return ChatColor.stripColor(content);
  }

  public static Component formatAmpersand(String content) {
    return LegacyComponentSerializer.legacyAmpersand().deserialize(content);
  }

  public static Component formatSection(String content) {
    return LegacyComponentSerializer.legacySection().deserialize(content);
  }
}
