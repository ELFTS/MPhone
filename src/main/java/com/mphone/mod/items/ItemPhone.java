package com.mphone.mod.items;

import com.mphone.mod.MPhoneMod;
import com.mphone.mod.gui.GuiHandler;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class ItemPhone extends Item {
    public ItemPhone(String name) {
        setTranslationKey(MPhoneMod.MODID + "." + name);
        setRegistryName(MPhoneMod.MODID, name);
        setCreativeTab(CreativeTabs.TOOLS);
        setMaxStackSize(1);
    }

    /**
     * 检查手机是否已插入SIM卡
     */
    public static boolean hasSIMCard(ItemStack phoneStack) {
        if (!phoneStack.hasTagCompound()) return false;
        return phoneStack.getTagCompound().hasKey("sim_number");
    }

    /**
     * 获取手机中的SIM卡号码
     */
    public static String getSIMNumber(ItemStack phoneStack) {
        if (!hasSIMCard(phoneStack)) return null;
        return phoneStack.getTagCompound().getString("sim_number");
    }

    /**
     * 插入SIM卡
     */
    public static boolean insertSIMCard(ItemStack phoneStack, ItemStack simStack) {
        if (hasSIMCard(phoneStack)) return false; // 已有SIM卡
        if (!(simStack.getItem() instanceof ItemSIMCard)) return false;

        String simNumber = ItemSIMCard.getSIMNumber(simStack);
        
        if (!phoneStack.hasTagCompound()) {
            phoneStack.setTagCompound(new NBTTagCompound());
        }
        
        phoneStack.getTagCompound().setString("sim_number", simNumber);
        ItemSIMCard.setInserted(simStack, true);
        return true;
    }

    /**
     * 取出SIM卡
     */
    public static String removeSIMCard(ItemStack phoneStack) {
        if (!hasSIMCard(phoneStack)) return null;
        
        String simNumber = getSIMNumber(phoneStack);
        phoneStack.getTagCompound().removeTag("sim_number");
        return simNumber;
    }

    /**
     * 获取当前打开的应用ID
     */
    public static int getCurrentApp(ItemStack phoneStack) {
        if (!phoneStack.hasTagCompound()) return -1;
        return phoneStack.getTagCompound().getInteger("current_app");
    }

    /**
     * 设置当前打开的应用ID
     */
    public static void setCurrentApp(ItemStack phoneStack, int appId) {
        if (!phoneStack.hasTagCompound()) {
            phoneStack.setTagCompound(new NBTTagCompound());
        }
        phoneStack.getTagCompound().setInteger("current_app", appId);
    }

    /**
     * 清除当前应用（返回主屏幕）
     */
    public static void clearCurrentApp(ItemStack phoneStack) {
        if (phoneStack.hasTagCompound()) {
            phoneStack.getTagCompound().removeTag("current_app");
        }
    }

    // ==================== 已安装应用管理 ====================

    private static final String INSTALLED_APPS_KEY = "installed_apps";

    /**
     * 获取已安装的应用ID列表
     */
    public static java.util.List<String> getInstalledApps(ItemStack phoneStack) {
        java.util.List<String> apps = new java.util.ArrayList<>();
        if (!phoneStack.hasTagCompound()) return apps;

        NBTTagCompound tag = phoneStack.getTagCompound();
        if (tag.hasKey(INSTALLED_APPS_KEY)) {
            String appsStr = tag.getString(INSTALLED_APPS_KEY);
            if (!appsStr.isEmpty()) {
                String[] appArray = appsStr.split(",");
                for (String app : appArray) {
                    if (!app.trim().isEmpty()) {
                        apps.add(app.trim());
                    }
                }
            }
        }
        return apps;
    }

    /**
     * 检查是否已安装某个应用
     */
    public static boolean hasAppInstalled(ItemStack phoneStack, String appId) {
        return getInstalledApps(phoneStack).contains(appId);
    }

    /**
     * 安装应用
     */
    public static boolean installApp(ItemStack phoneStack, String appId) {
        if (hasAppInstalled(phoneStack, appId)) return false;

        java.util.List<String> apps = getInstalledApps(phoneStack);
        apps.add(appId);
        saveInstalledApps(phoneStack, apps);
        return true;
    }

    /**
     * 卸载应用
     */
    public static boolean uninstallApp(ItemStack phoneStack, String appId) {
        if (!hasAppInstalled(phoneStack, appId)) return false;

        java.util.List<String> apps = getInstalledApps(phoneStack);
        apps.remove(appId);
        saveInstalledApps(phoneStack, apps);
        return true;
    }

    /**
     * 保存应用列表到NBT
     */
    private static void saveInstalledApps(ItemStack phoneStack, java.util.List<String> apps) {
        if (!phoneStack.hasTagCompound()) {
            phoneStack.setTagCompound(new NBTTagCompound());
        }
        String appsStr = String.join(",", apps);
        phoneStack.getTagCompound().setString(INSTALLED_APPS_KEY, appsStr);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack phoneStack = player.getHeldItem(hand);
        ItemStack otherStack = player.getHeldItem(hand == EnumHand.MAIN_HAND ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);

        // 检查是否是插入SIM卡的操作
        if (!otherStack.isEmpty() && otherStack.getItem() instanceof ItemSIMCard) {
            if (!world.isRemote) {
                if (hasSIMCard(phoneStack)) {
                    player.sendMessage(new TextComponentString("§c[MPhone] §f手机中已有SIM卡，请先取出"));
                } else {
                    if (insertSIMCard(phoneStack, otherStack)) {
                        String simNumber = ItemSIMCard.getSIMNumber(otherStack);
                        player.sendMessage(new TextComponentString("§a[MPhone] §f成功插入SIM卡，号码: " + simNumber));
                        // 消耗掉SIM卡
                        otherStack.shrink(1);
                    }
                }
            }
            return new ActionResult<>(EnumActionResult.SUCCESS, phoneStack);
        }

        // 检查是否是取出SIM卡的操作（潜行+右键）
        if (player.isSneaking()) {
            if (!world.isRemote) {
                if (hasSIMCard(phoneStack)) {
                    String simNumber = removeSIMCard(phoneStack);
                    player.sendMessage(new TextComponentString("§a[MPhone] §f已取出SIM卡，号码: " + simNumber));
                    // 尝试将SIM卡放入玩家背包
                    ItemStack simStack = new ItemStack(com.mphone.mod.init.ModItems.simCard);
                    ItemSIMCard.getSIMNumber(simStack); // 生成号码
                    simStack.getTagCompound().setString("sim_number", simNumber);
                    
                    if (!player.inventory.addItemStackToInventory(simStack)) {
                        player.dropItem(simStack, false);
                    }
                } else {
                    player.sendMessage(new TextComponentString("§c[MPhone] §f手机中没有SIM卡"));
                }
            }
            return new ActionResult<>(EnumActionResult.SUCCESS, phoneStack);
        }

        // 只在客户端打开GUI
        if (world.isRemote) {
            // 检查是否有上次打开的应用
            int currentApp = getCurrentApp(phoneStack);
            if (currentApp >= 0) {
                player.openGui(MPhoneMod.instance, currentApp, world, (int) player.posX, (int) player.posY, (int) player.posZ);
            } else {
                player.openGui(MPhoneMod.instance, GuiHandler.GUI_PHONE, world, (int) player.posX, (int) player.posY, (int) player.posZ);
            }
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, phoneStack);
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, java.util.List<String> tooltip, net.minecraft.client.util.ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        
        if (hasSIMCard(stack)) {
            tooltip.add("§aSIM卡: §f" + getSIMNumber(stack));
        } else {
            tooltip.add("§7未插入SIM卡");
        }
        
        tooltip.add("§7右键: 打开手机");
        tooltip.add("§7潜行+右键: 取出SIM卡");
        tooltip.add("§7手持SIM卡右键: 插入");
    }
}
