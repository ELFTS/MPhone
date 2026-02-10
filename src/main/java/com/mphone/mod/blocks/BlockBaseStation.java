package com.mphone.mod.blocks;

import com.mphone.mod.MPhoneMod;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockBaseStation extends Block {
    // 信号覆盖范围（方块半径）
    public static final int SIGNAL_RANGE = 64;
    
    public BlockBaseStation(String name) {
        super(Material.IRON);
        setTranslationKey(MPhoneMod.MODID + "." + name);
        setRegistryName(MPhoneMod.MODID, name);
        setCreativeTab(CreativeTabs.REDSTONE);
        setHardness(3.0F);
        setResistance(5.0F);
        setLightLevel(0.5F);
    }
    
    /**
     * 检查指定位置是否在基站的信号范围内
     */
    public static boolean isInSignalRange(World world, double x, double y, double z) {
        // 遍历所有加载的区块中的方块实体
        for (TileEntity te : world.loadedTileEntityList) {
            if (te instanceof TileEntityBaseStation) {
                TileEntityBaseStation baseStation = (TileEntityBaseStation) te;
                double distance = Math.sqrt(
                    Math.pow(te.getPos().getX() + 0.5 - x, 2) +
                    Math.pow(te.getPos().getY() + 0.5 - y, 2) +
                    Math.pow(te.getPos().getZ() + 0.5 - z, 2)
                );
                if (distance <= SIGNAL_RANGE) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * 获取指定位置的信号强度 (0-4)
     */
    public static int getSignalStrength(World world, double x, double y, double z) {
        double minDistance = Double.MAX_VALUE;
        
        for (TileEntity te : world.loadedTileEntityList) {
            if (te instanceof TileEntityBaseStation) {
                double distance = Math.sqrt(
                    Math.pow(te.getPos().getX() + 0.5 - x, 2) +
                    Math.pow(te.getPos().getY() + 0.5 - y, 2) +
                    Math.pow(te.getPos().getZ() + 0.5 - z, 2)
                );
                if (distance < minDistance) {
                    minDistance = distance;
                }
            }
        }
        
        if (minDistance == Double.MAX_VALUE) {
            return 0; // 无信号
        }
        
        // 根据距离计算信号强度
        if (minDistance <= SIGNAL_RANGE * 0.25) return 4;      // 0-16格: 满格信号
        if (minDistance <= SIGNAL_RANGE * 0.5) return 3;       // 16-32格: 3格信号
        if (minDistance <= SIGNAL_RANGE * 0.75) return 2;      // 32-48格: 2格信号
        if (minDistance <= SIGNAL_RANGE) return 1;             // 48-64格: 1格信号
        return 0;                                              // 超出范围: 无信号
    }
    
    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }
    
    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityBaseStation();
    }
}
