package network.atria.Ranks;

import java.util.UUID;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.PermissionNode;
import network.atria.Manager.RankManager;
import network.atria.Mixed;
import network.atria.UserProfile.UserProfile;

public class setGroup {

  public void setPrefixPermission(UUID uuid) {
    LuckPerms api = LuckPermsProvider.get();
    User user = api.getUserManager().getUser(uuid);
    RankManager rankManager = new RankManager();

    UserProfile profile = Mixed.get().getProfileManager().getProfile(uuid);
    if (user != null) {
      removeGroup(user, profile.getRank().getName());
      addGroup(user, rankManager.getNextRank(uuid).getName());
      profile.setRank(rankManager.getNextRank(uuid));
      api.getUserManager().saveUser(user);
    }
  }

  private static void addGroup(User user, String groupName) {
    PermissionNode node = PermissionNode.builder("pgm.group." + groupName).build();
    user.data().add(node);
  }

  private static void removeGroup(User user, String groupName) {
    PermissionNode node = PermissionNode.builder("pgm.group." + groupName).build();
    user.data().remove(node);
  }
}
