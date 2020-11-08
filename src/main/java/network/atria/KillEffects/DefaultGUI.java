package network.atria.KillEffects;

import app.ashcon.intake.Command;
import app.ashcon.intake.bukkit.parametric.annotation.Sender;
import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class DefaultGUI implements Listener {

  public static Inventory gui;

  public DefaultGUI(Plugin plugin) {
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
    gui = Bukkit.createInventory(null, 27, "Main GUI");
    addIconItems();
  }

  private void addIconItems() {

    final ItemStack close = new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.RED.getData());
    final ItemMeta meta = close.getItemMeta();

    meta.setDisplayName(ChatColor.RED + "Close the GUI");
    close.setItemMeta(meta);

    gui.setItem(10, createGuiItem(Material.REDSTONE, ChatColor.GREEN + "Kill Effects"));
    gui.setItem(12, createGuiItem(Material.RECORD_3, ChatColor.GREEN + "Kill Sounds"));
    gui.setItem(14, createGuiItem(Material.BOW, ChatColor.GREEN + "Projectile Trails"));
    gui.setItem(17, close);
  }

  @EventHandler
  public void onGuiClick(final InventoryClickEvent e) {
    try {
      if (e.getView().getTitle().equals("Main GUI")) {
        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();
        final Player player = (Player) e.getWhoClicked();
        final String getItemName = clickedItem.getItemMeta().getDisplayName();

        if (clickedItem.hasItemMeta()) {
          if (clickedItem.getItemMeta().hasDisplayName()) {
            if (getItemName.equals(ChatColor.RED + "Close the GUI")) {
              player.closeInventory();
            } else if (getItemName.equals(ChatColor.GREEN + "Kill Effects")) {
              player.openInventory(KillEffectsGUI.effect);
            } else if (getItemName.equals(ChatColor.GREEN + "Kill Sounds")) {
              player.openInventory(KillSoundsGUI.sound);
            } else if (getItemName.equals(ChatColor.GREEN + "Projectile Trails")) {
              player.openInventory(ProjectileGUI.projectile);
            }
          }
        }
      }
    } catch (NullPointerException ignored) {
    }
  }

  protected static ItemStack createGuiItem(
      final Material material, final String name, final String... lore) {
    final ItemStack item = new ItemStack(material, 1);
    final ItemMeta meta = item.getItemMeta();

    meta.setDisplayName(name);
    meta.setLore(Arrays.asList(lore));
    item.setItemMeta(meta);

    return item;
  }

  @Command(
      aliases = {"effect", "sound", "projectile"},
      desc = "Open Select GUI")
  public boolean gui(@Sender Player sender) {
    sender.openInventory(gui);
    return true;
  }
}
