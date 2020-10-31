package network.atria.Task;

import java.util.*;
import network.atria.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class BroadCastMesseage {

  private final String prefix =
      ChatColor.WHITE
          + ""
          + ChatColor.BOLD
          + "["
          + ChatColor.BLUE
          + ""
          + ChatColor.BOLD
          + "TIP"
          + ChatColor.WHITE
          + ""
          + ChatColor.BOLD
          + "] "
          + ChatColor.DARK_AQUA;

  public void randomMesseage() {

    final String messeage = random(null);

    new BukkitRunnable() {
      @Override
      public void run() {
        Bukkit.broadcastMessage(
            prefix + ChatColor.translateAlternateColorCodes('&', random(messeage)));
      }
    }.runTaskTimer(Main.getInstance(), 0L, 20 * 60 * 5);
  }

  private String random(String msg) {
    final List<String> messeages = Main.getInstance().getConfig().getStringList("Messeages");

    final int index = (new Random()).nextInt(messeages.size());
    msg = messeages.get(index);
    return msg;
  }
}
