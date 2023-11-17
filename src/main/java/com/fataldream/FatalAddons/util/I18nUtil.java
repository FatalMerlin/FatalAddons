package com.fataldream.FatalAddons.util;

import net.minecraft.util.text.translation.I18n;

/**
 * @author FatalMerlin <merlin.brandes@gmail.com>
 */
public class I18nUtil {
    public static String localize(String key) {
        return I18n.translateToLocal(key).replace("\\n", "\n");
    }

    public static String localizeAndFormat(String key, Object... formatting) {
        return I18n.translateToLocalFormatted(key, formatting);
    }
}
