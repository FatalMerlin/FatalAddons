package com.fataldream.FatalAddons.fsmm.oc.environments;

import com.fataldream.FatalAddons.fsmm.util.TransferManager;
import com.fataldream.FatalAddons.util.PlayerUtils;
import com.mojang.authlib.GameProfile;
import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.AbstractManagedEnvironment;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.UUID;

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
     * @param description (optional) the description for the transfer request
     * @return the ID of the transfer request if successful, otherwise nil and the error message
     */
    @Callback(doc = "function(fromPlayer:string, toPlayer:string, amount:number, description?:string):error|nil, nil|string")
    public Object[] requestTransfer(Context context, Arguments args) {
        String fromPlayerName = args.checkString(0);
        String toPlayerName = args.checkString(1);
        double amount = args.checkDouble(2);
        String description = args.optString(3, null);

        GameProfile fromPlayer = PlayerUtils.getGameProfile(fromPlayerName);
        GameProfile toPlayer = PlayerUtils.getGameProfile(toPlayerName);

        if (fromPlayer == null) {
            return new Object[]{"Sending Player not found: " + fromPlayerName, null};
        }

        if (toPlayer == null) {
            return new Object[]{"Receiving Player not found: " + toPlayerName, null};
        }

        if (!PlayerUtils.isOnline(fromPlayerName)) {
            return new Object[]{"Sending Player not online: " + fromPlayerName, null};
        }

        if (amount <= 0) {
            return new Object[]{"Invalid amount: " + amount, null};
        }

        // TODO: Check if players exist in the Server and / or have accounts
        // TODO: Intentionally do NOT check balance as that is done by the bank and could be used to exploit this command to check other players balance

        UUID transactionId = TransferManager.getInstance().requestTransfer(fromPlayer, toPlayer, amount, context, description);

        return new Object[]{null, transactionId.toString()};
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
