package org.abyssdev.abyssafkzone.player;

import lombok.Data;

import java.util.UUID;

/**
 * The afk profile for the player.
 *
 * @author Relocation
 * @company Abyss Development LLC
 */
@Data
public final class AfkPlayer {

    private final UUID uuid;

    private long enteredAfk, lastReward;
    private boolean afk, up;

}