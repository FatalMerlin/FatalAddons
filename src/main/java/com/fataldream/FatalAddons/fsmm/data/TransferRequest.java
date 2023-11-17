package com.fataldream.FatalAddons.fsmm.data;

import com.fataldream.FatalAddons.fsmm.util.TransferManager;
import com.mojang.authlib.GameProfile;
import li.cil.oc.api.machine.Context;

import java.util.Date;
import java.util.UUID;

/**
 * Represents a transfer request made by a player.
 *
 * @author FatalMerlin (merlin.brandes@gmail.com)
 */
public class TransferRequest {
    private final UUID id = UUID.randomUUID();
    private final GameProfile fromPlayer;
    private final GameProfile toPlayer;
    private final double amount;
    private final Context context;

    private final String description;

    private final Date expiryDate = new Date(new Date().getTime() + TransferManager.REQUEST_EXPIRY_SECONDS * 1000);

    // TODO: change from player name to GameProfile with username AND uuid
    /**
     * Create a new transfer request.
     *
     * @param fromPlayer  the name of the player who made the transfer request
     * @param toPlayer    the name of the player who is receiving the transfer request
     * @param amount      the amount of the transfer request
     * @param context     the OpenComputers API context to send a signal detailing the transfer result
     */
    public TransferRequest(GameProfile fromPlayer, GameProfile toPlayer, double amount, Context context, String description) {

        this.fromPlayer = fromPlayer;
        this.toPlayer = toPlayer;
        this.amount = amount;
        this.context = context;
        this.description = description;
    }

    public GameProfile getFromPlayer() {
        return fromPlayer;
    }

    public GameProfile getToPlayer() {
        return toPlayer;
    }

    public double getAmount() {
        return amount;
    }

    public Context getContext() {
        return context;
    }

    public String getDescription() {
        return description;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public UUID getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.join("\n",
                "&bId&r: &a" + id,
                "&bTo&r: &a" + toPlayer.getName(),
                "&bAmount&r: &a" + amount + "&7F$",
                "&bDescription&r: &a" + description,
                "&bExpires in&r: &a" + ((expiryDate.getTime() - new Date().getTime()) / 1000) + " seconds"
        );
    }
}
