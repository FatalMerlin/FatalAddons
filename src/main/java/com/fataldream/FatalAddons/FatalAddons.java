package com.fataldream.FatalAddons;

import com.fataldream.FatalAddons.fsmm.FSMMAddon;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author FatalMerlin <merlin.brandes@gmail.com>
 */
@Mod(modid = FatalAddons.MODID, name = FatalAddons.NAME, version = FatalAddons.VERSION)
public class FatalAddons {
    public static final String MODID = "fataladdons";
    public static final String NAME = "FatalAddons";
    public static final String VERSION = "1.0";

    @SidedProxy(clientSide = "com.fataldream.FatalAddons.ClientProxy", serverSide = "com.fataldream.FatalAddons.CommonProxy")
    public static CommonProxy proxy;

    // TODO: Add logging
    public static Logger logger = LogManager.getLogger(NAME);

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger.info("Initializing...");
        FSMMAddon.preInit();
        logger.info("Initialization complete.");
    }

//    @EventHandler
//    public void init(FMLInitializationEvent event) {
//        logger.info("Initializing...");
//        FSMMAddon.init();
//        logger.info("Initialization complete.");
//    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        FSMMAddon.postInit();
    }

    public static void registerItem(Item item, String name) {
        GameRegistry.findRegistry(Item.class).register(item.setRegistryName(new ResourceLocation(MODID, name)));
    }
}
