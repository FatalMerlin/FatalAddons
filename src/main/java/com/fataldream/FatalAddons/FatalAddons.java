package com.fataldream.FatalAddons;

import com.fataldream.FatalAddons.fsmm.FSMMAddon;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = FatalAddons.MODID, name = FatalAddons.NAME, version = FatalAddons.VERSION)
public class FatalAddons {
    public static final String MODID = "fataladdons";
    public static final String NAME = "FatalAddons";
    public static final String VERSION = "1.0";

    private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        FSMMAddon.register();
    }
}
