package com.voidjam.hypersuit.client;

import com.voidjam.hypersuit.HyperSuit;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import java.util.Locale;

@EventBusSubscriber(modid = HyperSuit.MOD_ID,
    bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class HyperSuitHud {
    private static final double WARNING_SPEED = 7.1D;

    private HyperSuitHud() {}

    @SubscribeEvent
    public static void renderHud(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.font == null) return;

        if (!mc.player.getItemBySlot(EquipmentSlot.HEAD).is(HyperSuit.HYPER_HELMET.get())) {
            return;
        }

        Vec3 velocity = mc.player.getDeltaMovement();
        double speed = velocity.length() * 20.0D;
        String meter = String.format(Locale.ROOT, "SPEED  %.1f blocks/s", speed);
        var graphics = event.getGuiGraphics();

        graphics.drawString(mc.font, Component.literal(meter), 10, 10, 0xFFFFFFFF, true);

        if (speed > WARNING_SPEED) {
            String warning = "HIGH VELOCITY";
            int x = (mc.getWindow().getGuiScaledWidth() - mc.font.width(warning)) / 2;
            int y = Mth.floor(mc.getWindow().getGuiScaledHeight() * 0.40D);
            graphics.drawString(mc.font, warning, x, y, 0xFFFF3030, true);
        }
    }
}
