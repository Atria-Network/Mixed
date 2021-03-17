package network.atria.Manager;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import network.atria.Mixed;
import network.atria.MySQL;
import network.atria.UserProfile.UserProfile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class UserProfileManager implements Listener {

  private final Map<UUID, UserProfile> profiles;

  public UserProfileManager() {
    this.profiles = Maps.newHashMap();
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    UUID uuid = event.getPlayer().getUniqueId();
    UserProfile profile = Mixed.get().getProfileManager().getProfile(uuid);

    MySQL.query().updateProfile(profile);
    this.profiles.remove(profile.getUUID());
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
