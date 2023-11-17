package com.fataldream.FatalAddons.fsmm.util;

import com.fataldream.FatalAddons.FatalAddons;
import com.fataldream.FatalAddons.util.ChatUtils;
import net.fexcraft.mod.fsmm.util.FSMMSubCommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import java.util.UUID;

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
                    break;
                } catch (IllegalArgumentException e) {
                    ChatUtils.sendMessage(sender, "&cInvalid Request ID.");
                }
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
