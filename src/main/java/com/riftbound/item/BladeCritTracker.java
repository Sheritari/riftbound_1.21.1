package com.riftbound.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class BladeCritTracker {
    private static final Map<HitKey, Boolean> PENDING_CRITS = new ConcurrentHashMap<>();

    private BladeCritTracker() {
    }

    public static void markCrit(Player player, LivingEntity victim) {
        PENDING_CRITS.put(new HitKey(player.getUUID(), victim.getId()), Boolean.TRUE);
    }

    public static boolean consumeCrit(Player player, LivingEntity victim) {
        return Boolean.TRUE.equals(PENDING_CRITS.remove(new HitKey(player.getUUID(), victim.getId())));
    }

    private record HitKey(UUID playerId, int victimId) {
    }
}
