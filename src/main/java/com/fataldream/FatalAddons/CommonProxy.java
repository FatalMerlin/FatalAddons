package com.fataldream.FatalAddons;

import net.minecraft.item.Item;

/**
 * @author FatalMerlin <merlin.brandes@gmail.com>
 */
public class CommonProxy {
    public boolean isClient() {
        return false;
    }

    public void registerItemModel(Item item, int meta, String name) {

    }
}
