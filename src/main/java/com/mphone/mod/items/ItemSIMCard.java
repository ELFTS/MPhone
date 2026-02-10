package com.mphone.mod.items;

import com.mphone.mod.MPhoneMod;
import net.minecraft.client.util.ITooltipFlag;
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

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class ItemSIMCard extends Item {
    
    public ItemSIMCard(String name) {
        setTranslationKey(MPhoneMod.MODID + "." + name);
        setRegistryName(MPhoneMod.MODID, name);
        setCreativeTab(CreativeTabs.MISC);
        setMaxStackSize(1);
    }
    
    /**
     * 获取或创建SIM卡号码
     */
    public static String getSIMNumber(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        NBTTagCompound nbt = stack.getTagCompound();
        
        if (!nbt.hasKey("sim_number")) {
            // 生成随机电话号码
            String number = generatePhoneNumber();
            nbt.setString("sim_number", number);
        }
        
        return nbt.getString("sim_number");
    }
    
    /**
     * 生成随机电话号码 (6位)
     */
    private static String generatePhoneNumber() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append((int) (Math.random() * 10));
        }
        return sb.toString();
    }
    
    /**
     * 检查SIM卡是否已插入手机
     */
    public static boolean isInserted(ItemStack stack) {
        if (!stack.hasTagCompound()) return false;
        return stack.getTagCompound().getBoolean("inserted");
    }
    
    /**
     * 设置SIM卡插入状态
     */
    public static void setInserted(ItemStack stack, boolean inserted) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound().setBoolean("inserted", inserted);
    }
    
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        
        if (!stack.isEmpty()) {
            String number = getSIMNumber(stack);
            tooltip.add("§7电话号码: §f" + number);
            
            if (isInserted(stack)) {
                tooltip.add("§a已插入手机");
            } else {
                tooltip.add("§7右键手机插入");
            }
        }
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        
        if (!world.isRemote) {
            // 显示SIM卡信息
            String number = getSIMNumber(stack);
            player.sendMessage(new TextComponentString("§a[MPhone] §fSIM卡号码: " + number));
        }
        
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
