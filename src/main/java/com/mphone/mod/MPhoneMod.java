package com.mphone.mod;

import com.mphone.mod.gui.GuiHandler;
import com.mphone.mod.proxy.CommonProxy;
import com.mphone.mod.sms.SMSEventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

@Mod(modid = MPhoneMod.MODID, name = MPhoneMod.NAME, version = MPhoneMod.VERSION, acceptedMinecraftVersions = "[1.12.2]")
public class MPhoneMod {
    public static final String MODID = "mphone";
    public static final String NAME = "MPhone Mod";
    public static final String VERSION = "1.0.1";

    @Instance(MODID)
    public static MPhoneMod instance;

    @SidedProxy(clientSide = "com.mphone.mod.proxy.ClientProxy", serverSide = "com.mphone.mod.proxy.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
        // 注册短信事件处理器
        MinecraftForge.EVENT_BUS.register(SMSEventHandler.class);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }
}
