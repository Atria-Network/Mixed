package network.atria.KillEffects;

import network.atria.Database.MySQLSetterGetter;
import network.atria.Util.EffectUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import tc.oc.pgm.api.match.MatchScope;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.api.player.event.MatchPlayerDeathEvent;
import tc.oc.pgm.api.setting.SettingKey;
import tc.oc.pgm.api.setting.SettingValue;
import tc.oc.pgm.events.ListenerScope;
import tc.oc.pgm.util.chat.Sound;

@ListenerScope(MatchScope.RUNNING)
public class KillSounds extends EffectUtils implements Listener {

  private void playSound(MatchPlayer player, Sound sound) {
    if (player.getSettings().getValue(SettingKey.SOUNDS).equals(SettingValue.SOUNDS_ALL)) {
      player.playSound(sound);
    }
  }

  @EventHandler
  public void onMatchPlayerDeath(MatchPlayerDeathEvent e) {
    MatchPlayer killer = null;
    final MatchPlayer victim = e.getVictim();

    if (e.getKiller() != null) {
      killer = e.getKiller().getParty().getPlayer(e.getKiller().getId());

      final String getSound = MySQLSetterGetter.getKillSound(killer.getId().toString());
      final Vector death = victim.getBukkit().getLocation().toVector();

      switch (getSound) {
        case "DEFAULT":
          if (!killer.getParty().equals(victim.getParty())) {
            playSound(killer, new Sound("random.levelup", 1f, 1.5f));
            playSound(killer, new Sound("mob.irongolem.hit", 1, 4f / 3f, death));
          } else {
            playSound(victim, new Sound("mob.irongolem.hit", death));
          }
          break;
        case "HOWL":
          if (!killer.getParty().equals(victim.getParty())) {
            if (hasRequirePoint(killer.getId(), getRequirePoints("KILL_SOUND", "HOWL"))) {
              playSound(killer, new Sound("mob.wolf.howl", 1f, 1f));
            } else {
              playSound(victim, new Sound("mob.irongolem.death", death));
            }
          }
          break;
        case "VILLAGER":
          if (!killer.getParty().equals(victim.getParty())) {
            if (hasRequirePoint(killer.getId(), getRequirePoints("KILL_SOUND", "VILLAGER"))) {
              playSound(killer, new Sound("mob.villager.death", 2f, 0.8f));
            } else {
              playSound(victim, new Sound("mob.irongolem.death", death));
            }
          }
          break;
        case "BOMB":
          if (!killer.getParty().equals(victim.getParty())) {
            if (hasRequirePoint(killer.getId(), getRequirePoints("KILL_SOUND", "BOMB"))) {
              playSound(killer, new Sound("random.explode", 1f, 2f));
            } else {
              playSound(victim, new Sound("mob.irongolem.death", death));
            }
          }
          break;
        case "BURP":
          if (!killer.getParty().equals(victim.getParty())) {
            if (hasRequirePoint(killer.getId(), getRequirePoints("KILL_SOUND", "BURP"))) {
              playSound(killer, new Sound("random.burp", 1f, 0.1f));
            } else {
              playSound(victim, new Sound("mob.irongolem.death", death));
            }
          }
          break;
      }
    } else {
      playSound(victim, new Sound("mob.irongolem.death"));
    }
  }

  public KillSounds(Plugin plugin) {
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }
}
