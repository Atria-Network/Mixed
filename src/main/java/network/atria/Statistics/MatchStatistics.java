package network.atria.Statistics;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import network.atria.Manager.UserProfileManager;
import network.atria.Mixed;
import network.atria.UserProfile.UserProfile;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.player.MatchPlayer;

public class MatchStatistics {

  private final Map<UUID, BukkitTask> playtimeTask;
  private final Map<UUID, AtomicInteger> playtime;
  private final UserProfileManager manager;

  public MatchStatistics() {
    this.playtimeTask = Maps.newConcurrentMap();
    this.playtime = Maps.newConcurrentMap();
    this.manager = Mixed.get().getProfileManager();
  }

  public void endMatch() {
    clearPlaytime();
  }

  public void addKill(UUID uuid) {
    UserProfile profile = manager.getProfile(uuid);
    AtomicInteger kills = new AtomicInteger(profile.getKills());
    AtomicInteger weekly_kills = new AtomicInteger(profile.getWeekly_kills());

    profile.setKills(kills.incrementAndGet());
    profile.setWeekly_kills(weekly_kills.incrementAndGet());
  }

  public void addDeath(UUID uuid) {
    UserProfile profile = manager.getProfile(uuid);
    AtomicInteger deaths = new AtomicInteger(profile.getDeaths());
    AtomicInteger weekly_deaths = new AtomicInteger(profile.getWeekly_deaths());

    profile.setDeaths(deaths.incrementAndGet());
    profile.setWeekly_deaths(weekly_deaths.incrementAndGet());
  }

  public void addWool(UUID uuid) {
    UserProfile profile = manager.getProfile(uuid);
    AtomicInteger wools = new AtomicInteger(profile.getWools());
    AtomicInteger weekly_wools = new AtomicInteger(profile.getWeekly_wools());

    profile.setWools(wools.incrementAndGet());
    profile.setWeekly_wools(weekly_wools.incrementAndGet());
  }

  public void addMonument(UUID uuid) {
    UserProfile profile = manager.getProfile(uuid);
    AtomicInteger monuments = new AtomicInteger(profile.getMonuments());
    AtomicInteger weekly_monuments = new AtomicInteger(profile.getWeekly_monuments());

    profile.setMonuments(monuments.incrementAndGet());
    profile.setWeekly_monuments(weekly_monuments.incrementAndGet());
  }

  public void addCore(UUID uuid) {
    UserProfile profile = manager.getProfile(uuid);
    AtomicInteger cores = new AtomicInteger(profile.getMonuments());
    AtomicInteger weekly_cores = new AtomicInteger(profile.getWeekly_cores());

    profile.setMonuments(cores.incrementAndGet());
    profile.setWeekly_cores(weekly_cores.incrementAndGet());
  }

  public void addFlag(UUID uuid) {
    UserProfile profile = manager.getProfile(uuid);
    AtomicInteger flags = new AtomicInteger(profile.getFlags());
    AtomicInteger weekly_flags = new AtomicInteger(profile.getWeekly_flags());

    profile.setFlags(flags.incrementAndGet());
    profile.setWeekly_flags(weekly_flags.incrementAndGet());
  }

  public void addPoint(UUID uuid, int point) {
    UserProfile profile = manager.getProfile(uuid);
    AtomicInteger points = new AtomicInteger(profile.getPoints());

    profile.setPoints(points.addAndGet(point));
  }

  public void addWins(MatchPlayer player) {
    UserProfile profile = manager.getProfile(player.getId());
    AtomicInteger wins = new AtomicInteger(profile.getWins());
    AtomicInteger weekly_wins = new AtomicInteger(profile.getWeekly_wins());

    profile.setWins(wins.incrementAndGet());
    profile.setWeekly_wins(weekly_wins.incrementAndGet());
  }

  public void addLoses(MatchPlayer player) {
    UserProfile profile = manager.getProfile(player.getId());
    AtomicInteger loses = new AtomicInteger(profile.getLoses());
    AtomicInteger weekly_loses = new AtomicInteger(profile.getWeekly_loses());

    profile.setLoses(loses.incrementAndGet());
    profile.setWeekly_loses(weekly_loses.incrementAndGet());
  }

  public void countPlaytime(UUID uuid, Match match) {
    playtime.putIfAbsent(uuid, new AtomicInteger());

    BukkitTask task =
        Bukkit.getScheduler()
            .runTaskTimerAsynchronously(
                Mixed.get(),
                () -> {
                  if (!match.isRunning()) return;
                  playtime.get(uuid).incrementAndGet();
                },
                0L,
                20L);
    BukkitTask old = playtimeTask.put(uuid, task);
    if (old != null) old.cancel();
  }

  public void removePlaytime(UUID uuid) {
    BukkitTask old = playtimeTask.remove(uuid);
    UserProfile profile = manager.getProfile(uuid);

    profile.setPlaytime(profile.getPlaytime() + playtime.get(uuid).get());
    profile.setWeekly_playtime(profile.getWeekly_playtime() + playtime.get(uuid).get());

    playtime.remove(uuid);
    if (old != null) old.cancel();
  }

  private void clearPlaytime() {
    playtime.forEach(
        ((uuid, atomicInteger) -> {
          UserProfile profile = manager.getProfile(uuid);

          profile.setPlaytime(profile.getPlaytime() + atomicInteger.get());
          profile.setWeekly_playtime(profile.getWeekly_playtime() + atomicInteger.get());
        }));
    playtime.clear();
    playtimeTask.forEach(((uuid, bukkitTask) -> bukkitTask.cancel()));
    playtimeTask.clear();
  }

  public Map<UUID, AtomicInteger> getPlaytime() {
    return playtime;
  }
}
