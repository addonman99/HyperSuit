package com.voidjam.hypersuit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;

@EventBusSubscriber(modid = HyperSuit.MOD_ID)
public final class DashHandler {
    private static final double DASH_SPEED = 1.0D;
    private static final int DASH_TICKS = 4;
    private static final Map<UUID, DashState> ACTIVE = new HashMap<>();
    private static final Map<UUID, Double> SLIDING = new HashMap<>();
    private static final Map<UUID, Boolean> FALL_SAFE = new HashMap<>();

    private DashHandler() {}

    public static void startDash(ServerPlayer player) {
        Vec3 direction = player.getLookAngle().normalize();
        ACTIVE.put(player.getUUID(), new DashState(direction, DASH_TICKS));
        FALL_SAFE.put(player.getUUID(), true);
        player.setDeltaMovement(direction.scale(DASH_SPEED));
        if (!player.onGround()) player.startFallFlying();
        player.hurtMarked = true;
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (!(player instanceof ServerPlayer sp)) return;
        UUID id = sp.getUUID();

        if (sp.onGround()) {
            ACTIVE.remove(id);
            SLIDING.remove(id);
            FALL_SAFE.remove(id);
            return;
        }

        if (SLIDING.containsKey(id)) {
            if (sp.horizontalCollision) {
                double speed = Math.min(0.38D, SLIDING.get(id) + 0.035D);
                SLIDING.put(id, speed);
                sp.setDeltaMovement(0.0D, -speed, 0.0D);
                sp.hurtMarked = true;
            } else {
                SLIDING.remove(id);
            }
            return;
        }

        DashState dash = ACTIVE.get(id);
        if (dash == null) return;

        if (sp.horizontalCollision) {
            ACTIVE.remove(id);
            SLIDING.put(id, 0.07D);
            if (sp.level() instanceof ServerLevel level) {
                level.sendParticles(ParticleTypes.POOF,
                    sp.getX(), sp.getY() + sp.getBbHeight() / 2.0, sp.getZ(),
                    8, 0.2, 0.2, 0.2, 0.025);
            }
            sp.setDeltaMovement(0.0D, -0.07D, 0.0D);
            sp.hurtMarked = true;
            return;
        }

        sp.setDeltaMovement(dash.direction.scale(DASH_SPEED));
        if (!sp.onGround()) sp.startFallFlying();
        sp.hurtMarked = true;
        if (--dash.remainingTicks <= 0) ACTIVE.remove(id);
    }

    @SubscribeEvent
    public static void preventDashFallDamage(LivingFallEvent event) {
        if (event.getEntity() instanceof Player player
                && FALL_SAFE.containsKey(player.getUUID())) {
            event.setCanceled(true);
        }
    }

    private static final class DashState {
        final Vec3 direction;
        int remainingTicks;
        DashState(Vec3 direction, int remainingTicks) {
            this.direction = direction;
            this.remainingTicks = remainingTicks;
        }
    }
}
