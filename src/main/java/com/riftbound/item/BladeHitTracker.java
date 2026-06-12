package com.riftbound.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class BladeHitTracker {
    private static final Map<HitKey, PendingHit> PENDING_HITS = new ConcurrentHashMap<>();

    private BladeHitTracker() {
    }

    public static void record(Player player, LivingEntity victim, PendingHit hit) {
        PENDING_HITS.put(new HitKey(player.getUUID(), victim.getId()), hit);
    }

    public static Optional<PendingHit> consume(Player player, LivingEntity victim) {
        return Optional.ofNullable(PENDING_HITS.remove(new HitKey(player.getUUID(), victim.getId())));
    }

    public record PendingHit(float fireDamage, boolean crit) {
    }

    private record HitKey(UUID playerId, int victimId) {
    }
}
