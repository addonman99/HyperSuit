package com.voidjam.hypersuit.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.voidjam.hypersuit.HyperSuit;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = HyperSuit.MOD_ID,
    bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class DashKeybinds {
    public static final KeyMapping DASH = new KeyMapping(
        "key.hypersuit.dash", InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_R, "key.categories.hypersuit");
    public static final KeyMapping SLOWMO = new KeyMapping(
        "key.hypersuit.slowmo", InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_F, "key.categories.hypersuit");

    private DashKeybinds() {}

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(DASH);
        event.register(SLOWMO);
    }
}
