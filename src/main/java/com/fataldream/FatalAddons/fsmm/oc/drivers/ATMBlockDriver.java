package com.fataldream.FatalAddons.fsmm.oc.drivers;

import com.fataldream.FatalAddons.fsmm.oc.environments.ATMEnvironment;
import li.cil.oc.api.Driver;
import li.cil.oc.api.driver.DriverBlock;
import li.cil.oc.api.network.ManagedEnvironment;
import net.fexcraft.mod.fsmm.blocks.ATM;
import net.minecraft.block.Block;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author FatalMerlin (merlin.brandes@gmail.com)
 */
public class ATMBlockDriver implements DriverBlock {
    /**
     * Determines if the given block at the specified position in the specified world
     * works with the ATM driver.
     *
     * @param  world       the world in which the block is located
     * @param  blockPos    the position of the block
     * @param  enumFacing  the facing direction of the block
     * @return             true if the block is an instance of ATM, false otherwise
     */
    @Override
    public boolean worksWith(World world, BlockPos blockPos, EnumFacing enumFacing) {
        Block block = world.getBlockState(blockPos).getBlock();

        return block instanceof ATM;
    }

    /**
     * Creates a new ManagedEnvironment object for the given world, block position, and facing direction.
     *
     * @param  world       the world in which the environment is created
     * @param  blockPos    the position of the block in the world
     * @param  enumFacing  the facing direction of the environment
     * @return             a new ManagedEnvironment object
     */
    @Override
    public ManagedEnvironment createEnvironment(World world, BlockPos blockPos, EnumFacing enumFacing) {
        return new ATMEnvironment();
    }

    /**
     * Registers the ATMDriver with the Driver class.
     */
    public static void register() {
        Driver.add(new ATMBlockDriver());
    }
}
