package org.abyssdev.abyssafkzone.task;

import net.abyssdev.abysslib.placeholder.PlaceholderReplacer;
import net.abyssdev.abysslib.runnable.AbyssTask;
import net.abyssdev.abysslib.scheduler.AbyssScheduler;
import net.abyssdev.abysslib.utils.Utils;
import org.abyssdev.abyssafkzone.AbyssAFKZone;
import org.abyssdev.abyssafkzone.player.AfkPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Optional;

/**
 * The afk zone task
 *
 * @author Relocation
 */
public final class AfkTask extends AbyssTask<AbyssAFKZone> {
    private static final PotionEffect LEVITATION_EFFECT = new PotionEffect(PotionEffectType.LEVITATION, 20, 3, false, false, false);

    private static final PotionEffect SLOW_FALLING_EFFECT = new PotionEffect(PotionEffectType.SLOW_FALLING, 21, 5, false, false, false);

    public AfkTask(final AbyssAFKZone plugin) {
        super(plugin, 20, true);
    }

    @Override
    public void run() {
        for (final Player player : Bukkit.getOnlinePlayers()) {
            final Optional<AfkPlayer> profileOpt = this.plugin.getPlayerRegistry().get(player.getUniqueId());

            if (!profileOpt.isPresent()) {
                continue;
            }

            final AfkPlayer profile = profileOpt.get();

            if (this.plugin.getAfkZoneRegion().isInside(player.getLocation()) && !profile.isAfk()) {
                continue;
            }

            if (!this.plugin.getAfkZoneRegion().isInside(player.getLocation())) {
                profile.setAfk(false);
                profile.setUp(false);
                profile.setEnteredAfk(-1L);
                profile.setLastReward(-1L);
                continue;
            }

            if (profile.isAfk()) {
                this.plugin.getMessageCache().sendMessage(player, "messages.time-message", new PlaceholderReplacer()
                        .addPlaceholder("%time%", Utils.getTimeFormat(System.currentTimeMillis() - profile.getEnteredAfk())));

                AbyssScheduler.sync().run(() -> {
                    if (profile.isUp()) {
                        player.addPotionEffect(LEVITATION_EFFECT);
                        profile.setUp(false);
                        return;
                    }

                    player.addPotionEffect(SLOW_FALLING_EFFECT);
                    profile.setUp(true);
                });

                if (profile.getLastReward() + this.plugin.getRewardInterval() > System.currentTimeMillis()) {
                    continue;
                }

                profile.setLastReward(System.currentTimeMillis());
                this.plugin.getRewardCollection().next().execute(player);
                continue;
            }

            this.plugin.getMessageCache().sendMessage(player, "messages.entered-zone");

            AbyssScheduler.sync().run(() -> {
                if (profile.isUp()) {
                    player.removePotionEffect(PotionEffectType.LEVITATION);
                    return;
                }

                player.removePotionEffect(PotionEffectType.SLOW_FALLING);
            });

            profile.setAfk(true);
            profile.setUp(true);
            profile.setEnteredAfk(System.currentTimeMillis());
            profile.setLastReward(System.currentTimeMillis());
        }
    }

}