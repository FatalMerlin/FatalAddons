package com.fataldream.FatalAddons.fsmm.util;

import com.fataldream.FatalAddons.fsmm.data.TransferRequest;
import com.fataldream.FatalAddons.util.ChatUtils;
import com.fataldream.FatalAddons.util.PlayerUtils;
import com.mojang.authlib.GameProfile;
import li.cil.oc.api.machine.Context;
import net.fexcraft.mod.fsmm.data.Account;
import net.fexcraft.mod.fsmm.data.Bank;
import net.fexcraft.mod.fsmm.util.Config;
import net.fexcraft.mod.fsmm.util.DataManager;
import net.minecraft.entity.player.EntityPlayerMP;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author FatalMerlin (merlin.brandes@gmail.com)
 */
public class TransferManager {
    private static TransferManager instance;
    public static final int REQUEST_EXPIRY_SECONDS = 60;
    private final HashMap<UUID, LinkedHashMap<UUID, TransferRequest>> requests = new HashMap<>();

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
     */
    public UUID requestTransfer(GameProfile fromPlayer, GameProfile toPlayer, double amount, String description) {
        return requestTransfer(fromPlayer, toPlayer, amount, description, null);
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
    public UUID requestTransfer(GameProfile fromPlayer, GameProfile toPlayer, double amount, String description, @Nullable Context context) {
        TransferRequest request = new TransferRequest(fromPlayer, toPlayer, amount, description, context);

        if (!requests.containsKey(fromPlayer.getId())) {
            requests.put(fromPlayer.getId(), new LinkedHashMap<>());
        }

        requests.get(fromPlayer.getId()).put(request.getId(), request);

        EntityPlayerMP player = PlayerUtils.getPlayer(fromPlayer);

        if (player != null) {
            ChatUtils.sendMessage(player, String.join("\n",
                    "\n=== &bNew transfer request&r ===",
                    request.toString(),
                    "==========================" // this is padding after the button because the underline overlaps the next line
            ));
        }

        return request.getId();
    }

    /**
     * Replies to a transfer request, either accepting or rejecting it, and removes the request from the list.
     * If the request has been accepted, it will execute the transfer.
     *
     * @param fromPlayer the player who sent the request
     * @param accepted   whether the request has been accepted or not
     */
    void replyToTransferRequest(EntityPlayerMP fromPlayer, UUID requestId, boolean accepted) {
        GameProfile profile = fromPlayer.getGameProfile();

        if (!hasTransferRequest(profile, requestId)) {
            ChatUtils.sendMessage(
                    fromPlayer,
                    "&cNo transfer request with id &b" + requestId + "&r"
            );
            return;
        }

        HashMap<UUID, TransferRequest> playerRequests = requests.get(profile.getId());
        TransferRequest request = requests.get(profile.getId()).get(requestId);

        if (accepted) {
            accepted = executeTransferRequest(fromPlayer, request.getToPlayer(), request.getAmount());
        } else {
            ChatUtils.sendMessage(
                    fromPlayer,
                    "&cTransfer request rejected"
            );
        }

        if (!accepted) {
            ChatUtils.sendMessage(
                    PlayerUtils.getPlayer(request.getToPlayer()),
                    "&c" + request.getFromPlayer().getName() + " rejected your transfer request."
            );
        }

        sendSignalToRequestContext(request, accepted);
        playerRequests.remove(requestId);
    }

    private void sendSignalToRequestContext(TransferRequest request, boolean success) {
        Context context = request.getContext();
        // we don't want to send signals if the context is null
        if (context == null) return;

        context.signal(
                "fsmm_transfer",
                request.getId().toString(),
                request.getFromPlayer().getName(),
                request.getToPlayer().getName(),
                request.getAmount(),
                success
        );
    }

    /**
     * Executes a transfer request from one player to another.
     *
     * @param fromPlayer the username of the player making the transfer
     * @param toPlayer   the username of the player receiving the transfer
     * @param amount     the amount to be transferred
     * @return true if the transfer was successful, false otherwise
     */
    private boolean executeTransferRequest(EntityPlayerMP fromPlayer, GameProfile toPlayer, double amount) {
        boolean success = false;
        long actualAmount = (long) (amount * 1000);

        Map<String, Account> accounts = DataManager.getAccountsOfType("player");
        if (accounts == null) return false;

        Account fromAccount = accounts.get(fromPlayer.getGameProfile().getId().toString());
        Account toAccount = accounts.get(toPlayer.getId().toString());

        // bank should do the null-check on toAccount
        if (fromAccount != null) {
            fixAccountBank(fromAccount);
            fixAccountBank(toAccount);

            Bank bank = fromAccount.getBank();
            success = bank.processAction(Bank.Action.TRANSFER, fromPlayer, fromAccount, actualAmount, toAccount, false);
        }

        if (success) {
            if (PlayerUtils.isOnline(fromPlayer.getGameProfile())) {
                ChatUtils.sendMessage(fromPlayer, "&aTransaction successful! New balance: &r"
                        + Config.getWorthAsString(fromAccount.getBalance()));
            }

            if (PlayerUtils.isOnline(toPlayer)) {
                ChatUtils.sendMessage(PlayerUtils.getPlayer(toPlayer), "&a" + fromPlayer.getGameProfile().getName()
                        + " sent you &r" + Config.getWorthAsString((long) (amount * 1000)));
            }
        } else {
            ChatUtils.sendMessage(fromPlayer, "&cError executing transaction: Insufficient funds.");
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
     * Checks if there is a transfer request for the given player.
     *
     * @param fromPlayer the name of the player to check for a transfer request
     * @return true if there is a transfer request for the player, false otherwise
     */
    public boolean hasTransferRequest(GameProfile fromPlayer, UUID requestId) {
        return requests.containsKey(fromPlayer.getId()) && requests.get(fromPlayer.getId()).containsKey(requestId);
    }

    public void listTransferRequests(EntityPlayerMP player) {
        HashMap<UUID, TransferRequest> playerRequests = requests.get(player.getGameProfile().getId());
        ChatUtils.sendMessage(player, String.join("\n",
                "=== &bTransfer Requests&r ===",
                playerRequests == null || playerRequests.isEmpty() ? "No Transfer Requests." : playerRequests.values().stream()
                        .map(TransferRequest::toString)
                        .collect(Collectors.joining("=========================\n"))
        ));
    }

    protected void cleanupRequests() {
        Date now = new Date();

        // use iterator approach to prevent ConcurrentModificationException
        // see: https://stackoverflow.com/questions/602636/why-is-a-concurrentmodificationexception-thrown-and-how-to-debug-it
        // see: https://stackoverflow.com/questions/16180568/concurrentmodificationexception-with-linkedhashmap
        Iterator<LinkedHashMap<UUID, TransferRequest>> entryIterator = requests.values().iterator();
        while (entryIterator.hasNext()) {
            LinkedHashMap<UUID, TransferRequest> entry = entryIterator.next();

            Iterator<TransferRequest> playerRequestIterator = entry.values().iterator();
            while (playerRequestIterator.hasNext()) {
                TransferRequest playerRequests = playerRequestIterator.next();
                if (playerRequests.getExpiryDate().before(now)) {
                    GameProfile toPlayer = playerRequests.getToPlayer();
                    GameProfile fromPlayer = playerRequests.getFromPlayer();

                    Context context = playerRequests.getContext();

                    // if context is null the request was sent via command from a player
                    if (context == null && PlayerUtils.isOnline(toPlayer)) {
                        ChatUtils.sendMessage(PlayerUtils.getPlayer(toPlayer), "&cTransfer request for " + fromPlayer.getName() + " expired.");
                    }
                    sendSignalToRequestContext(playerRequests, false);
                    playerRequestIterator.remove();
                } else {
                    // if we encounter a value that is not yet expired, we can stop the loop as the LinkedHashMap is
                    // ordered by insertion order
                    break;
                }
            }

            // clean up the map if it is empty to save memory and processing time for the next iteration
            if (entry.isEmpty()) {
                entryIterator.remove();
            }
        }
    }
}
