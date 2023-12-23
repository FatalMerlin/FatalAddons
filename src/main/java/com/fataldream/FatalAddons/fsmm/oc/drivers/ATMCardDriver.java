package com.fataldream.FatalAddons.fsmm.oc.drivers;

import com.fataldream.FatalAddons.FatalAddons;
import com.fataldream.FatalAddons.fsmm.oc.environments.ATMEnvironment;
import com.fataldream.FatalAddons.util.OCUtils;
import li.cil.oc.api.Driver;
import li.cil.oc.api.driver.DriverItem;
import li.cil.oc.api.driver.EnvironmentProvider;
import li.cil.oc.api.driver.item.Slot;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.EnvironmentHost;
import li.cil.oc.api.network.ManagedEnvironment;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author FatalMerlin <merlin.brandes@gmail.com>
 */
public class ATMCardDriver extends Item implements DriverItem, EnvironmentProvider {
    private static ATMCardDriver driver;

    public ATMCardDriver() {
        setTranslationKey(FatalAddons.MODID + ".card_banking");
        setCreativeTab(CreativeTabs.MISC);
        setMaxDamage(0);
    }

    @Override
    public boolean worksWith(ItemStack itemStack) {
        return itemStack.getItem().equals(this);
    }

    @Override
    public ManagedEnvironment createEnvironment(ItemStack itemStack, EnvironmentHost environmentHost) {
        return new ATMEnvironment();
    }

    @Override
    public String slot(ItemStack itemStack) {
        return Slot.Card;
    }

    @Override
    public int tier(ItemStack itemStack) {
        return 1;
    }

    @Override
    public NBTTagCompound dataTag(ItemStack itemStack) {
        return OCUtils.dataTag(itemStack);
    }

    @Override
    public Class<? extends Environment> getEnvironment(ItemStack itemStack) {
        return ATMEnvironment.class;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        OCUtils.addTooltip(stack, tooltip, flag);
    }

    public static void preInit() {
        driver = new ATMCardDriver();
        Driver.add((DriverItem) driver);
        Driver.add((EnvironmentProvider) driver);
        FatalAddons.registerItem(driver, "card_banking");
        FatalAddons.proxy.registerItemModel(driver, 0, "fataladdons:card_banking");
    }

    public static void postInit() {
        ItemStack result = new ItemStack(driver, 1, 0);

        GameRegistry.findRegistry(IRecipe.class).register(
                new ShapelessOreRecipe(null,
                        result,
                        "oc:dataCard1", ForgeRegistries.ITEMS.getValue(new ResourceLocation("fsmm:1foney")))
                        .setRegistryName(result.getItem().getRegistryName().getPath())
        );
    }
}
