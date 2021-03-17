package network.atria.GUI.GUIes;

import static net.kyori.adventure.text.Component.text;

import app.ashcon.intake.Command;
import app.ashcon.intake.bukkit.parametric.annotation.Sender;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import network.atria.GUI.GUI;
import network.atria.Mixed;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class MainGUI extends GUI {

  public MainGUI() {
    super(27, text("Select Menu"));
    initializeItems();
  }

  public void initializeItems() {
    setItem(
        10,
        createIcon(
            text("Kill Effect").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD),
            Material.REDSTONE),
        player -> open(player, Mixed.get().getGUIManager().getKillEffectsGUI()));
    setItem(
        12,
        createIcon(
            text("Kill Sound").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD),
            Material.RECORD_4),
        player -> open(player, Mixed.get().getGUIManager().getSoundsGUI()));
    setItem(
        14,
        createIcon(
            text("Projectile Trail").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD),
            Material.BOW),
        player -> open(player, Mixed.get().getGUIManager().getProjectilesGUI()));
  }

  @Command(
      aliases = {"effect", "projectile", "sound"},
      desc = "Open effects select gui")
  public void openGUI(@Sender Player player) {
    open(player);
  }
}
