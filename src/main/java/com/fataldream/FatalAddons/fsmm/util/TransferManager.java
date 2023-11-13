package com.fataldream.FatalAddons.fsmm.util;

import com.fataldream.FatalAddons.fsmm.data.TransferRequest;
import com.mojang.authlib.GameProfile;
import li.cil.oc.api.machine.Context;
import net.fexcraft.mod.fsmm.data.Account;
import net.fexcraft.mod.fsmm.data.Bank;
import net.fexcraft.mod.fsmm.util.Config;
import net.fexcraft.mod.fsmm.util.DataManager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author FatalMerlin (merlin.brandes@gmail.com)
 */
public class TransferManager {
    private static TransferManager instance;
    private final HashMap<String, TransferRequest> requests = new HashMap<>();
    // TODO: Remove transfers after 60 seconds

    private TransferManager() {
    }

    public static TransferManager getInstance() {
        if (instance == null) {
            instance = new TransferManager();
        }
        return instance;
    }

    /**
     * Requests a transfer from one player to another, and stores the transfer for use with the
     * /fsmm accept and /fsmm reject commands.
     * Also notifies the player that a transfer request has been sent.
     *
     * @param fromPlayer the name of the player sending the transfer
     * @param toPlayer   the name of the player receiving the transfer
     * @param amount     the amount of the transfer
     * @param context    the context of the transfer
     */
    public void requestTransfer(String fromPlayer, String toPlayer, double amount, Context context) {
        requests.put(fromPlayer, new TransferRequest(fromPlayer, toPlayer, amount, context));

        EntityPlayerMP player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(fromPlayer);

        if (player != null) {
            player.sendMessage(new TextComponentString("=== \u00a7bNew transfer request\u00a7r ==="));
            player.sendMessage(new TextComponentString("\u00a7bTo\u00a7r: \u00a7a" + toPlayer));
            player.sendMessage(new TextComponentString("\u00a7bAmount\u00a7r: \u00a7a" + amount + "\u00a77F$"));
            player.sendMessage(new TextComponentString("Use ")
                    .appendSibling(new TextComponentString("/fsmm transfer accept").setStyle(
                            new Style()
                                    .setColor(TextFormatting.GREEN)
                                    .setUnderlined(true)
                                    .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fsmm transfer accept"))))
                    .appendSibling(new TextComponentString(" to accept it or "))
                    .appendSibling(new TextComponentString("/fsmm transfer reject").setStyle(
                            new Style()
                                    .setColor(TextFormatting.RED)
                                    .setUnderlined(true)
                                    .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fsmm transfer reject"))))
                    .appendSibling(new TextComponentString(" to reject it"))
            );
            player.sendMessage(new TextComponentString("Use ").appendSibling(
                    new TextComponentString("/fsmm").setStyle(
                            new Style()
                                    .setColor(TextFormatting.AQUA)
                                    .setUnderlined(true)
                                    .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fsmm"))
                    ).appendSibling(new TextComponentString("\u00a7r to check your current balance").setStyle(new Style()))
            ));
            player.sendMessage(new TextComponentString("The request will be automatically \u00a7crejected\u00a7r in \u00a7b60\u00a7r seconds"));
        }
    }

    /**
     * Replies to a transfer request, either accepting or rejecting it, and removes the request from the list.
     * If the request has been accepted, it will execute the transfer.
     *
     * @param fromPlayer the player who sent the request
     * @param accepted   whether the request has been accepted or not
     */
    private void replyToTransferRequest(String fromPlayer, boolean accepted) {
        TransferRequest request = requests.get(fromPlayer);
        if (request == null) return;
        if (accepted) {
            accepted = executeTransferRequest(request.getFromPlayer(), request.getToPlayer(), request.getAmount());
        }
        request.getContext().signal("fsmm_transfer", request.getFromPlayer(), request.getToPlayer(), request.getAmount(), accepted);
        requests.remove(request.getFromPlayer());
    }

    /**
     * Executes a transfer request from one player to another.
     *
     * @param fromPlayer the username of the player making the transfer
     * @param toPlayer   the username of the player receiving the transfer
     * @param amount     the amount to be transferred
     * @return true if the transfer was successful, false otherwise
     */
    private boolean executeTransferRequest(String fromPlayer, String toPlayer, double amount) {
        boolean success = false;
        long actualAmount = (long) (amount * 1000);

        Map<String, Account> accounts = DataManager.getAccountsOfType("player");
        if (accounts == null) return false;

        String fromUUID = getUuidFromUsername(fromPlayer);
        String toUUID = getUuidFromUsername(toPlayer);

        Account fromAccount = accounts.get(fromUUID);
        Account toAccount = accounts.get(toUUID);

        EntityPlayerMP player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(fromPlayer);

        // bank should do the null-check on toAccount
        if (fromAccount != null) {
            fixAccountBank(fromAccount);
            fixAccountBank(toAccount);

            Bank bank = fromAccount.getBank();
            success = bank.processAction(Bank.Action.TRANSFER, player, fromAccount, actualAmount, toAccount, false);
        }

        if (!success) {
            player.sendMessage(new TextComponentString("\u00a7cError executing transaction: Check available balance."));
        } else {
            player.sendMessage(new TextComponentString("\u00a7aTransaction successful! New balance: \u00a7r" + Config.getWorthAsString(fromAccount.getBalance())));
        }

        return success;
    }

    /**
     * Fixes the bank of the given account if it is null by setting it to the default bank.
     * The default bank is retrieved by ID because @see DataManager#getDefaultBank() can return null in certain cases.
     *
     * @param account the account to fix the bank for
     */
    private void fixAccountBank(Account account) {
        if (account != null && account.getBank() == null)
            account.setBank(DataManager.getBank("0"));
    }

    /**
     * Retrieves the UUID associated with the given username.
     *
     * @param username the username for which to retrieve the UUID
     * @return the UUID associated with the given username, or null if the username is not found
     */
    private String getUuidFromUsername(String username) {
        GameProfile profile = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerProfileCache().getGameProfileForUsername(username);
        return profile == null ? null : profile.getId().toString();
    }

    /**
     * Accepts a transfer request from a player.
     * Used by the `/fsmm accept` command.
     *
     * @param fromPlayer the player who sent the transfer request
     * @return void
     */
    public void acceptTransferRequest(String fromPlayer) {
        replyToTransferRequest(fromPlayer, true);
    }

    /**
     * Rejects a transfer request from a player.
     * Used by the `/fsmm reject` command.
     *
     * @param fromPlayer the player who sent the transfer request
     */
    public void rejectTransferRequest(String fromPlayer) {
        replyToTransferRequest(fromPlayer, false);
    }

    /**
     * Checks if there is a transfer request for the given player.
     *
     * @param fromPlayer the name of the player to check for a transfer request
     * @return true if there is a transfer request for the player, false otherwise
     */
    public boolean hasTransferRequest(String fromPlayer) {
        return requests.containsKey(fromPlayer);
    }
}
