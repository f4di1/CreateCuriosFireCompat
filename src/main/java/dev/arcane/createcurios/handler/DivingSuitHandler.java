package dev.arcane.createcurios.handler;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingBreatheEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import dev.arcane.createcurios.compat.CreateReflection;
import dev.arcane.createcurios.helper.CuriosHelper;
import top.theillusivec4.curios.api.event.CurioChangeEvent;

public final class DivingSuitHandler {

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onLivingEquipmentChange(LivingEquipmentChangeEvent event) {
        if (event.getSlot().getType() != EquipmentSlot.Type.ARMOR) return;
        recalculate(event.getEntity());
    }

    @SubscribeEvent
    public void onCurioChange(CurioChangeEvent event) {
        if (!"back".equals(event.getIdentifier())) return;
        recalculate(event.getEntity());
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onLivingBreathe(LivingBreatheEvent event) {
        LivingEntity entity = event.getEntity();
        if (!entity.level().isClientSide()) return;
        if (!entity.isInLava()) return;

        int totalAir = 0;
        for (ItemStack backStack : CuriosHelper.getAllBackStacks(entity)) {
            if (CreateReflection.isPressurizedAirSource(backStack)) {
                totalAir += CreateReflection.getBacktankAir(backStack);
            }
        }
        if (totalAir > 0) {
            entity.getPersistentData().putInt("VisualBacktankAir", totalAir);
        }
    }

    private void recalculate(LivingEntity entity) {
        byte bits = 0;
        ItemStack head = entity.getItemBySlot(EquipmentSlot.HEAD);
        if (CreateReflection.isNetheriteDivingHelmet(head)) {
            bits |= (1 << EquipmentSlot.HEAD.getIndex());
        }

        ItemStack chest = entity.getItemBySlot(EquipmentSlot.CHEST);
        // The Netherite Exoskeleton evaluates to true here
        if (chest.getItem() instanceof ArmorItem && chest.getItem().isFireResistant()) {
            boolean hasAir = CreateReflection.isPressurizedAirSource(chest) ||
                    CuriosHelper.getAllBackStacks(entity).stream().anyMatch(CreateReflection::isPressurizedAirSource);
            if (hasAir) {
                bits |= (1 << EquipmentSlot.CHEST.getIndex());
            }
        }

        ItemStack legs = entity.getItemBySlot(EquipmentSlot.LEGS);
        if (CreateReflection.hasFireResistant(legs)) {
            bits |= (1 << EquipmentSlot.LEGS.getIndex());
        }

        ItemStack feet = entity.getItemBySlot(EquipmentSlot.FEET);
        if (CreateReflection.hasFireResistant(feet)) {
            bits |= (1 << EquipmentSlot.FEET.getIndex());
        }

        byte oldBits = CreateReflection.getBits(entity);
        CreateReflection.setBits(entity, bits);
        boolean wasFull = ((oldBits & 0xF) == 15);
        boolean isFull = ((bits & 0xF) == 15);

        if (isFull && !wasFull) {
            CreateReflection.setFireImmune(entity, true);
        } else if (wasFull && !isFull) {
            CreateReflection.setFireImmune(entity, false);
        }
    }
}