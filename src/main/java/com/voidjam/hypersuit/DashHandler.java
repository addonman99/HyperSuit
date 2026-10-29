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

@EventBusSubscriber(modid = HyperSuit.MOD_ID)
public final class DashHandler {
    private static final double SPEED = 2.5D;
    private static final int DURATION = 5;
    private static final Map<UUID, DashState> ACTIVE = new HashMap<>();

    private DashHandler() {}

    public static void startDash(ServerPlayer player) {
        Vec3 direction = player.getLookAngle().normalize();
        ACTIVE.put(player.getUUID(), new DashState(direction, DURATION));
        player.setDeltaMovement(direction.scale(SPEED));
        player.hurtMarked = true;
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        DashState dash = ACTIVE.get(player.getUUID());
        if (dash == null) return;

        if (player.horizontalCollision || player.verticalCollision) {
            if (player.level() instanceof ServerLevel level) {
                level.sendParticles(ParticleTypes.POOF,
                    player.getX(), player.getY() + player.getBbHeight() / 2.0,
                    player.getZ(), 10, 0.22, 0.22, 0.22, 0.04);
            }
            ACTIVE.remove(player.getUUID());
            player.setDeltaMovement(Vec3.ZERO);
            player.hurtMarked = true;
            return;
        }

        player.setDeltaMovement(dash.direction.scale(SPEED));
        player.hurtMarked = true;

        if (--dash.remainingTicks <= 0) {
            ACTIVE.remove(serverPlayer.getUUID());
        }
    }

    private static final class DashState {
        private final Vec3 direction;
        private int remainingTicks;

        private DashState(Vec3 direction, int remainingTicks) {
            this.direction = direction;
            this.remainingTicks = remainingTicks;
        }
    }
}
