package network.atria.Effects.Particles;

import com.github.fierioziy.particlenativeapi.api.Particles_1_8;
import java.util.*;
import network.atria.Mixed;
import network.atria.UserProfile.UserProfile;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.api.player.event.MatchPlayerDeathEvent;
import tc.oc.pgm.api.setting.SettingKey;
import tc.oc.pgm.api.setting.SettingValue;

public class KillEffects extends ParticleAPI implements Listener {

  private static List<Color> colors;

  @EventHandler
  public void onPlayerDeath(MatchPlayerDeathEvent e) {

    Particles_1_8 api = Mixed.get().getParticles();
    MatchPlayer murder;
    MatchPlayer victim = e.getVictim();

    if (e.getKiller() != null) {
      murder = e.getKiller().getPlayer().get();
      if (murder.getSettings().getValue(SettingKey.EFFECTS).equals(SettingValue.EFFECTS_OFF))
        return;
      Location location = victim.getBukkit().getLocation();
      UserProfile profile = Mixed.get().getProfileManager().getProfile(murder.getId());
      if (!Mixed.get().getEffectManager().isNone(profile.getKilleffect())) {
        switch (profile.getKilleffect().getName()) {
          case "BLOOD":
            sendEffectPacket(
                murder.getBukkit(),
                api.BLOCK_CRACK()
                    .of(Material.REDSTONE_BLOCK)
                    .packet(true, location, 0.5D, 1.5D, 0.5D, 0.1D, 200));
            break;
          case "HEART":
            sendEffectPacket(
                murder.getBukkit(),
                api.HEART().packet(true, location, 0.75D, 0.85D, 0.75D, 0.01D, 50));
            break;

          case "SMOKE":
            sendEffectPacket(
                murder.getBukkit(),
                api.SMOKE_LARGE().packet(true, location, 0.75D, 1.2D, 0.75D, 0.1D, 70));
            break;

          case "FLAME":
            sendEffectPacket(
                murder.getBukkit(),
                api.FLAME().packet(true, location, 0.75D, 1.2D, 0.75D, 0.1D, 100));
            break;
          case "RAINBOW":
            double maxheight = 7;

            for (double y = 0; y < maxheight; y += 0.05) {
              double radius = 2;
              double x = Math.sin(y * radius);
              double z = Math.cos(y * radius);

              Object packet =
                  api.REDSTONE()
                      .packetColored(
                          true,
                          (float) (location.getX() + x),
                          (float) (location.getY() + y),
                          (float) (location.getZ() + z),
                          colors.get(new Random().nextInt(colors.size())));

              api.createPlayerConnection(murder.getBukkit()).sendPacket(packet);
            }
            break;

          case "DONOR":
            double radius = 1.2d;
            Location Location = victim.getBukkit().getLocation().add(0.0D, 2.5D, 0.0D);
            int point = 30;

            sendEffectPacket(
                murder.getBukkit(),
                api.REDSTONE()
                    .packetColored(
                        true, location, colors.get(new Random().nextInt(colors.size()))));

            for (int i = 0; i < point; i++) {
              double circle = 2 * Math.PI * i / point;
              Location ring =
                  Location.clone().add(radius * Math.sin(circle), 0.0d, radius * Math.cos(circle));
              Object packet =
                  api.REDSTONE()
                      .packetColored(
                          true,
                          ring.getX(),
                          ring.getY(),
                          ring.getZ(),
                          colors.get(new Random().nextInt(colors.size())));

              api.createPlayerConnection(murder.getBukkit()).sendPacket(packet);
            }
            break;
          case "SPHERE":
            new BukkitRunnable() {
              double phi = 0;

              public void run() {
                phi += Math.PI / 10;

                for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 40) {
                  double r = 2;
                  double x = r * Math.cos(theta) * Math.sin(phi);
                  double y = r * Math.cos(phi) + 1.5;
                  double z = r * Math.sin(theta) * Math.sin(phi);

                  Location location = victim.getBukkit().getLocation();
                  location.add(x, y, z);

                  sendEffectPacket(
                      murder.getBukkit(),
                      api.CRIT_MAGIC().packet(true, location, 0D, 0D, 0D, 0.1D, 60));
                  location.subtract(x, y, z);
                }
                if (phi > 2 * Math.PI) {
                  this.cancel();
                }
              }
            }.runTaskTimer(Mixed.get(), 0, 1);
            break;

          case "MAGIC":
            Color[] magic = {Color.PURPLE, Color.FUCHSIA};
            sendEffectPacket(
                murder.getBukkit(),
                api.REDSTONE()
                    .packetColored(true, location, magic[new Random().nextInt(magic.length)]));
            sendEffectPacket(
                murder.getBukkit(),
                api.SPELL_MOB_AMBIENT().packet(false, location, 0D, 0D, 0D, 0.1D, 100));
            break;
        }
      }
    }
  }

  private void addColor() {
    colors = new ArrayList<>();

    colors.add(Color.BLUE);
    colors.add(Color.LIME);
    colors.add(Color.ORANGE);
    colors.add(Color.PURPLE);
    colors.add(Color.AQUA);
    colors.add(Color.YELLOW);
    colors.add(Color.GREEN);
    colors.add(Color.RED);
    colors.add(Color.OLIVE);
    colors.add(Color.MAROON);
    colors.add(Color.TEAL);
    colors.add(Color.NAVY);
  }

  public KillEffects(Plugin plugin) {
    addColor();
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }
}
