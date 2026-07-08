package dev.arcane.createcurios;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// Be sure to adjust these imports if you placed the classes in different packages
import dev.arcane.createcurios.compat.CreateReflection;
import dev.arcane.createcurios.handler.DivingSuitHandler;

@Mod(Createcurios.MODID)
public class Createcurios {

    public static final String MODID = "createcurios";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Createcurios() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the custom handler to the Forge event bus so it listens to equipment and breathe events
        MinecraftForge.EVENT_BUS.register(new DivingSuitHandler());
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Initializing Create Curios Fire Compat Reflection...");

        // Initialize the reflection needed to read Create's diving suit logic safely
        CreateReflection.init();
    }
}