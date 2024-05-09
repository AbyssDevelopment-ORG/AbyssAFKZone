package org.abyssdev.abyssafkzone.reward;

import lombok.Getter;
import net.abyssdev.abysslib.config.AbyssConfig;
import net.abyssdev.abysslib.scheduler.AbyssScheduler;
import net.abyssdev.abysslib.text.message.Message;
import net.abyssdev.abysslib.utils.PlayerUtils;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Represents an AFK reward.
 *
 * @author Relocation
 * @company Abyss Development LLC
 */
@Getter
public final class AfkReward {

  private final Message message;
  private final List<String> commands;

  private final double chance;


  /**
   * Constructs a new AfkReward instance.
   *
   * @param config The configuration file.
   * @param path   The path to the reward.
   */
  public AfkReward(final AbyssConfig config, final String path) {
    this.message = new Message(config, path + ".message");
    this.commands = config.getStringList(path + ".commands");
    this.chance = config.getDouble(path + ".chance");
  }

    /**
     * Executes the reward commands
     *
     * @param player The player.
     */
  public void execute(Player player) {
    this.message.send(player);
    AbyssScheduler.sync().run(() -> PlayerUtils.dispatchCommands(player, this.commands));
  }

}