package com.mphone.mod.appstore;

import com.mphone.mod.MPhoneMod;
import com.mphone.mod.gui.GuiHandler;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * 应用商店管理器
 * 注意：此类只管理应用定义，不存储安装状态
 * 安装状态存储在每个手机的NBT中（通过ItemPhone管理）
 */
public class AppStoreManager {
    private static final List<AppStoreItem> availableApps = new ArrayList<>();
    private static AppStoreManager instance;

    private AppStoreManager() {
        initializeDefaultApps();
    }

    public static AppStoreManager getInstance() {
        if (instance == null) {
            instance = new AppStoreManager();
        }
        return instance;
    }

    private ResourceLocation getAppIcon(String name) {
        // 使用 AppIconManager 获取预加载的图标
        com.mphone.mod.client.AppIconManager.init();
        return com.mphone.mod.client.AppIconManager.getIcon(name);
    }

    private void initializeDefaultApps() {
        // 工具类应用
        availableApps.add(new AppStoreItem(
            "compass",
            "指南针",
            "显示方向的实用工具",
            new ItemStack(Items.COMPASS),
            AppStoreItem.AppType.UTILITY,
            GuiHandler.GUI_COMPASS,
            0xFF835432,  // 棕色 (Minecraft风格)
            0x263C,      // 指南针符号 (备用)
            getAppIcon("compass")
        ));

        availableApps.add(new AppStoreItem(
            "clock",
            "时钟",
            "显示当前游戏时间",
            new ItemStack(Items.CLOCK),
            AppStoreItem.AppType.UTILITY,
            GuiHandler.GUI_CLOCK,
            0xFF3AB3DA,  // 青色 (Minecraft风格)
            0x23F0,      // 时钟符号 (备用)
            getAppIcon("clock")
        ));

        availableApps.add(new AppStoreItem(
            "calculator",
            "计算器",
            "进行基本数学计算",
            new ItemStack(Items.PAPER),
            AppStoreItem.AppType.UTILITY,
            GuiHandler.GUI_CALCULATOR,
            0xFF3C44AA,  // 蓝色 (Minecraft风格)
            0x221A,      // 根号符号 (备用)
            getAppIcon("calculator")
        ));

        // 游戏类应用
        availableApps.add(new AppStoreItem(
            "snake",
            "贪吃蛇",
            "经典贪吃蛇游戏",
            new ItemStack(Items.APPLE),
            AppStoreItem.AppType.GAME,
            GuiHandler.GUI_SNAKE_GAME,
            0xFF5D8C22,  // 绿色 (Minecraft风格)
            0x2605,      // 星星 (备用)
            getAppIcon("snake")
        ));

        availableApps.add(new AppStoreItem(
            "tetris",
            "俄罗斯方块",
            "经典方块消除游戏",
            new ItemStack(Items.BRICK),
            AppStoreItem.AppType.GAME,
            GuiHandler.GUI_TETRIS_GAME,
            0xFF8932B8,  // 紫色 (Minecraft风格)
            0x25A0,      // 方块 (备用)
            getAppIcon("tetris")
        ));

        // 社交类应用
        availableApps.add(new AppStoreItem(
            "chat",
            "聊天室",
            "与服务器玩家聊天",
            new ItemStack(Items.BOOK),
            AppStoreItem.AppType.SOCIAL,
            GuiHandler.GUI_CHAT,
            0xFF3C44AA,  // 蓝色 (Minecraft风格)
            0x272F,      // 对话气泡 (备用)
            getAppIcon("chat")
        ));

        availableApps.add(new AppStoreItem(
            "mail",
            "邮件",
            "发送和接收邮件",
            new ItemStack(Items.FEATHER),
            AppStoreItem.AppType.SOCIAL,
            GuiHandler.GUI_MAIL,
            0xFFF9801D,  // 橙色 (Minecraft风格)
            0x2709,      // 信封 (备用)
            getAppIcon("mail")
        ));

        // 生产力类应用
        availableApps.add(new AppStoreItem(
            "notes",
            "备忘录",
            "记录重要事项",
            new ItemStack(Items.WRITABLE_BOOK),
            AppStoreItem.AppType.PRODUCTIVITY,
            GuiHandler.GUI_NOTES,
            0xFFFED83D,  // 黄色 (Minecraft风格)
            0x270E,      // 铅笔 (备用)
            getAppIcon("notes")
        ));

        availableApps.add(new AppStoreItem(
            "calendar",
            "日历",
            "查看游戏内日期",
            new ItemStack(Items.PAPER),
            AppStoreItem.AppType.PRODUCTIVITY,
            GuiHandler.GUI_CALENDAR,
            0xFFC74EBD,  // 粉色 (Minecraft风格)
            0x1F4C5,     // 日历符号 (备用)
            getAppIcon("calendar")
        ));

        // 娱乐类应用
        availableApps.add(new AppStoreItem(
            "music",
            "音乐播放器",
            "播放游戏音乐",
            new ItemStack(Items.RECORD_13),
            AppStoreItem.AppType.ENTERTAINMENT,
            GuiHandler.GUI_MUSIC,
            0xFF169C9C,  // 青色 (Minecraft风格)
            0x266B,      // 音符 (备用)
            getAppIcon("music")
        ));

        availableApps.add(new AppStoreItem(
            "gallery",
            "相册",
            "查看拍摄的照片",
            new ItemStack(Items.PAINTING),
            AppStoreItem.AppType.ENTERTAINMENT,
            GuiHandler.GUI_GALLERY,
            0xFFB02E26,  // 红色 (Minecraft风格)
            0x25A1,      // 图片框 (备用)
            getAppIcon("gallery")
        ));
    }

    public List<AppStoreItem> getAllApps() {
        return new ArrayList<>(availableApps);
    }

    public List<AppStoreItem> getAppsByType(AppStoreItem.AppType type) {
        List<AppStoreItem> result = new ArrayList<>();
        for (AppStoreItem app : availableApps) {
            if (app.getType() == type) {
                result.add(app);
            }
        }
        return result;
    }

    public AppStoreItem getAppById(String id) {
        for (AppStoreItem app : availableApps) {
            if (app.getId().equals(id)) {
                return app;
            }
        }
        return null;
    }
}
