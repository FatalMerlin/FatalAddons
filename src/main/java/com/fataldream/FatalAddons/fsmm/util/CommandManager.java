package com.fataldream.FatalAddons.fsmm.util;

import com.fataldream.FatalAddons.FatalAddons;
import net.fexcraft.mod.fsmm.util.FSMMSubCommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandManager implements FSMMSubCommand {
    public static void register() {
        FSMMSubCommand.register("transfer", new CommandManager());
    }

    @Override
    public void process(MinecraftServer server, ICommandSender sender, String[] args) {
        if (args.length < 2) {
            printHelp(sender);
            return;
        }

        switch (args[1]) {
            case "accept": {
                if (!TransferManager.getInstance().hasTransferRequest(sender.getName())) {
                    sender.sendMessage(new TextComponentString("\u00a7bNo Transfer Requests."));
                    return;
                }
                TransferManager.getInstance().acceptTransferRequest(sender.getName());
                sender.sendMessage(new TextComponentString("\u00a7aTransfer Request accepted."));
                break;
            }
            case "reject": {
                if (!TransferManager.getInstance().hasTransferRequest(sender.getName())) {
                    sender.sendMessage(new TextComponentString("\u00a7bNo Transfer Requests."));
                    return;
                }
                TransferManager.getInstance().rejectTransferRequest(sender.getName());
                sender.sendMessage(new TextComponentString("\u00a7cTransfer Request rejected."));
                break;
            }
            default: {
                printHelp(sender);
            }
        }
    }

    @Override
    public void printHelp(ICommandSender sender) {
        sender.sendMessage(new TextComponentString("=== " + FatalAddons.NAME + " ==="));
        sender.sendMessage(new TextComponentString("/fsmm transfer <accept|reject>"));
    }

    @Override
    public void printVersion(ICommandSender sender) {
        sender.sendMessage(new TextComponentString(FatalAddons.NAME + " Version: " + FatalAddons.VERSION));
    }

    @Override
    public void printStatus(ICommandSender sender) {

    }
}
