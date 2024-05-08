package org.abyssdev.abyssafkzone;

import java.util.UUID;

import lombok.Getter;
import net.abyssdev.abysslib.config.AbyssConfig;
import net.abyssdev.abysslib.patterns.registry.Registry;
import net.abyssdev.abysslib.plugin.AbyssPlugin;
import net.abyssdev.abysslib.runnable.AbyssTask;
import net.abyssdev.abysslib.text.MessageCache;
import net.abyssdev.abysslib.utils.RandomCollection;
import net.abyssdev.abysslib.utils.Region;
import org.abyssdev.abyssafkzone.player.AfkPlayer;
import org.abyssdev.abyssafkzone.player.registry.AfkPlayerRegistry;
import org.abyssdev.abyssafkzone.reward.AfkReward;
import org.abyssdev.abyssafkzone.task.AfkTask;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Main class of the plugin.
 *
 * @author Relocation
 * @company Abyss Development LLC
 */
@Getter
public final class AbyssAFKZone extends AbyssPlugin {

    private static AbyssAFKZone instance;

    private final Registry<UUID, AfkPlayer> playerRegistry = new AfkPlayerRegistry();

    private final AbyssConfig rewardsConfig = this.getAbyssConfig("rewards");
    private final AbyssConfig settingsConfig = this.getAbyssConfig("settings");
    private final AbyssConfig langConfig = this.getAbyssConfig("lang");

    private final MessageCache messageCache = new MessageCache((FileConfiguration)this.langConfig);

    private final Region afkZoneRegion = this.settingsConfig.getRegion("afk-zone-region");

    private final RandomCollection<AfkReward> rewardCollection = new RandomCollection<>();

    private final long rewardInterval = this.settingsConfig.getInt("reward-interval") * 1000L;
    private AbyssTask<AbyssAFKZone> afkTask;

    @Override
    public void onEnable() {
        instance = this;

        this.loadMessages(this.messageCache, this.langConfig);
        this.loadRewards();

        this.afkTask = new AfkTask(this);
    }

    @Override
    public void onDisable() {
        this.afkTask.cancel();
    }

    private void loadRewards() {
        this.rewardsConfig.getSectionKeys("rewards").forEach(path -> {
            AfkReward reward = new AfkReward(this.rewardsConfig, "rewards." + path);
            this.rewardCollection.add(reward.getChance(), reward);
        });
    }

    public static AbyssAFKZone get() {
        return instance;
    }

}
