package network.atria.Effects.GUI.GUIes;

import static net.kyori.adventure.text.Component.text;
import static network.atria.Util.TextFormat.format;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import network.atria.Effects.GUI.GUI;
import network.atria.Manager.EffectManager;
import network.atria.Mixed;
import network.atria.MySQL;
import network.atria.UserProfile.UserProfile;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ProjectilesGUI extends GUI {

  public ProjectilesGUI() {
    super(27, text("Projectile Trails"));
    initializeItems();
  }

  public void initializeItems() {
    ItemStack reset = new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.RED.getData());
    ItemMeta reset_meta = reset.getItemMeta();

    reset_meta.setDisplayName(
        format(text("Reset Projectile Trails", NamedTextColor.RED, TextDecoration.BOLD)));
    reset.setItemMeta(reset_meta);
    setItem(
        26,
        reset,
        player -> {
          Audience audience = Mixed.get().getAudience().player(player);
          audience.sendMessage(text("Reset your Projectile Trails", NamedTextColor.GREEN));
          MySQL.SQLQuery.update("RANKS", "PROJECTILE", "NONE", player.getUniqueId());
        });

    ItemStack back = new ItemStack(Material.ARROW);
    ItemMeta back_meta = back.getItemMeta();

    back_meta.setDisplayName(
        format(text("Go to previous page ➡", NamedTextColor.RED, TextDecoration.BOLD)));
    back.setItemMeta(back_meta);
    setItem(8, back, player -> open(player, Mixed.get().getGUIManager().getMainGUI()));

    EffectManager manager = Mixed.get().getEffectManager();
    EffectManager.getProjectiles()
        .forEach(
            projectile ->
                setItem(
                    projectile.getSlot(),
                    projectile.getIcon(),
                    player -> {
                      UserProfile profile =
                          Mixed.get().getProfileManager().getProfile(player.getUniqueId());
                      Audience audience = Mixed.get().getAudience().player(player);

                      if (projectile.isDonorOnly(player)) {
                        MySQL.SQLQuery.update(
                            "RANKS",
                            "PROJECTILE",
                            projectile.getUncoloredName(),
                            player.getUniqueId());
                        profile.setProjectile(projectile);
                        audience.sendMessage(
                            text("You selected ", NamedTextColor.GREEN)
                                .append(text(profile.getName()))
                                .append(text(" projectile trail", NamedTextColor.GREEN)));
                      } else if (projectile.canUseDonor()
                          || manager.hasRequirePoint(projectile, player.getUniqueId())) {
                        MySQL.SQLQuery.update(
                            "RANKS",
                            "PROJECTILE",
                            projectile.getUncoloredName(),
                            player.getUniqueId());
                        profile.setProjectile(projectile);
                        audience.sendMessage(
                            text("You selected ", NamedTextColor.GREEN)
                                .append(text(projectile.getName()))
                                .append(text(" projectile trail", NamedTextColor.GREEN)));
                      } else {
                        audience.sendMessage(
                            text("You don't have enough points.", NamedTextColor.RED));
                      }
                    }));
  }

  @Override
  public Inventory getGUI() {
    return super.getGUI();
  }
}
