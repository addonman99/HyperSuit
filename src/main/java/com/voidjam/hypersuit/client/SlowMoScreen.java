package com.voidjam.hypersuit.client;

import com.voidjam.hypersuit.SlowMoPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public final class SlowMoScreen extends Screen {
    private EditBox input;

    public SlowMoScreen() {
        super(Component.literal("Slow Motion TPS"));
    }

    @Override
    protected void init() {
        input = new EditBox(font, width / 2 - 45, height / 2 - 5,
            90, 22, Component.literal("TPS"));
        input.setValue(Integer.toString(Math.round(SlowMoClientState.getTps())));
        input.setFilter(s -> s.matches("\\d{0,2}"));
        addRenderableWidget(input);
        setInitialFocus(input);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        renderBackground(g, mouseX, mouseY, partialTick);
        g.drawCenteredString(font, Component.literal("SLOW MOTION TPS"),
            width / 2, height / 2 - 35, 0xFFFF2222);
        g.drawCenteredString(font, Component.literal("1-20 TPS | Enter or Esc to save"),
            width / 2, height / 2 + 25, 0xFFAAAAAA);
        super.render(g, mouseX, mouseY, partialTick);
    }

    private void saveAndClose() {
        try {
            int value = Integer.parseInt(input.getValue());
            if (value >= 1 && value <= 20) {
                PacketDistributor.sendToServer(new SlowMoPayload(1, value));
                SlowMoClientState.setTps(value);
            }
        } catch (NumberFormatException ignored) {}
        onClose();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 257 || keyCode == 256) {
            saveAndClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        minecraft.setScreen(null);
    }
}
