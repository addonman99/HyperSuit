package com.voidjam.hypersuit;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

@EventBusSubscriber(modid = HyperSuit.MOD_ID)
public final class SlowMoData extends SavedData {
    private float targetTps = 10.0F;
    private boolean enabled = false;

    public static SlowMoData get(MinecraftServer server) {
        ServerLevel world = server.overworld();
        return world.getDataStorage().computeIfAbsent(
            new SavedData.Factory<>(SlowMoData::new, SlowMoData::load, null),
            "hypersuit_slowmo"
        );
    }

    public float getTargetTps() { return targetTps; }
    public boolean isEnabled() { return enabled; }

    public void setTargetTps(float tps) {
        targetTps = Math.max(1.0F, Math.min(20.0F, tps));
        setDirty();
    }

    public void toggle(MinecraftServer server) {
        enabled = !enabled;
        setDirty();
        apply(server);
    }

    public void apply(MinecraftServer server) {
        server.tickRateManager().setTickRate(enabled ? targetTps : 20.0F);
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        get(event.getServer()).apply(event.getServer());
    }

    public static SlowMoData load(CompoundTag tag, HolderLookup.Provider registries) {
        SlowMoData data = new SlowMoData();
        data.targetTps = Math.max(1.0F,
            Math.min(20.0F, tag.getFloat("target_tps")));
        data.enabled = tag.getBoolean("enabled");
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putFloat("target_tps", targetTps);
        tag.putBoolean("enabled", enabled);
        return tag;
    }
}
