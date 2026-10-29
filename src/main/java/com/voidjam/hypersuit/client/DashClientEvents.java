package com.voidjam.hypersuit.client;

import com.voidjam.hypersuit.DashPayload;
import com.voidjam.hypersuit.HyperSuit;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(
    modid = HyperSuit.MOD_ID,
    bus = EventBusSubscriber.Bus.GAME,
    value = Dist.CLIENT
)
public final class DashClientEvents {
    private DashClientEvents() {}

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        while (DashKeybinds.DASH.consumeClick()) {
            PacketDistributor.sendToServer(new DashPayload());
        }
    }
}
