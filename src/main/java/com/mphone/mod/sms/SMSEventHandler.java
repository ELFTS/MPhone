package com.mphone.mod.sms;

import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * 短信世界事件处理器 - 处理世界加载和保存事件
 */
@Mod.EventBusSubscriber
public class SMSEventHandler {

    private static int saveTickCounter = 0;

    /**
     * 世界加载时读取短信数据
     */
    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event) {
        World world = event.getWorld();
        // 只在服务端执行
        if (!world.isRemote && world.provider.getDimension() == 0) {
            SMSWorldData worldData = SMSWorldData.getInstance(world);
            worldData.syncToManager();
        }
    }

    /**
     * 世界保存时写入短信数据
     */
    @SubscribeEvent
    public static void onWorldSave(WorldEvent.Save event) {
        World world = event.getWorld();
        // 只在服务端执行
        if (!world.isRemote && world.provider.getDimension() == 0) {
            SMSWorldData worldData = SMSWorldData.getInstance(world);
            worldData.syncFromManager();
        }
    }

    /**
     * 定期保存短信数据（每5分钟）
     */
    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !event.world.isRemote && event.world.provider.getDimension() == 0) {
            saveTickCounter++;
            // 5分钟 = 6000 ticks (20 ticks/second * 60 seconds * 5 minutes)
            if (saveTickCounter >= 6000) {
                saveTickCounter = 0;
                SMSWorldData worldData = SMSWorldData.getInstance(event.world);
                worldData.syncFromManager();
            }
        }
    }
}
