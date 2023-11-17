package com.fataldream.FatalAddons.util;

import li.cil.oc.api.Driver;
import li.cil.oc.api.driver.DriverItem;
import li.cil.oc.client.KeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collections;
import java.util.List;

public class OCUtils {
    public static NBTTagCompound dataTag(final ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        final NBTTagCompound nbt = stack.getTagCompound();
        // This is the suggested key under which to store item component data.
        // You are free to change this as you please.
        if (!nbt.hasKey("oc:data")) {
            nbt.setTag("oc:data", new NBTTagCompound());
        }
        return nbt.getCompoundTag("oc:data");
    }

    //Mostly stolen from Sangar and Vexatos
    private static final int maxWidth = 220;

    @SideOnly(Side.CLIENT)
    public static void addTooltip(ItemStack stack, List<String> tooltip, ITooltipFlag flag) {
        {
            FontRenderer font = Minecraft.getMinecraft().fontRenderer;
            final String key = stack.getTranslationKey() + ".tip";
            String tip = I18nUtil.localize(key);
            if (!tip.equals(key)) {
                String[] lines = tip.split("\n");
                if (font == null) {
                    Collections.addAll(tooltip, lines);
                } else {
                    boolean shouldShorten = (font.getStringWidth(tip) > maxWidth) && !KeyBindings.showExtendedTooltips();
                    if (shouldShorten) {
                        tooltip.add(I18nUtil.localizeAndFormat("oc:tooltip.toolong",
                                KeyBindings.getKeyBindingName(KeyBindings.extendedTooltip())));
                    } else {
                        for (String line : lines) {
                            List<String> list = font.listFormattedStringToWidth(line, maxWidth);
                            tooltip.addAll(list);
                        }
                    }
                }
            }
        }
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("oc:data")) {
            NBTTagCompound data = stack.getTagCompound().getCompoundTag("oc:data");
            if (data.hasKey("node") && data.getCompoundTag("node").hasKey("address")) {
                tooltip.add(TextFormatting.DARK_GRAY
                        + data.getCompoundTag("node").getString("address").substring(0, 13) + "..."
                        + TextFormatting.GRAY);
            }
        }
        if (flag.isAdvanced()) {
            DriverItem item = Driver.driverFor(stack);
            tooltip.add(I18nUtil.localizeAndFormat("oc:tooltip.tier", item != null ? item.tier(stack) + 1 : 0));
        }
    }
}
