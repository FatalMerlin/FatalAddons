package com.fataldream.FatalAddons.fsmm.util;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * @author FatalMerlin <merlin.brandes@gmail.com>
 */
public class TransferManagerTickHandler {
    private int tickCount = 0;

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        tickCount++;
        tickCount %= 20;

        if (tickCount != 0) {
            return;
        }

        TransferManager.getInstance().cleanupRequests();
    }
}
