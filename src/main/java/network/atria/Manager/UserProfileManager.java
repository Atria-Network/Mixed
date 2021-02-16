package network.atria.Manager;

import static net.kyori.adventure.text.Component.text;
import static network.atria.MySQL.SQLQuery.*;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import network.atria.Effects.Particles.Effect;
import network.atria.Mixed;
import network.atria.MySQL;
import network.atria.UserProfile.UserProfile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class UserProfileManager implements Listener {

  private final Map<UUID, UserProfile> profiles;

  public UserProfileManager() {
    this.profiles = Maps.newConcurrentMap();
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    UUID uuid = event.getPlayer().getUniqueId();
    UserProfile profile = Mixed.get().getProfileManager().getProfile(uuid);

    pushProfile(profile);
  }

  public UserProfile createProfile(String name, UUID uuid) {
    EffectManager manager = Mixed.get().getEffectManager();

    Effect killeffect =
        manager.findEffect(getAsString("RANKS", "EFFECT", uuid)).isPresent()
            ? manager.findEffect(getAsString("RANKS", "EFFECT", uuid)).get()
            : new Effect("NONE", text("NONE"), 0, false);
    Effect killsound =
        manager.findEffect(getAsString("RANKS", "SOUND", uuid)).isPresent()
            ? manager.findEffect(getAsString("RANKS", "SOUND", uuid)).get()
            : new Effect("NONE", text("NONE"), 0, false);
    Effect projectile =
        manager.findEffect(getAsString("RANKS", "PROJECTILE", uuid)).isPresent()
            ? manager.findEffect(getAsString("RANKS", "PROJECTILE", uuid)).get()
            : new Effect("DEFAULT", text("DEFAULT"), 0, false);
    return new UserProfile(
        name,
        uuid,
        Mixed.get().getRankManager().getRank(getAsString("RANKS", "GAMERANK", uuid)),
        killeffect,
        projectile,
        killsound,
        MySQL.SQLQuery.getAsInteger("STATS", "POINTS", uuid));
  }

  public void pushProfile(UserProfile profile) {
    Connection connection = null;
    PreparedStatement statement;
    String sql =
        "UPDATE RANKS SET GAMERANK = ?, EFFECT = ?, SOUND = ?, PROJECTILE = ? WHERE UUID = ?";
    try {
      connection = MySQL.get().getHikari().getConnection();
      statement = connection.prepareStatement(sql);
      statement.setString(1, profile.getRank().getName());
      statement.setString(2, profile.getKilleffect().getName());
      statement.setString(3, profile.getKillsound().getName());
      statement.setString(4, profile.getProjectile().getName());
      statement.setString(5, profile.getUUID().toString());
      statement.executeUpdate();
      statement.close();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      if (connection != null) {
        try {
          connection.close();
        } catch (SQLException throwables) {
          throwables.printStackTrace();
        }
      }
      this.profiles.remove(profile.getUUID());
    }
  }

  public void addProfile(UUID uuid, UserProfile profile) {
    this.profiles.put(uuid, profile);
  }

  public UserProfile getProfile(UUID uuid) {
    return this.profiles.get(uuid) == null ? null : this.profiles.get(uuid);
  }

  public Collection<UserProfile> getProfiles() {
    return ImmutableList.copyOf(this.profiles.values());
  }
}
