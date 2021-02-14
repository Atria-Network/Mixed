package network.atria.Statistics;

import java.sql.*;
import java.util.*;
import network.atria.Manager.RankManager;
import network.atria.Mixed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import tc.oc.pgm.api.match.MatchModule;
import tc.oc.pgm.api.match.MatchScope;
import tc.oc.pgm.api.match.event.MatchFinishEvent;
import tc.oc.pgm.api.match.event.MatchStartEvent;
import tc.oc.pgm.api.party.Competitor;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.api.player.MatchPlayerState;
import tc.oc.pgm.api.player.event.MatchPlayerDeathEvent;
import tc.oc.pgm.core.CoreLeakEvent;
import tc.oc.pgm.destroyable.DestroyableDestroyedEvent;
import tc.oc.pgm.events.ListenerScope;
import tc.oc.pgm.events.PlayerParticipationStartEvent;
import tc.oc.pgm.events.PlayerParticipationStopEvent;
import tc.oc.pgm.flag.event.FlagCaptureEvent;
import tc.oc.pgm.wool.PlayerWoolPlaceEvent;

@ListenerScope(MatchScope.RUNNING)
public class MatchEvents implements Listener, MatchModule {

  @EventHandler(priority = EventPriority.MONITOR)
  public void onMatchStart(MatchStartEvent event) {
    event
        .getMatch()
        .getPlayers()
        .forEach(
            player -> Mixed.get().getStatistics().countPlaytime(player.getId(), event.getMatch()));
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onJoinMatch(PlayerParticipationStartEvent event) {
    if (!event.isCancelled() && event.getMatch().isRunning()) {
      Mixed.get().getStatistics().countPlaytime(event.getPlayer().getId(), event.getMatch());
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onLeaveMatch(PlayerParticipationStopEvent event) {
    if (!event.isCancelled() && event.getMatch().isRunning()) {
      Mixed.get().getStatistics().removePlaytime(event.getPlayer().getId());
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerDeath(MatchPlayerDeathEvent event) {
    MatchPlayer victim = event.getPlayer();
    MatchPlayer murder;

    if (event.isTeamKill()) return;
    Mixed.get().getStatistics().addDeath(victim.getId());

    if (event.getKiller() != null && !event.isSelfKill() && !event.isSuicide()) {
      murder = event.getKiller().getParty().getPlayer(event.getKiller().getId());
      if (!murder.getParty().equals(victim.getParty())) {
        Mixed.get().getStatistics().addKill(murder.getId());
        Mixed.get().getStatistics().addPoint(murder.getId(), 5);
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onWoolCapture(PlayerWoolPlaceEvent event) {
    MatchPlayerState player = event.getPlayer();

    Mixed.get().getStatistics().addWool(player.getId());
    Mixed.get().getStatistics().addPoint(player.getId(), 20);
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onCoreBreak(CoreLeakEvent event) {
    event
        .getCore()
        .getTouchingPlayers()
        .forEach(
            player -> {
              Mixed.get().getStatistics().addCore(player.getId());
              Mixed.get().getStatistics().addPoint(player.getId(), 25);
            });
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onFlagCapture(FlagCaptureEvent event) {
    MatchPlayer player = event.getCarrier();

    Mixed.get().getStatistics().addFlag(player.getId());
    Mixed.get().getStatistics().addPoint(player.getId(), 15);
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onDestroyMonument(DestroyableDestroyedEvent event) {
    event
        .getDestroyable()
        .getContributions()
        .forEach(
            player -> {
              Mixed.get().getStatistics().addMonument(player.getPlayerState().getId());
              Mixed.get().getStatistics().addPoint(player.getPlayerState().getId(), 25);
            });
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onMatchFinish(MatchFinishEvent event) {
    RankManager manager = new RankManager();
    Collection<Competitor> winners = event.getWinners();

    if (!winners.isEmpty()) {
      event
          .getMatch()
          .getParticipants()
          .forEach(
              player -> {
                if (winners.contains(player.getCompetitor())) {
                  Mixed.get().getStatistics().addWins(player);
                } else {
                  Mixed.get().getStatistics().addLoses(player);
                }
              });
    }

    event
        .getMatch()
        .getParticipants()
        .forEach(
            player -> {
              Mixed.get().getStatistics().addPoint(player.getId(), 10);
              manager.RankUP(player);
            });
    Mixed.get().getStatistics().endMatch();
  }

  public MatchEvents(Plugin plugin) {
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }
}
