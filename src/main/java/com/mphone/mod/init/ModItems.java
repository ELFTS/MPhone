package com.mphone.mod.init;

import com.mphone.mod.MPhoneMod;
import com.mphone.mod.items.ItemPhone;
import com.mphone.mod.items.ItemSIMCard;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid = MPhoneMod.MODID)
public class ModItems {
    public static Item phone;
    public static Item simCard;

    public static void init() {
        phone = new ItemPhone("phone");
        simCard = new ItemSIMCard("sim_card");
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(phone);
        event.getRegistry().register(simCard);
    }

    @SideOnly(Side.CLIENT)
    public static void registerModels() {
        registerModel(phone);
        registerModel(simCard);
    }

    @SideOnly(Side.CLIENT)
    private static void registerModel(Item item) {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }
}
