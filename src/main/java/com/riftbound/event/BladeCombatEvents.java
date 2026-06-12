package com.riftbound.event;

import com.riftbound.item.BladeCombatStats;
import com.riftbound.item.BladeCritTracker;
import com.riftbound.registry.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
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

        LivingEntity victim = event.getEntity();
        if (player.getRandom().nextFloat() < BladeCombatStats.CRIT_CHANCE) {
            BladeCritTracker.markCrit(player, victim);
        }

        float damage = BladeCombatStats.rollHitDamage(player.getRandom())
                + BladeCombatStats.getAffixDamageBonus(weapon);
        event.setAmount(damage);
    }

    @SubscribeEvent
    public static void onLivingDamagePre(LivingDamageEvent.Pre event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }

        DamageSource source = event.getSource();
        if (!source.is(DamageTypes.PLAYER_ATTACK) || !(source.getEntity() instanceof Player player)) {
            return;
        }

        if (!player.getMainHandItem().is(ModItems.SHARD_BLADE.get())) {
            return;
        }

        LivingEntity victim = event.getEntity();
        if (!BladeCritTracker.consumeCrit(player, victim)) {
            return;
        }

        event.setNewDamage(event.getNewDamage() * BladeCombatStats.CRIT_DAMAGE_MULTIPLIER);
        spawnVanillaCritParticles(victim);
    }

    private static void spawnVanillaCritParticles(LivingEntity victim) {
        if (!(victim.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        RandomSource random = victim.getRandom();
        double x = victim.getX();
        double y = victim.getY(0.5D);
        double z = victim.getZ();

        for (int i = 0; i < 7; ++i) {
            double offsetX = random.nextGaussian() * 0.02D;
            double offsetY = random.nextGaussian() * 0.02D;
            double offsetZ = random.nextGaussian() * 0.02D;
            serverLevel.sendParticles(
                    ParticleTypes.CRIT,
                    x + offsetX * 2.0D,
                    y + offsetY,
                    z + offsetZ * 2.0D,
                    1,
                    offsetX,
                    offsetY,
                    offsetZ,
                    0.1D
            );
        }
    }

    private static boolean isFullStrengthAttack(Player player) {
        return player.getAttackStrengthScale(0.0F) >= BladeCombatStats.FULL_ATTACK_STRENGTH_THRESHOLD;
    }
}
