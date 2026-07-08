package dev.arcane.createcurios.compat;

import java.lang.reflect.Method;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class CreateReflection {
    private static final String NETHERITE_DIVING_BITS_KEY = "CreateNetheriteDivingBits";
    private static final String FIRE_IMMUNE_KEY = "CreateFireImmune";

    private static TagKey<Item> pressurizedAirSourcesTag;
    private static Class<?> backtankUtilClass;
    private static Class<?> divingHelmetItemClass;
    private static Method getAllWithAirMethod;
    private static boolean initialized = false;

    public static void init() {
        if (initialized) return;
        initialized = true;

        // 1.20.1 uses standard instantiation for ResourceLocation
        pressurizedAirSourcesTag = ItemTags.create(new ResourceLocation("create", "pressurized_air_sources"));

        try {
            backtankUtilClass = Class.forName("com.simibubi.create.content.equipment.armor.BacktankUtil");
            divingHelmetItemClass = Class.forName("com.simibubi.create.content.equipment.armor.DivingHelmetItem");
            getAllWithAirMethod = backtankUtilClass.getMethod("getAllWithAir", LivingEntity.class);
        } catch (Exception e) {
            throw new RuntimeException("Create classes not found, compat cannot initialize", e);
        }
    }

    public static boolean isNetheriteDivingHelmet(ItemStack stack) {
        return stack.getItem().getClass() == divingHelmetItemClass && hasFireResistant(stack);
    }

    public static boolean hasFireResistant(ItemStack stack) {
        // 1.20.1 relies on the Item property rather than a runtime DataComponent
        return stack.getItem() instanceof ArmorItem && stack.getItem().isFireResistant();
    }

    public static boolean isPressurizedAirSource(ItemStack stack) {
        return pressurizedAirSourcesTag != null && stack.is(pressurizedAirSourcesTag);
    }

    public static byte getBits(LivingEntity entity) {
        return entity.getPersistentData().getByte(NETHERITE_DIVING_BITS_KEY);
    }

    public static void setBits(LivingEntity entity, byte bits) {
        entity.getPersistentData().putByte(NETHERITE_DIVING_BITS_KEY, bits);
    }

    public static void setFireImmune(LivingEntity entity, boolean fireImmune) {
        entity.getPersistentData().putBoolean(FIRE_IMMUNE_KEY, fireImmune);
    }

    public static int getBacktankAir(ItemStack backtank) {
        try {
            Method getAir = backtankUtilClass.getMethod("getAir", ItemStack.class);
            return (Integer) getAir.invoke(null, backtank);
        } catch (Exception e) {
            return 0;
        }
    }

    @SuppressWarnings("unchecked")
    public static boolean hasFirBacktankWithAir(LivingEntity entity) {
        try {
            List<ItemStack> allWithAir = (List<ItemStack>) getAllWithAirMethod.invoke(null, entity);
            return allWithAir.stream().anyMatch(CreateReflection::hasFireResistant);
        } catch (Exception e) {
            return false;
        }
    }
}