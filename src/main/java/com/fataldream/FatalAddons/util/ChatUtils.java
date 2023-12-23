package com.fataldream.FatalAddons.util;

import com.fataldream.FatalAddons.FatalAddons;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author FatalMerlin <merlin.brandes@gmail.com>
 */
public class ChatUtils {
    private static final String PARAGRAPH = "\u00a7";
    private static final Pattern CLICK_EVENT_PATTERN = Pattern.compile(PARAGRAPH + "([rsu])\\[(.*?)\\]\\((\\/.*?)\\)");

    // TODO: switch to rest parameter?
    public static void sendMessage(@Nullable ICommandSender sender, String message) {
        if (sender == null) return;
        Arrays.stream(message.split("\n")).map(ChatUtils::parse).forEach(sender::sendMessage);
    }

    private static ITextComponent parse(String message) {
        message = message.replace("&", PARAGRAPH);
        Matcher matcher = CLICK_EVENT_PATTERN.matcher(message);

        ITextComponent component = new TextComponentString("");

        while (matcher.find()) {
            String[] split = message.split(Pattern.quote(matcher.group()), 2);
            message = split[1];

            component.appendSibling(
                    new TextComponentString(split[0])
            );

            String type = matcher.group(1);
            String text = matcher.group(2);
            String param = matcher.group(3);

            ClickEvent.Action action;

            switch (type) {
                case "r":
                    action = ClickEvent.Action.RUN_COMMAND;
                    break;
                case "s":
                    action = ClickEvent.Action.SUGGEST_COMMAND;
                    break;
                case "u":
                    action = ClickEvent.Action.OPEN_URL;
                    break;
                default:
                    throw new IllegalArgumentException(FatalAddons.NAME + ": Invalid click event value: " + type);
            }

            ITextComponent clickEventComponent = new TextComponentString(text);
            clickEventComponent.setStyle(
                    new Style()
                            .setClickEvent(new ClickEvent(action, param))
                            .setHoverEvent(
                                    new HoverEvent(
                                            HoverEvent.Action.SHOW_TEXT,
                                            new TextComponentString(param)
                                    )
                            )
            );
            component.appendSibling(clickEventComponent);
        }

        component.appendSibling(new TextComponentString(message));

        return component;
    }
}
