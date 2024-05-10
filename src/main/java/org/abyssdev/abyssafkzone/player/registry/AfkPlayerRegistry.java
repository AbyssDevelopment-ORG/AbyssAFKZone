package org.abyssdev.abyssafkzone.player.registry;

import net.abyssdev.abysslib.patterns.registry.Registry;
import org.abyssdev.abyssafkzone.player.AfkPlayer;
import org.eclipse.collections.api.factory.Maps;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * The afk player registry implementation
 *
 * @author Relocation
 * @company Abyss Development LLC
 */
public final class AfkPlayerRegistry implements Registry<UUID, AfkPlayer> {
  private final Map<UUID, AfkPlayer> players = Maps.mutable.empty();
  
  @NotNull @Override
  public Optional<AfkPlayer> get(final @NotNull UUID key) {
    if (!this.players.containsKey(key)) {
      final AfkPlayer player = new AfkPlayer(key);
      this.players.put(key, player);

      return Optional.of(player);
    }

    return Optional.of(this.players.get(key));
  }

  @Override
  public Map<UUID, AfkPlayer> getRegistry() {
    return this.players;
  }
}