package network.atria.Statistics;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import network.atria.Mixed;
import network.atria.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import tc.oc.pgm.api.match.Match;

public class MatchStatistics {

  private final Map<UUID, BukkitTask> playtimeTask;
  private StoreStatistics stats;

  public MatchStatistics() {
    this.playtimeTask = Maps.newConcurrentMap();
  }

  public void endMatch() {
    clearPlaytime();
  }

  public void newMatch() {
    this.stats = new StoreStatistics();
  }

  public void addKill(UUID uuid) {
    AtomicInteger kills = stats.getKills().computeIfAbsent(uuid, x -> new AtomicInteger());
    kills.incrementAndGet();
  }

  public void addDeath(UUID uuid) {
    AtomicInteger deaths = stats.getDeaths().computeIfAbsent(uuid, x -> new AtomicInteger());
    deaths.incrementAndGet();
  }

  public void addWool(UUID uuid) {
    AtomicInteger wools = stats.getWools().computeIfAbsent(uuid, x -> new AtomicInteger());
    wools.incrementAndGet();
  }

  public void addMonument(UUID uuid) {
    AtomicInteger monuments = stats.getMonuments().computeIfAbsent(uuid, x -> new AtomicInteger());
    monuments.incrementAndGet();
  }

  public void addCore(UUID uuid) {
    AtomicInteger cores = stats.getCores().computeIfAbsent(uuid, x -> new AtomicInteger());
    cores.incrementAndGet();
  }

  public void addFlag(UUID uuid) {
    AtomicInteger flags = stats.getFlags().computeIfAbsent(uuid, x -> new AtomicInteger());
    flags.incrementAndGet();
  }

  public void addPoint(UUID uuid, int point) {
    AtomicInteger points = stats.getPoints().computeIfAbsent(uuid, x -> new AtomicInteger());
    points.addAndGet(point);
  }

  public void addWins(UUID uuid) {
    AtomicInteger wins = stats.getWins().computeIfAbsent(uuid, x -> new AtomicInteger());
    wins.incrementAndGet();
  }

  public void addLosses(UUID uuid) {
    AtomicInteger loses = stats.getLosses().computeIfAbsent(uuid, x -> new AtomicInteger());
    loses.incrementAndGet();
  }

  public void countPlaytime(UUID uuid, Match match) {
    stats.getPlaytime().putIfAbsent(uuid, new AtomicInteger());
    BukkitTask task =
        Bukkit.getScheduler()
            .runTaskTimerAsynchronously(
                Mixed.get(),
                () -> {
                  if (!match.isRunning()) return;
                  stats.getPlaytime().get(uuid).incrementAndGet();
                },
                0L,
                20L);
    BukkitTask old = playtimeTask.put(uuid, task);
    if (old != null) old.cancel();
  }

  public void removePlaytime(UUID uuid) {
    BukkitTask old = playtimeTask.remove(uuid);
    stats.getPlaytime().remove(uuid);
    if (old != null) old.cancel();
  }

  private void clearPlaytime() {
    playtimeTask.forEach(((uuid, bukkitTask) -> bukkitTask.cancel()));
    playtimeTask.clear();
  }

  public void updateStats(UUID uuid) {
    int kills = stats.getKills(uuid);
    int deaths = stats.getDeaths(uuid);
    int flags = stats.getFlags(uuid);
    int cores = stats.getCores(uuid);
    int wools = stats.getWools(uuid);
    int monuments = stats.getMonuments(uuid);
    int playtime = stats.getPlaytime(uuid);
    int wins = stats.getWins(uuid);
    int losses = stats.getLosses(uuid);
    int points = stats.getPoints(uuid);

    MySQL.query()
        .insertStats(
            uuid, kills, deaths, flags, cores, wools, monuments, playtime, points, wins, losses);
  }

  public StoreStatistics getStatsSummary() {
    return stats;
  }
}
