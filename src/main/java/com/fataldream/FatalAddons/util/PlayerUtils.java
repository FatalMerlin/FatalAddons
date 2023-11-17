package com.fataldream.FatalAddons.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * @author FatalMerlin <merlin.brandes@gmail.com>
 */
public class PlayerUtils {
    public static MinecraftServer getMinecraftServer() {
        return FMLCommonHandler.instance().getMinecraftServerInstance();
    }

    public static PlayerProfileCache getPlayerProfileCache() {
        return getMinecraftServer().getPlayerProfileCache();
    }

    public static PlayerList getPlayerList() {
        return getMinecraftServer().getPlayerList();
    }

    @Nullable
    public static GameProfile getGameProfile(String username) {
        return getPlayerProfileCache().getGameProfileForUsername(username);
    }

    @Nullable
    public static GameProfile getGameProfile(UUID uuid) {
        return getPlayerProfileCache().getProfileByUUID(uuid);
    }

    public static boolean isOnline(String username) {
        return getPlayerList().getPlayerByUsername(username) != null;
    }

    public static boolean isOnline(UUID uuid) {
        // The annotation on getPlayerByUUID is wrong, the underlying code will return null if the uuid is not found
        //noinspection ConstantValue
        return getPlayerList().getPlayerByUUID(uuid) != null;
    }

    public static boolean isOnline(GameProfile profile) {
        // The annotation on getPlayerByUUID is wrong, the underlying code will return null if the uuid is not found
        //noinspection ConstantValue
        return getPlayerList().getPlayerByUUID(profile.getId()) != null;
    }

    @Nullable
    public static EntityPlayerMP getPlayer(String username) {
        return getPlayerList().getPlayerByUsername(username);
    }

    @Nullable
    public static EntityPlayerMP getPlayer(UUID uuid) {
        return getPlayerList().getPlayerByUUID(uuid);
    }

    @Nullable
    public static EntityPlayerMP getPlayer(GameProfile profile) {
        return getPlayerList().getPlayerByUUID(profile.getId());
    }
}
