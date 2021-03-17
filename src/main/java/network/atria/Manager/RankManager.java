package network.atria.Manager;

import static net.kyori.adventure.text.Component.text;
import static network.atria.Util.TextFormat.message;

import com.google.common.collect.Lists;
import java.util.*;
import net.kyori.adventure.audience.Audience;
import net.md_5.bungee.api.ChatColor;
import network.atria.Mixed;
import network.atria.Ranks.Rank;
import network.atria.Ranks.setGroup;
import network.atria.UserProfile.UserProfile;
import network.atria.Util.RanksConfig;
import network.atria.Util.TextFormat;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.util.LegacyFormatUtils;

public class RankManager {

  private static List<Rank> ranks;

  public RankManager() {
    ranks = Lists.newArrayList();
    setupRanks();
  }

  private void setupRanks() {
    FileConfiguration config = RanksConfig.getCustomConfig();
    if (ranks == null || ranks.isEmpty()) {
      config
          .getConfigurationSection("Ranks")
          .getKeys(false)
          .forEach(
              section ->
                  ranks.add(
                      new Rank(
                          section,
                          TextFormat.formatAmpersand(
                              config.getString("Ranks." + section + ".Display-Name")),
                          config.getInt("Ranks." + section + ".Points"))));
    }
  }

  public Rank getNextRank(UUID uuid) {
    UserProfile profile = Mixed.get().getProfileManager().getProfile(uuid);
    for (int i = ranks.size() - 1; i >= 0; i--) {
      if (profile.getRank().equals(ranks.get(i))) {
        try {
          return ranks.get(i + 1);
        } catch (IndexOutOfBoundsException | NullPointerException e) {
          return profile.getRank();
        }
      }
    }
    return null;
  }

  public Integer getRequirePoint(UUID uuid) {
    Rank NEXT = ranks.contains(getNextRank(uuid)) ? getNextRank(uuid) : null;

    UserProfile profile = Mixed.get().getProfileManager().getProfile(uuid);
    if (NEXT == null) return null;
    return NEXT.getPoint() - profile.getPoints();
  }

  public boolean canRankUp(UUID uuid) {
    UserProfile profile = Mixed.get().getProfileManager().getProfile(uuid);
    if (getNextRank(uuid) == null || getNextRank(uuid).equals(profile.getRank())) return false;
    return getRequirePoint(uuid) <= 0;
  }

  public void RankUP(MatchPlayer player) {
    UUID uuid = player.getId();
    setGroup chatPrefix = new setGroup();
    Rank next = getNextRank(uuid);
    Audience audience = Mixed.get().getAudience().player(uuid);

    UserProfile profile = Mixed.get().getProfileManager().getProfile(uuid);
    if (canRankUp(uuid)) {
      audience.sendMessage(
          message("rank.rankup.broadcast", text(player.getPrefixedName()), next.getColoredName()));

      player
          .getBukkit()
          .playSound(player.getBukkit().getLocation(), Sound.LEVEL_UP, 10, (float) 0.3);

      audience.sendMessage(
          text(LegacyFormatUtils.horizontalLineHeading("Rank UP!", ChatColor.DARK_BLUE, 300)));
      audience.sendMessage(
          message("rank.rankup", profile.getRank().getColoredName(), next.getColoredName()));
      audience.sendMessage(text(LegacyFormatUtils.horizontalLine(ChatColor.DARK_BLUE, 300)));

      chatPrefix.setPrefixPermission(uuid);
    }
  }

  public Rank getRank(String Name) {
    return ranks.stream().filter(rank -> Name.equals(rank.getName())).findFirst().orElse(null);
  }

  public List<Rank> getRanks() {
    return Collections.unmodifiableList(ranks);
  }
}
