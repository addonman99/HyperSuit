package com.voidjam.hypersuit.client;

import com.voidjam.hypersuit.DashPayload;
import com.voidjam.hypersuit.HyperSuit;
import com.voidjam.hypersuit.SlowMoPayload;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = HyperSuit.MOD_ID,
    bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class DashClientEvents {
    private static long slowMoDownAt = 0L;
    private static boolean editorOpened = false;
    private static boolean flightAnimationLocked = false;

    private DashClientEvents() {}

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            flightAnimationLocked = false;
            return;
        }

        while (DashKeybinds.DASH.consumeClick()) {
            PacketDistributor.sendToServer(new DashPayload());
            if (!mc.player.onGround()) {
                flightAnimationLocked = true;
                mc.player.startFallFlying();
            }
        }

        boolean down = DashKeybinds.SLOWMO.isDown();
        if (down && slowMoDownAt == 0L) {
            slowMoDownAt = System.currentTimeMillis();
            editorOpened = false;
        }
        if (down && !editorOpened
                && System.currentTimeMillis() - slowMoDownAt >= 1500L) {
            editorOpened = true;
            mc.setScreen(new SlowMoScreen());
        }
        if (!down && slowMoDownAt != 0L) {
            if (!editorOpened) {
                PacketDistributor.sendToServer(new SlowMoPayload(0, 0));
            }
            slowMoDownAt = 0L;
            editorOpened = false;
        }

        if (flightAnimationLocked) {
            if (mc.player.onGround() || mc.player.horizontalCollision) {
                flightAnimationLocked = false;
                mc.player.stopFallFlying();
            } else if (!mc.player.isFallFlying()) {
                mc.player.startFallFlying();
            }
        }
    }
}
