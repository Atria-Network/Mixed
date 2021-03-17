package network.atria.GUI.GUIes;

import static net.kyori.adventure.text.Component.text;
import static network.atria.Util.TextFormat.format;
import static network.atria.Util.TextFormat.message;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import network.atria.Effects.Particles.Effect;
import network.atria.GUI.GUI;
import network.atria.Manager.EffectManager;
import network.atria.Mixed;
import network.atria.UserProfile.UserProfile;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ProjectilesGUI extends GUI {

  public ProjectilesGUI() {
    super(27, text("Projectile Trails").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD));
    initializeItems();
  }

  public void initializeItems() {
    ItemStack reset = new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.RED.getData());
    ItemMeta reset_meta = reset.getItemMeta();

    reset_meta.setDisplayName(format(text("Reset your Projectile").color(NamedTextColor.RED)));
    reset.setItemMeta(reset_meta);
    setItem(
        26,
        reset,
        player -> {
          Audience audience = Mixed.get().getAudience().player(player);
          UserProfile profile = Mixed.get().getProfileManager().getProfile(player.getUniqueId());
          audience.sendMessage(
              message("gui.effect.reset", NamedTextColor.GREEN, text("Projectile")));
          profile.setProjectile(new Effect("NONE", null, 0, 0, false, false));
        });

    ItemStack back = new ItemStack(Material.ARROW);
    ItemMeta back_meta = back.getItemMeta();

    back_meta.setDisplayName(format(text("Go to previous page ⇒").color(NamedTextColor.RED)));
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
                        profile.setProjectile(projectile);
                        audience.sendMessage(
                            message(
                                "gui.effect.select",
                                NamedTextColor.GREEN,
                                text(profile.getName()),
                                text("projectile")));
                      } else if (projectile.canUseDonor()
                          || manager.hasRequirePoint(projectile, profile)) {
                        profile.setProjectile(projectile);
                        audience.sendMessage(
                            message(
                                "gui.effect.select",
                                NamedTextColor.GREEN,
                                text(profile.getName()),
                                text("projectile")));
                      } else {
                        audience.sendMessage(
                            message("gui.effect.select.reject", NamedTextColor.RED));
                      }
                    }));
  }

  @Override
  public Inventory getGUI() {
    return super.getGUI();
  }
}
