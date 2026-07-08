package dev.arcane.createcurios.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

public final class CuriosHelper {
    private static final String BACK_SLOT_ID = "back";

    public static List<ItemStack> getAllBackStacks(LivingEntity entity) {
        LazyOptional<ICuriosItemHandler> handlerOpt = CuriosApi.getCuriosHelper().getCuriosHandler(entity);
        if (!handlerOpt.isPresent()) return Collections.emptyList();

        ICuriosItemHandler handler = handlerOpt.orElse(null);
        if (handler == null) return Collections.emptyList();

        Optional<ICurioStacksHandler> backHandlerOpt = handler.getStacksHandler(BACK_SLOT_ID);
        if (!backHandlerOpt.isPresent()) return Collections.emptyList();

        ICurioStacksHandler backHandler = backHandlerOpt.get();
        int slots = backHandler.getSlots();
        List<ItemStack> result = new ArrayList<>(slots);

        for (int i = 0; i < slots; i++) {
            ItemStack stack = backHandler.getStacks().getStackInSlot(i);
            if (!stack.isEmpty()) {
                result.add(stack);
            }
        }
        return result;
    }

    public static Optional<ItemStack> getBackStack(LivingEntity entity) {
        List<ItemStack> all = getAllBackStacks(entity);
        return all.isEmpty() ? Optional.empty() : Optional.of(all.get(0));
    }

    public static boolean hasBacktankInCurios(LivingEntity entity) {
        return !getAllBackStacks(entity).isEmpty();
    }
}