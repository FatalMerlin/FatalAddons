package com.fataldream.FatalAddons.fsmm;

import com.fataldream.FatalAddons.FatalAddons;
import com.fataldream.FatalAddons.fsmm.oc.drivers.ATMBlockDriver;
import com.fataldream.FatalAddons.fsmm.oc.drivers.ATMCardDriver;
import com.fataldream.FatalAddons.fsmm.util.CommandManager;
import com.fataldream.FatalAddons.fsmm.util.TransferManagerTickHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;

/**
 * @author FatalMerlin <merlin.brandes@gmail.com>
 */
public class FSMMAddon {
    public static void preInit() {
        if (Loader.isModLoaded("fsmm") && Loader.isModLoaded("opencomputers")) {
            FatalAddons.logger.info("Registering FSMM Integration...");
            ATMBlockDriver.register();
            ATMCardDriver.preInit();
            CommandManager.register();
            MinecraftForge.EVENT_BUS.register(new TransferManagerTickHandler());
            FatalAddons.logger.info("FSMM Integration registered.");
        }
    }

    public static void postInit() {
        if (Loader.isModLoaded("fsmm") && Loader.isModLoaded("opencomputers")) {
            ATMCardDriver.postInit();
        }
    }
}
