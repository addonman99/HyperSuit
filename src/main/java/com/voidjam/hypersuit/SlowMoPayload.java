package com.voidjam.hypersuit;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.fml.DistExecutor;
import net.neoforged.api.distmarker.Dist;

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
        var registrar = event.registrar("1");
        registrar.playToServer(TYPE, CODEC, SlowMoPayload::handle);
        registrar.playToClient(TYPE, CODEC, SlowMoPayload::handle);
    }

    private static void handle(SlowMoPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                SlowMoData data = SlowMoData.get(player.server);
                if (payload.mode() == 0) {
                    data.toggle(player.server);
                } else if (payload.mode() == 1) {
                    data.setTargetTps(payload.tps());
                    if (data.isEnabled()) data.apply(player.server);
                } else if (payload.mode() == 2) {
                    PacketDistributor.sendToPlayer(player,
                        new SlowMoPayload(3, data.getTargetTps()));
                }
            } else if (payload.mode() == 3) {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                    () -> () -> com.voidjam.hypersuit.client.SlowMoClientState
                        .setTps(payload.tps()));
            }
        });
    }
}
