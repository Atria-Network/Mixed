package network.atria.Commands;

import static net.kyori.adventure.text.Component.text;
import static network.atria.Util.TextFormat.message;

import app.ashcon.intake.Command;
import app.ashcon.intake.bukkit.parametric.annotation.Sender;
import java.util.UUID;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;
import network.atria.Mixed;
import network.atria.UserProfile.UserProfile;
import org.bukkit.entity.Player;
import tc.oc.pgm.util.LegacyFormatUtils;

public class RankCommand {

  @Command(
      aliases = {"rank"},
      desc = "Show Currently your Rank and Points")
  public void rank(@Sender Player sender) {
    UUID uuid = sender.getUniqueId();
    Audience audience = Mixed.get().getAudience().player(sender);
    UserProfile profile = Mixed.get().getProfileManager().getProfile(uuid);
    
    audience.sendMessage(
        text(LegacyFormatUtils.horizontalLineHeading("§bRank", ChatColor.BLUE, 200)));
    audience.sendMessage(message("rank.command.line1", profile.getRank().getColoredName()));
    audience.sendMessage(
        message("rank.command.line2", text(profile.getPoints(), NamedTextColor.AQUA)));
    audience.sendMessage(
        message(
            "rank.command.line3",
            text(Mixed.get().getRankManager().getRequirePoint(uuid), NamedTextColor.AQUA),
            Mixed.get().getRankManager().getNextRank(uuid).getColoredName()));
    audience.sendMessage(text(LegacyFormatUtils.horizontalLine(ChatColor.BLUE, 200)));
  }
}
