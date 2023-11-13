package com.fataldream.FatalAddons.fsmm;

import com.fataldream.FatalAddons.fsmm.oc.drivers.ATMDriver;
import com.fataldream.FatalAddons.fsmm.util.CommandManager;
import net.minecraftforge.fml.common.Loader;

public class FSMMAddon {
    public static void register() {
        if (Loader.isModLoaded("fsmm") && Loader.isModLoaded("opencomputers")) {
            ATMDriver.register();
            CommandManager.register();
        }
    }
}
