package com.voidjam.hypersuit;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SlowMoPayload(int mode, float tps) implements CustomPacketPayload {
    public static final Type<SlowMoPayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath("hypersuit", "slowmo"));

    public static final StreamCodec<ByteBuf, SlowMoPayload> CODEC =
        StreamCodec.composite(ByteBufCodecs.VAR_INT, SlowMoPayload::mode,
            ByteBufCodecs.FLOAT, SlowMoPayload::tps, SlowMoPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToServer(TYPE, CODEC, SlowMoPayload::handle);
    }

    private static void handle(SlowMoPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;
            SlowMoData data = SlowMoData.get(player.getServer());

            if (payload.mode() == 0) {
                data.toggle(player.getServer());
            } else if (payload.mode() == 1) {
                data.setTargetTps(payload.tps());
                if (data.isEnabled()) data.apply(player.getServer());
            }
        });
    }
}
