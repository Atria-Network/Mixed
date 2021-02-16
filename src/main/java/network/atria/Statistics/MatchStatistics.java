package network.atria.Statistics;

import com.google.common.collect.Maps;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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

  public void addLoses(UUID uuid) {
    AtomicInteger loses = stats.getLoses().computeIfAbsent(uuid, x -> new AtomicInteger());
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

  public void updateStats(UUID uuid, String name, String table) {
    Connection connection = null;
    PreparedStatement statement = null;
    String baseSQL =
        "INSERT INTO {table} (UUID, KILLS, DEATHS, FLAGS, CORES, WOOLS, MONUMENTS, PLAYTIME, WINS, LOSES, POINTS) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE "
            + "KILLS = KILLS + VALUES(KILLS), DEATHS = DEATHS + VALUES(DEATHS), FLAGS = FLAGS + VALUES(FLAGS), CORES = CORES + VALUES(CORES), WOOLS = WOOLS + VALUES(WOOLS), MONUMENTS = MONUMENTS + VALUES(MONUMENTS), PLAYTIME = PLAYTIME + VALUES(PLAYTIME), WINS = WINS + VALUES(WINS), LOSES = LOSES + VALUES(LOSES), POINTS = POINTS + VALUES(POINTS)";
    String sql = baseSQL.replace("{table}", table);
    if (table.equalsIgnoreCase("WEEK_STATS")) {
      if (!MySQL.SQLQuery.playerExist_in_weekly_table(uuid)) {
        MySQL.SQLQuery.create_weekly_table(uuid, name);
      }
    }
    try {
      connection = MySQL.get().getHikari().getConnection();
      statement = connection.prepareStatement(sql);
      statement.setString(1, uuid.toString());
      statement.setInt(
          2, stats.getKills().get(uuid) != null ? stats.getKills().get(uuid).get() : 0);
      statement.setInt(
          3, stats.getDeaths().get(uuid) != null ? stats.getDeaths().get(uuid).get() : 0);
      statement.setInt(
          4, stats.getFlags().get(uuid) != null ? stats.getFlags().get(uuid).get() : 0);
      statement.setInt(
          5, stats.getCores().get(uuid) != null ? stats.getCores().get(uuid).get() : 0);
      statement.setInt(
          6, stats.getWools().get(uuid) != null ? stats.getWools().get(uuid).get() : 0);
      statement.setInt(
          7, stats.getMonuments().get(uuid) != null ? stats.getMonuments().get(uuid).get() : 0);
      statement.setInt(
          8, stats.getPlaytime().get(uuid) != null ? stats.getPlaytime().get(uuid).get() : 0);
      statement.setInt(
          9, stats.getPoints().get(uuid) != null ? stats.getPoints().get(uuid).get() : 0);
      statement.setInt(10, stats.getWins().get(uuid) != null ? stats.getWins().get(uuid).get() : 0);
      statement.setInt(
          11, stats.getLoses().get(uuid) != null ? stats.getLoses().get(uuid).get() : 0);
      statement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      MySQL.SQLQuery.closeConnection(connection);
      MySQL.SQLQuery.closeStatement(statement);
    }
  }

  public StoreStatistics getStatsSummary() {
    return stats;
  }
}
