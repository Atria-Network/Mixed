package network.atria.Effects.GUI;

import static net.kyori.adventure.text.Component.text;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import network.atria.Effects.Particles.Effect;
import network.atria.Mixed;
import network.atria.MySQL;
import network.atria.UserProfile.UserProfile;
import network.atria.Util.TextFormat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CustomGUI implements Listener {

  public Inventory createGUI(Component title, int size) {
    return Bukkit.createInventory(null, size, TextFormat.format(title));
  }

  public void setItem(
      Inventory gui, int number, Material material, Component name, Component... lores) {
    ItemStack item = new ItemStack(material, 1);
    ItemMeta meta = item.getItemMeta();
    List<String> lore = Lists.newArrayList();
    Arrays.stream(lores).forEachOrdered(x -> lore.add(TextFormat.format(x)));
    meta.setDisplayName(TextFormat.format(name));
    meta.setLore(lore);
    item.setItemMeta(meta);

    setDefaultItem(gui);
    gui.setItem(number, item);
    lore.clear();
  }

  public void setDefaultItem(Inventory gui) {
    ItemStack back = new ItemStack(Material.ARROW, 1);
    ItemMeta back_meta = back.getItemMeta();

    back_meta.setDisplayName(TextFormat.format(text("Go to previous page ➡", NamedTextColor.RED)));
    back.setItemMeta(back_meta);

    gui.setItem(8, back);
  }

  public TextComponent canUseEffects(UUID uuid, Integer require) {
    UserProfile profile = Mixed.get().getProfileManager().getProfile(uuid);
    if (profile.getPoints() >= require) {
      return text("✔ Unlocked", NamedTextColor.GREEN, TextDecoration.BOLD);
    } else {
      return TextComponent.ofChildren(
          text("✖ ", NamedTextColor.RED, TextDecoration.BOLD),
          text(formatPoints(require)),
          text(" is required", NamedTextColor.RED, TextDecoration.BOLD));
    }
  }

  private String formatPoints(int points) {
    String rank = null;
    if (points <= 0) {
      rank = format("WOOD III", NamedTextColor.GOLD);
    } else if (points <= 250) {
      rank = format("WOOD II", NamedTextColor.GOLD);
    } else if (points <= 500) {
      rank = format("WOOD I", NamedTextColor.GOLD);
    } else if (points <= 1000) {
      rank = format("STONE III", NamedTextColor.GRAY);
    } else if (points <= 1500) {
      rank = format("STONE II", NamedTextColor.GRAY);
    } else if (points <= 2000) {
      rank = format("STONE I", NamedTextColor.GRAY);
    } else if (points <= 3000) {
      rank = format("IRON III", NamedTextColor.WHITE);
    } else if (points <= 4000) {
      rank = format("IRON II", NamedTextColor.WHITE);
    } else if (points <= 5000) {
      rank = format("IRON I", NamedTextColor.WHITE);
    } else if (points <= 6000) {
      rank = format("GOLD III", NamedTextColor.YELLOW);
    } else if (points <= 8000) {
      rank = format("GOLD I", NamedTextColor.YELLOW);
    } else if (points <= 10000) {
      rank = format("GOLD I", NamedTextColor.YELLOW);
    } else if (points <= 12000) {
      rank = format("EMERALD III", NamedTextColor.GREEN);
    } else if (points <= 15000) {
      rank = format("EMERALD II", NamedTextColor.GREEN);
    } else if (points <= 20000) {
      rank = format("EMERALD I", NamedTextColor.GREEN);
    } else if (points <= 30000) {
      rank = format("DIAMOND III", NamedTextColor.AQUA);
    } else if (points <= 40000) {
      rank = format("DIAMOND II", NamedTextColor.AQUA);
    } else if (points <= 50000) {
      rank = format("DIAMOND I", NamedTextColor.AQUA);
    } else if (points <= 100000) {
      rank = format("OBSIDIAN", NamedTextColor.DARK_PURPLE);
    }
    return rank;
  }

  private static String format(String rank, NamedTextColor color) {
    return TextFormat.format(text(rank, color, TextDecoration.BOLD));
  }

  @EventHandler
  public void onGuiClick(InventoryClickEvent event) {
    if (event
            .getView()
            .getTitle()
            .equalsIgnoreCase(
                TextFormat.format(
                    text("Kill Effect Select Menu", Style.style(TextDecoration.BOLD))))
        || event
            .getView()
            .getTitle()
            .equalsIgnoreCase(
                TextFormat.format(text("Kill Sound Select Menu", Style.style(TextDecoration.BOLD))))
        || event
            .getView()
            .getTitle()
            .equalsIgnoreCase(
                TextFormat.format(
                    text("Projectile Trails Select Menu", Style.style(TextDecoration.BOLD))))) {
      event.setCancelled(true);

      ItemStack clickedItem = event.getCurrentItem();
      Player player = (Player) event.getWhoClicked();
      Audience audience = Mixed.get().getAudience().player(player);
      UserProfile profile = Mixed.get().getProfileManager().getProfile(player.getUniqueId());

      if (clickedItem.getType() == Material.AIR) return;
      if (clickedItem.hasItemMeta()) {
        switch (TextFormat.format(clickedItem.getItemMeta().getDisplayName())) {
          case "Go to previous page ➡":
            player.openInventory(DefaultGUI.gui);
            break;
          case "Reset Kill Effect":
            MySQL.SQLQuery.update("RANKS", "EFFECT", "NONE", player.getUniqueId());
            profile.setKilleffect(
                new Effect(
                    "NONE", text("NONE", NamedTextColor.GREEN, TextDecoration.BOLD), 0, false));
            audience.sendMessage(
                text("Reset your ", NamedTextColor.GREEN)
                    .append(text("Kill Effect", NamedTextColor.YELLOW)));
            player.closeInventory();
            break;
          case "Reset Kill Sound":
            MySQL.SQLQuery.update("RANKS", "SOUND", "NONE", player.getUniqueId());
            profile.setKillsound(
                new Effect(
                    "NONE", text("NONE", NamedTextColor.GREEN, TextDecoration.BOLD), 0, false));
            audience.sendMessage(
                text("Reset your ", NamedTextColor.GREEN)
                    .append(text("Kill Sound", NamedTextColor.YELLOW)));
            player.closeInventory();
            break;
          case "Reset Projectile Trails":
            MySQL.SQLQuery.update("RANKS", "PROJECTILE", "DEFAULT", player.getUniqueId());
            profile.setProjectile(
                new Effect(
                    "DEFAULT",
                    text("DEFAULT", NamedTextColor.GREEN, TextDecoration.BOLD),
                    0,
                    false));
            audience.sendMessage(
                text("Reset your ", NamedTextColor.GREEN)
                    .append(text("Projectile Trails", NamedTextColor.YELLOW)));
            player.closeInventory();
            break;
        }
      }
    }
  }
}
