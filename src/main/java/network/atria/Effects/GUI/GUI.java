package network.atria.Effects.GUI;

import static network.atria.Util.TextFormat.format;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.kyori.adventure.text.Component;
import network.atria.Mixed;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class GUI implements Listener {

  private final Inventory inventory;
  private final Map<Integer, Action> actions;

  public GUI(int size, Component title) {
    actions = Maps.newHashMap();

    inventory = Bukkit.createInventory(null, size, format(title));
  }

  public void setItem(int slot, ItemStack item, Action action) {
    inventory.setItem(slot, item);
    if (action != null) {
      actions.put(slot, action);
    }
  }

  public ItemStack createIcon(Component name, Material material, List<String> lores) {
    ItemStack item = new ItemStack(material);
    ItemMeta meta = item.getItemMeta();

    meta.setDisplayName(format(name));
    if (lores != null && !lores.isEmpty()) {
      meta.setLore(lores);
    }

    item.setItemMeta(meta);
    return item;
  }

  public ItemStack createIcon(Component name, Material material) {
    return createIcon(name, material, null);
  }

  public void open(Player player) {
    player.openInventory(inventory);
  }

  public void open(Player player, GUI gui) {
    player.openInventory(gui.getGUI());
  }

  public void close(Player player) {
    player.closeInventory();
  }

  public Map<Integer, Action> getActions() {
    return actions;
  }

  public Inventory getGUI() {
    return inventory;
  }

  public static class GUIListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClick(InventoryClickEvent event) {
      if (!(event.getWhoClicked() instanceof Player)) {
        return;
      }

      Player player = (Player) event.getWhoClicked();
      GUI gui = Mixed.get().getGUIManager().findGUI(event.getClickedInventory().getTitle());

      if (gui != null) {
        Action action = gui.getActions().get(event.getSlot());
        action.click(player);
        event.setCancelled(true);
      }
    }
  }

  public interface Action {
    void click(Player player);
  }
}
