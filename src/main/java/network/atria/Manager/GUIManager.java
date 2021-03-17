package network.atria.Manager;

import com.google.common.collect.Sets;
import java.util.Optional;
import java.util.Set;
import network.atria.GUI.GUI;
import network.atria.GUI.GUIes.KillEffectsGUI;
import network.atria.GUI.GUIes.MainGUI;
import network.atria.GUI.GUIes.ProjectilesGUI;
import network.atria.GUI.GUIes.SoundsGUI;

public class GUIManager {

  private final KillEffectsGUI killEffectsGUI;
  private final SoundsGUI soundsGUI;
  private final ProjectilesGUI projectilesGUI;
  private final MainGUI mainGUI;
  private final Set<GUI> guies;

  public GUIManager() {
    this.killEffectsGUI = new KillEffectsGUI();
    this.soundsGUI = new SoundsGUI();
    this.projectilesGUI = new ProjectilesGUI();
    this.mainGUI = new MainGUI();
    this.guies = Sets.newHashSet(killEffectsGUI, soundsGUI, projectilesGUI, mainGUI);
  }

  public Optional<GUI> findGUI(String name) {
    return this.guies.stream().filter(x -> x.getGUI().getTitle().equals(name)).findFirst();
  }

  public KillEffectsGUI getKillEffectsGUI() {
    return killEffectsGUI;
  }

  public SoundsGUI getSoundsGUI() {
    return soundsGUI;
  }

  public ProjectilesGUI getProjectilesGUI() {
    return projectilesGUI;
  }

  public MainGUI getMainGUI() {
    return mainGUI;
  }
}
