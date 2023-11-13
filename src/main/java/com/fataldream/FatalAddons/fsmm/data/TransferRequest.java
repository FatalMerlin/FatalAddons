package com.fataldream.FatalAddons.fsmm.data;

import li.cil.oc.api.machine.Context;

/**
 * Represents a transfer request made by a player.
 *
 * @author FatalMerlin (merlin.brandes@gmail.com)
 */
public class TransferRequest {
    private final String fromPlayer;
    private final String toPlayer;
    private final double amount;
    private final Context context;

    // TODO: change from player name to GameProfile with username AND uuid
    /**
     * Create a new transfer request.
     *
     * @param fromPlayer  the name of the player who made the transfer request
     * @param toPlayer    the name of the player who is receiving the transfer request
     * @param amount      the amount of the transfer request
     * @param context     the OpenComputers API context to send a signal detailing the transfer result
     */
    public TransferRequest(String fromPlayer, String toPlayer, double amount, Context context) {

        this.fromPlayer = fromPlayer;
        this.toPlayer = toPlayer;
        this.amount = amount;
        this.context = context;
    }

    public String getFromPlayer() {
        return fromPlayer;
    }

    public String getToPlayer() {
        return toPlayer;
    }

    public double getAmount() {
        return amount;
    }

    public Context getContext() {
        return context;
    }
}
