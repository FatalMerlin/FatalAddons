package com.fataldream.FatalAddons.fsmm.oc.environments;

import com.fataldream.FatalAddons.fsmm.util.TransferManager;
import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.AbstractManagedEnvironment;
import net.minecraftforge.fml.common.FMLCommonHandler;

/**
 * @author FatalMerlin (merlin.brandes@gmail.com)
 */
public class ATMEnvironment extends AbstractManagedEnvironment {
    /**
     * Creates a new environment instance and connects it to the network.
     * Allows the ATM Block to be connected via Adapter Blocks.
     */
    public ATMEnvironment() {
        setNode(Network.newNode(this, Visibility.Network).withComponent("fsmm_atm", Visibility.Network).create());
    }

    /**
     * Requests a transfer of funds from one player to another.
     *
     * @param fromPlayer the name of the player sending the funds
     * @param toPlayer the name of the player receiving the funds
     * @param amount the amount of funds to transfer
     * @return an array containing a boolean indicating success or failure, and a string message if applicable
     */
    @Callback(doc = "function(fromPlayer:string, toPlayer:string, amount:number):boolean, string|nil")
    public Object[] requestTransfer(Context context, Arguments args) {
        String fromPlayer = args.checkString(0);
        String toPlayer = args.checkString(1);
        double amount = args.checkDouble(2);

        if(!checkIfPlayerIsOnline(fromPlayer)){
            return new Object[]{false, "Sending Player not online: " + fromPlayer};
        }

        if (amount <= 0) {
            return new Object[]{false, "Invalid amount: " + amount};
        }

        // TODO: Check if players exist in the Server and / or have accounts
        // TODO: Intentionally do NOT check balance as that is done by the bank and could be used to exploit this command to check other players balance

        TransferManager.getInstance().requestTransfer(fromPlayer, toPlayer, amount, context);

        return new Object[] {true, null};
    }

    /**
     * Checks if a player is online.
     *
     * @param  player  the name of the player to check
     * @return         true if the player is online, false otherwise
     */
    private boolean checkIfPlayerIsOnline(String player){
        return FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(player) != null;
    }
}
