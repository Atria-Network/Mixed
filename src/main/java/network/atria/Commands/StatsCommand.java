package network.atria.Commands;

import static net.kyori.adventure.text.Component.text;
import static network.atria.MySQL.SQLQuery.*;

import app.ashcon.intake.Command;
import app.ashcon.intake.bukkit.parametric.annotation.Sender;
import com.google.common.collect.Maps;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;
import network.atria.Mixed;
import network.atria.MySQL;
import network.atria.Util.Fetcher;
import network.atria.Util.TextFormat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import tc.oc.pgm.api.PGM;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.util.LegacyFormatUtils;

public class StatsCommand {

  @Command(
      aliases = {"stats"},
      desc = "Show Player Stats",
      usage = "[Target]")
  public void stats(@Sender Player player, @Nullable String playerName) {
    MatchPlayer matchPlayer = PGM.get().getMatchManager().getPlayer(player);
    if (matchPlayer == null) return;
    if (playerName == null) {
      showStats(matchPlayer.getId(), player);
    } else if (Bukkit.getPlayer(playerName) != null) {
      MatchPlayer target = PGM.get().getMatchManager().getPlayer(Bukkit.getPlayer(playerName));
      if (target != null) {
        showStats(target.getId(), player);
      }
    } else if (MySQL.SQLQuery.playerExists(Fetcher.getUUID(playerName))) {
      showStats(Fetcher.getUUID(playerName), player);
    } else {
      Audience audience = Mixed.get().getAudience().player(matchPlayer.getId());
      audience.sendMessage(Component.text("The player not found", NamedTextColor.RED));
    }
  }

  private void showStats(UUID targetUUID, MatchPlayer sender) {
    Map<String, Object> stats = MySQL.query().getStats(targetUUID);
    MatchPlayer target = PGM.get().getMatchManager().getPlayer(targetUUID);
    String prefixedName =
        target != null
            ? target.getPrefixedName()
            : TextFormat.format(text(Fetcher.getName(targetUUID), NamedTextColor.AQUA));
    Audience audience = Mixed.get().getAudience().player(sender.getBukkit());

    int kills = (Integer) stats.get("kill");
    int deaths = (Integer) stats.get("death");
    int wools = (Integer) stats.get("wool");
    int cores = (Integer) stats.get("core");
    int monuments = (Integer) stats.get("monument");
    int flags = (Integer) stats.get("flag");
    int wins = (Integer) stats.get("win");
    int losses = (Integer) stats.get("lose");
    double kd = divide(kills, deaths).doubleValue();
    double wl = divide(wins, deaths).doubleValue();

    audience.sendMessage(
        text(LegacyFormatUtils.horizontalLineHeading(prefixedName, ChatColor.BLUE, 250)));
    audience.sendMessage(
        formatStats("Kills: ", kills)
            .append(formatStats("Deaths: ", deaths))
            .append(text("K/D: ", NamedTextColor.DARK_AQUA).append(text(kd, NamedTextColor.AQUA))));
    audience.sendMessage(
        text(LegacyFormatUtils.horizontalLineHeading("§bObjectives", ChatColor.BLUE, 250)));
    audience.sendMessage(
        formatStats("Wools: ", wools)
            .append(formatStats("Cores: ", cores))
            .append(formatStats("Monuments: ", monuments))
            .append(formatStats("Flags: ", flags)));
    audience.sendMessage(
        text(LegacyFormatUtils.horizontalLineHeading("§bOthers", ChatColor.BLUE, 250)));
    audience.sendMessage(
        formatStats("Wins: ", wins)
            .append(
                formatStats("Loses: ", loses)
                    .append(text("W/L: ", NamedTextColor.DARK_AQUA))
                    .append(text(wd, NamedTextColor.AQUA))));
  }

  private TextComponent formatStats(String ladder, int value) {
    return text()
        .append(text(ladder, NamedTextColor.DARK_AQUA))
        .append(text(value, NamedTextColor.AQUA))
        .append(text(" | ", NamedTextColor.WHITE))
        .build();
  }

  private BigDecimal divide(int kills, int deaths) {
    BigDecimal bd1 = new BigDecimal(kills);
    BigDecimal bd2 = new BigDecimal(deaths);
    BigDecimal result;
    try {
      result = bd1.divide(bd2, 2, RoundingMode.HALF_UP);
    } catch (ArithmeticException e) {
      result = BigDecimal.ZERO;
    }
    return result;
  }
}
