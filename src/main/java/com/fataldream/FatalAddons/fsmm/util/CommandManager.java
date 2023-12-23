package com.fataldream.FatalAddons.fsmm.util;

import com.fataldream.FatalAddons.FatalAddons;
import com.fataldream.FatalAddons.util.ChatUtils;
import com.fataldream.FatalAddons.util.PlayerUtils;
import com.mojang.authlib.GameProfile;
import net.fexcraft.mod.fsmm.util.FSMMSubCommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author FatalMerlin <merlin.brandes@gmail.com>
 */
public class CommandManager implements FSMMSubCommand {
    public static void register() {
        FSMMSubCommand.register("transfer", new CommandManager());
    }

    @Override
    public void process(MinecraftServer server, ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayerMP)) return;

        if (args.length < 2) {
            printHelp(sender);
            return;
        }

        EntityPlayerMP player = (EntityPlayerMP) sender;
        TransferManager transferManager = TransferManager.getInstance();

        switch (args[1]) {
            case "list": {
                transferManager.listTransferRequests(player);
                break;
            }
            case "accept":
            case "reject": {
                if (args.length < 3) {
                    printHelp(sender);
                    return;
                }

                try {
                    UUID requestId = UUID.fromString(args[2]);

                    transferManager.replyToTransferRequest(player, requestId, args[1].equals("accept"));
                } catch (IllegalArgumentException e) {
                    ChatUtils.sendMessage(sender, "&cInvalid Request ID.");
                }
                break;
            }
            // /fsmm transfer request [player] [amount] [description]
            case "request": {
                if (args.length < 4) {
                    printHelp(sender);
                    return;
                }

                String fromPlayer = args[2];
                String description = Arrays.stream(args).skip(4).collect(Collectors.joining(" "));
                double amount;

                try {
                    amount = Double.parseDouble(args[3]);
                } catch (NumberFormatException e) {
                    ChatUtils.sendMessage(sender, "&cInvalid amount.");
                    return;
                }

                GameProfile fromProfile = PlayerUtils.getGameProfile(fromPlayer);
                if (fromProfile == null) {
                    ChatUtils.sendMessage(sender, "&cInvalid player name.");
                    return;
                }
                // check if online
                if (!PlayerUtils.isOnline(fromPlayer)) {
                    ChatUtils.sendMessage(sender, "&cTarget player is not online.");
                    return;
                }

                if (amount <= 0) {
                    ChatUtils.sendMessage(sender, "&cAmount must be greater than 0.");
                    return;
                }

                GameProfile toProfile = player.getGameProfile();

                UUID requestId = TransferManager.getInstance().requestTransfer(fromProfile, toProfile, amount, description);

                ChatUtils.sendMessage(sender, "&aRequest sent. (" + requestId + ")");
                break;
            }
            default: {
                printHelp(sender);
            }
        }
    }

    @Override
    public void printHelp(ICommandSender sender) {
        ChatUtils.sendMessage(sender, String.join("\n",
                "=== &bFSMM + " + FatalAddons.NAME + "&r ===",
                "&7/fsmm transfer request <&bplayer&7> <&bamount&7> [<&bdescription&7>]",
                "&7/fsmm transfer <&aaccept&7|&creject&7> <&brequest id&7>",
                "&7/fsmm transfer list"
        ));
    }

    @Override
    public void printVersion(ICommandSender sender) {
        sender.sendMessage(new TextComponentString(FatalAddons.NAME + " Version: " + FatalAddons.VERSION));
    }

    @Override
    public void printStatus(ICommandSender sender) {

    }
}
