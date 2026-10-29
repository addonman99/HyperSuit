package com.voidjam.hypersuit;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;

@EventBusSubscriber(modid = HyperSuit.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class HyperSuitDamageHandler {
    private HyperSuitDamageHandler() {}

    @SubscribeEvent
    public static void applyFullSetDamageReduction(LivingDamageEvent.Pre event) {
        LivingEntity entity = event.getEntity();
        boolean fullSet =
                entity.getItemBySlot(EquipmentSlot.HEAD).is(HyperSuit.HYPER_HELMET.get())
                && entity.getItemBySlot(EquipmentSlot.CHEST).is(HyperSuit.HYPER_CHESTPLATE.get())
                && entity.getItemBySlot(EquipmentSlot.LEGS).is(HyperSuit.HYPER_LEGGINGS.get())
                && entity.getItemBySlot(EquipmentSlot.FEET).is(HyperSuit.HYPER_BOOTS.get());

        if (fullSet) {
            event.setNewDamage(event.getNewDamage() * 0.25F);
        }
    }

    @SubscribeEvent
    public static void leggingsPreventFallDamage(LivingFallEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.getItemBySlot(EquipmentSlot.LEGS).is(HyperSuit.HYPER_LEGGINGS.get())) {
            event.setCanceled(true);
        }
    }
}
