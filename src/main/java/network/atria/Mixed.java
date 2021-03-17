package network.atria;

import com.github.fierioziy.particlenativeapi.api.ParticleNativeAPI;
import com.github.fierioziy.particlenativeapi.api.Particles_1_8;
import com.github.fierioziy.particlenativeapi.plugin.ParticleNativePlugin;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.PermissionNode;
import network.atria.Commands.graph.CommandExecutor;
import network.atria.Commands.graph.CommandGraph;
import network.atria.Effects.Particles.KillEffects;
import network.atria.Effects.Particles.ProjectileTrails;
import network.atria.Effects.Sounds.KillSounds;
import network.atria.GUI.GUI;
import network.atria.GUI.GUIes.KillEffectsGUI;
import network.atria.GUI.GUIes.MainGUI;
import network.atria.GUI.GUIes.ProjectilesGUI;
import network.atria.GUI.GUIes.SoundsGUI;
import network.atria.Manager.EffectManager;
import network.atria.Manager.GUIManager;
import network.atria.Manager.RankManager;
import network.atria.Manager.UserProfileManager;
import network.atria.Statistics.MatchEvents;
import network.atria.Statistics.MatchStatistics;
import network.atria.Util.KillEffectsConfig;
import network.atria.Util.RanksConfig;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Mixed extends JavaPlugin implements Listener {

  private static Mixed instance;
  private long uptime;
  private MatchStatistics statistics;
  private BukkitAudiences audiences;
  private RankManager rankManager;
  private UserProfileManager profileManager;
  private EffectManager effectManager;
  private GUIManager guiManager;
  private final FileConfiguration config = getConfig();

  @Override
  public void onEnable() {
    instance = this;
    new RanksConfig("ranks.yml");
    new KillEffectsConfig("effects.yml");

    config.options().copyDefaults();
    saveDefaultConfig();

    MySQL database = new MySQL();
    database.connect();
    MySQL.query().createTables();
    registerCommands();
    registerEvents();
    registerTranslator();
    this.audiences = BukkitAudiences.create(this);
    this.uptime = System.currentTimeMillis();
    this.rankManager = new RankManager();
    this.effectManager = new EffectManager();
    this.profileManager = new UserProfileManager();
    this.statistics = new MatchStatistics();
    this.guiManager = new GUIManager();
    createGUIs();
    super.onEnable();
  }

  @Override
  public void onDisable() {
    if (!profileManager.getProfiles().isEmpty()) {
      profileManager.getProfiles().stream()
          .filter(Objects::nonNull)
          .forEach(profile -> MySQL.query().updateProfile(profile));
    }

    if (MySQL.get().getHikari() != null) {
      MySQL.get().getHikari().close();
    }
    super.onDisable();
  }

  private void registerTranslator() {
    TranslationRegistry registry = TranslationRegistry.create(Key.key("message"));
    registry.registerAll(Locale.US, ResourceBundle.getBundle("message"), true);
    GlobalTranslator.get().addSource(registry);
  }

  private void registerEvents() {
    PluginManager pm = Bukkit.getServer().getPluginManager();

    pm.registerEvents(this, this);
    pm.registerEvents(new UserProfileManager(), this);
    pm.registerEvents(new GUI.GUIListener(), this);
    new MatchEvents(this);
    new KillEffects(this);
    new KillSounds(this);
    new ProjectileTrails(this);
  }

  private void registerCommands() {
    CommandGraph graph = new CommandGraph();
    new CommandExecutor(this, graph).register();
  }

  private void createGUIs() {
    new KillEffectsGUI();
    new MainGUI();
    new ProjectilesGUI();
    new SoundsGUI();
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onJoin(AsyncPlayerPreLoginEvent event) {
    LuckPerms api = LuckPermsProvider.get();
    User user = api.getUserManager().getUser(event.getUniqueId());
    PermissionNode node = PermissionNode.builder("pgm.group.wood_iii").build();

    if (user != null) {
      user.data().add(node);
      api.getUserManager().saveUser(user);
    }
    profileManager.addProfile(
        event.getUniqueId(), MySQL.query().registerPlayer(event.getName(), event.getUniqueId()));
  }

  public long getUptime() {
    return this.uptime;
  }

  public static Mixed get() {
    return instance;
  }

  public BukkitAudiences getAudience() {
    return this.audiences;
  }

  public Particles_1_8 getParticles() {
    ParticleNativeAPI api = ParticleNativePlugin.getAPI();
    return api.getParticles_1_8();
  }

  public MatchStatistics getStatistics() {
    return this.statistics;
  }

  public RankManager getRankManager() {
    return this.rankManager;
  }

  public UserProfileManager getProfileManager() {
    return this.profileManager;
  }

  public EffectManager getEffectManager() {
    return this.effectManager;
  }

  public GUIManager getGUIManager() {
    return this.guiManager;
  }
}
