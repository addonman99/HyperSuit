package com.voidjam.hypersuit;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public record DashPayload() implements CustomPacketPayload {
    public static final Type<DashPayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(
            HyperSuit.MOD_ID, "dash"));

    public static final StreamCodec<ByteBuf, DashPayload> STREAM_CODEC =
        StreamCodec.unit(new DashPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToServer(TYPE, STREAM_CODEC,
            DashPayload::handle);
    }

    private static void handle(DashPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                DashHandler.startDash(player);
            }
        });
    }
}
