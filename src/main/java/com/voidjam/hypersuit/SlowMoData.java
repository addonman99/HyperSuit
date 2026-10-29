package com.voidjam.hypersuit;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

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

    public float getTargetTps() {
        return targetTps;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setTargetTps(float tps) {
        targetTps = Math.max(1.0F, Math.min(20.0F, tps));
        setDirty();
    }

    public void toggle(MinecraftServer server) {
        enabled = !enabled;
        apply(server);
    }

    public void apply(MinecraftServer server) {
        server.tickRateManager().setTickRate(enabled ? targetTps : 20.0F);
    }

    public static SlowMoData load(CompoundTag tag, HolderLookup.Provider registries) {
        SlowMoData data = new SlowMoData();
        data.targetTps = Math.max(1.0F, Math.min(20.0F,
            tag.getFloat("target_tps")));
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putFloat("target_tps", targetTps);
        return tag;
    }
}
