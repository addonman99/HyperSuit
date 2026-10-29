package com.voidjam.hypersuit;

import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

@Mod(HyperSuit.MOD_ID)
public final class HyperSuit {
    public static final String MOD_ID = "hypersuit";

    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS =
            DeferredRegister.create(Registries.ARMOR_MATERIAL, MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> HYPER_MATERIAL = ARMOR_MATERIALS.register(
            "hyper_suit",
            () -> new ArmorMaterial(
                    Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                        map.put(ArmorItem.Type.HELMET, 3);
                        map.put(ArmorItem.Type.CHESTPLATE, 8);
                        map.put(ArmorItem.Type.LEGGINGS, 6);
                        map.put(ArmorItem.Type.BOOTS, 3);
                    }),
                    18,
                    SoundEvents.ARMOR_EQUIP_NETHERITE,
                    () -> Ingredient.of(Items.NETHERITE_INGOT),
                    List.of(new ArmorMaterial.Layer(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(MOD_ID, "hyper_suit"))),
                    3.0F,
                    0.15F
            )
    );

    public static final Supplier<ArmorItem> HYPER_HELMET = ITEMS.register("hyper_helmet",
            () -> new ArmorItem(HYPER_MATERIAL, ArmorItem.Type.HELMET, new net.minecraft.world.item.Item.Properties()));
    public static final Supplier<ArmorItem> HYPER_CHESTPLATE = ITEMS.register("hyper_chestplate",
            () -> new ArmorItem(HYPER_MATERIAL, ArmorItem.Type.CHESTPLATE, new net.minecraft.world.item.Item.Properties()));
    public static final Supplier<ArmorItem> HYPER_LEGGINGS = ITEMS.register("hyper_leggings",
            () -> new ArmorItem(HYPER_MATERIAL, ArmorItem.Type.LEGGINGS, new net.minecraft.world.item.Item.Properties()));
    public static final Supplier<ArmorItem> HYPER_BOOTS = ITEMS.register("hyper_boots",
            () -> new ArmorItem(HYPER_MATERIAL, ArmorItem.Type.BOOTS, new net.minecraft.world.item.Item.Properties()));

    public static final Supplier<CreativeModeTab> HYPER_TAB = CREATIVE_TABS.register("hyper_suit",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.hypersuit"))
                    .icon(() -> new ItemStack(HYPER_CHESTPLATE.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(HYPER_HELMET.get());
                        output.accept(HYPER_CHESTPLATE.get());
                        output.accept(HYPER_LEGGINGS.get());
                        output.accept(HYPER_BOOTS.get());
                    })
                    .build());

    public HyperSuit(IEventBus modEventBus) {
        ARMOR_MATERIALS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_TABS.register(modEventBus);
        modEventBus.addListener(DashPayload::registerPayloads);
        modEventBus.addListener(SlowMoPayload::register);
    }
}
