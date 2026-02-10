package com.mphone.mod.init;

import com.mphone.mod.MPhoneMod;
import com.mphone.mod.blocks.BlockBaseStation;
import com.mphone.mod.blocks.TileEntityBaseStation;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid = MPhoneMod.MODID)
public class ModBlocks {
    public static Block baseStation;

    public static void init() {
        baseStation = new BlockBaseStation("base_station");
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(baseStation);
        
        // 注册方块实体
        GameRegistry.registerTileEntity(TileEntityBaseStation.class, 
            new ResourceLocation(MPhoneMod.MODID, "base_station"));
    }

    @SubscribeEvent
    public static void registerItemBlocks(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new ItemBlock(baseStation).setRegistryName(baseStation.getRegistryName()));
    }

    @SideOnly(Side.CLIENT)
    public static void registerModels() {
        registerModel(Item.getItemFromBlock(baseStation));
    }

    @SideOnly(Side.CLIENT)
    private static void registerModel(Item item) {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }
}
