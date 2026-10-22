package com.voidjam.hypersuit.client;

import com.voidjam.hypersuit.HyperSuit;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import java.io.IOException;

@EventBusSubscriber(
    modid = HyperSuit.MOD_ID,
    bus = EventBusSubscriber.Bus.MOD,
    value = Dist.CLIENT
)
public final class HyperSuitRenderer {
    private static ShaderInstance portalShader;

    private static final RenderType PORTAL_ARMOR = RenderType.create(
        "hypersuit_portal_armor",
        DefaultVertexFormat.NEW_ENTITY,
        com.mojang.blaze3d.vertex.VertexFormat.Mode.QUADS,
        256,
        false,
        true,
        RenderType.CompositeState.builder()
            .setShaderState(new RenderStateShard.ShaderStateShard(
                () -> portalShader
            ))
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .setCullState(RenderStateShard.NO_CULL)
            .setLightmapState(RenderStateShard.LIGHTMAP)
            .setOverlayState(RenderStateShard.OVERLAY)
            .createCompositeState(false)
    );

    private HyperSuitRenderer() {}

    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(
            new ShaderInstance(
                event.getResourceProvider(),
                ResourceLocation.fromNamespaceAndPath(
                    HyperSuit.MOD_ID, "hyper_portal"
                ),
                DefaultVertexFormat.NEW_ENTITY
            ),
            shader -> portalShader = shader
        );
    }

    @SubscribeEvent
    public static void addPlayerLayers(EntityRenderersEvent.AddLayers event) {
        for (String skin : event.getSkins()) {
            PlayerRenderer renderer = event.getSkin(skin);
            if (renderer != null) {
                renderer.addLayer(
                    new PortalArmorLayer(renderer, event.getContext().getModelSet())
                );
            }
        }
    }

    private static final class PortalArmorLayer
        extends RenderLayer<Player, PlayerModel<Player>> {

        private final HumanoidModel<Player> outerArmor;
        private final HumanoidModel<Player> innerArmor;

        PortalArmorLayer(
            RenderLayerParent<Player, PlayerModel<Player>> parent,
            EntityModelSet modelSet
        ) {
            super(parent);
            outerArmor = new HumanoidModel<>(
                modelSet.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)
            );
            innerArmor = new HumanoidModel<>(
                modelSet.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)
            );
        }

        @Override
        public void render(
            PoseStack poseStack,
            MultiBufferSource buffers,
            int packedLight,
            Player player,
            float limbSwing,
            float limbSwingAmount,
            float partialTick,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
        ) {
            if (portalShader == null) return;

            boolean helmet = is(player, EquipmentSlot.HEAD, HyperSuit.HYPER_HELMET.get());
            boolean chest = is(player, EquipmentSlot.CHEST, HyperSuit.HYPER_CHESTPLATE.get());
            boolean legs = is(player, EquipmentSlot.LEGS, HyperSuit.HYPER_LEGGINGS.get());
            boolean boots = is(player, EquipmentSlot.FEET, HyperSuit.HYPER_BOOTS.get());

            if (!helmet && !chest && !legs && !boots) return;

            portalShader.safeGetUniform("GameTime").set(
                (player.tickCount + partialTick) * 0.02F
            );

            getParentModel().copyPropertiesTo(outerArmor);
            outerArmor.setAllVisible(false);
            outerArmor.head.visible = helmet;
            outerArmor.hat.visible = helmet;
            outerArmor.body.visible = chest;
            outerArmor.rightArm.visible = chest;
            outerArmor.leftArm.visible = chest;
            outerArmor.rightLeg.visible = boots;
            outerArmor.leftLeg.visible = boots;

            VertexConsumer outerConsumer = buffers.getBuffer(PORTAL_ARMOR);
            outerArmor.renderToBuffer(
                poseStack, outerConsumer, packedLight,
                OverlayTexture.NO_OVERLAY, 0xFFFFFFFF
            );

            if (legs) {
                getParentModel().copyPropertiesTo(innerArmor);
                innerArmor.setAllVisible(false);
                innerArmor.body.visible = true;
                innerArmor.rightLeg.visible = true;
                innerArmor.leftLeg.visible = true;

                innerArmor.renderToBuffer(
                    poseStack, buffers.getBuffer(PORTAL_ARMOR),
                    packedLight, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF
                );
            }
        }

        private static boolean is(Player player, EquipmentSlot slot, net.minecraft.world.item.Item item) {
            ItemStack stack = player.getItemBySlot(slot);
            return stack.is(item);
        }
    }
}
