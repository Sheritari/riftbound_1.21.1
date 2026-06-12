package com.riftbound.event;

import com.riftbound.item.BladeCombatStats;
import com.riftbound.registry.ModItems;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;

public final class BladeCombatEvents {
    private BladeCombatEvents() {
    }

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) {
            return;
        }
        if (!player.getMainHandItem().is(ModItems.SHARD_BLADE.get())) {
            return;
        }
        if (!isFullStrengthAttack(player)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onCriticalHit(CriticalHitEvent event) {
        if (event.getEntity().getMainHandItem().is(ModItems.SHARD_BLADE.get())) {
            event.setCriticalHit(false);
        }
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }

        DamageSource source = event.getSource();
        if (!source.is(DamageTypes.PLAYER_ATTACK) || !(source.getEntity() instanceof Player player)) {
            return;
        }

        ItemStack weapon = player.getMainHandItem();
        if (!weapon.is(ModItems.SHARD_BLADE.get())) {
            return;
        }

        if (!isFullStrengthAttack(player)) {
            event.setCanceled(true);
            return;
        }

        float damage = BladeCombatStats.rollHitDamage(player.getRandom())
                + BladeCombatStats.getAffixDamageBonus(weapon);
        event.setAmount(damage);
    }

    private static boolean isFullStrengthAttack(Player player) {
        return player.getAttackStrengthScale(0.0F) >= BladeCombatStats.FULL_ATTACK_STRENGTH_THRESHOLD;
    }
}
