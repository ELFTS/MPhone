package com.mphone.mod.appstore;

import com.mphone.mod.gui.GuiHandler;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

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

    private void initializeDefaultApps() {
        // 工具类应用
        availableApps.add(new AppStoreItem(
            "compass",
            "指南针",
            "显示方向的实用工具",
            new ItemStack(Items.COMPASS),
            AppStoreItem.AppType.UTILITY,
            GuiHandler.GUI_COMPASS,
            0xFF795548,  // 棕色
            0x263C       // 指南针符号
        ));

        availableApps.add(new AppStoreItem(
            "clock",
            "时钟",
            "显示当前游戏时间",
            new ItemStack(Items.CLOCK),
            AppStoreItem.AppType.UTILITY,
            GuiHandler.GUI_CLOCK,
            0xFF607D8B,  // 蓝灰色
            0x23F0       // 时钟符号
        ));

        availableApps.add(new AppStoreItem(
            "calculator",
            "计算器",
            "进行基本数学计算",
            new ItemStack(Items.PAPER),
            AppStoreItem.AppType.UTILITY,
            GuiHandler.GUI_CALCULATOR,
            0xFF3F51B5,  // 靛蓝色
            0x221A       // 根号符号
        ));

        // 游戏类应用
        availableApps.add(new AppStoreItem(
            "snake",
            "贪吃蛇",
            "经典贪吃蛇游戏",
            new ItemStack(Items.APPLE),
            AppStoreItem.AppType.GAME,
            GuiHandler.GUI_SNAKE_GAME,
            0xFF4CAF50,  // 绿色
            0x2605       // 星星
        ));

        availableApps.add(new AppStoreItem(
            "tetris",
            "俄罗斯方块",
            "经典方块消除游戏",
            new ItemStack(Items.BRICK),
            AppStoreItem.AppType.GAME,
            GuiHandler.GUI_TETRIS_GAME,
            0xFF9C27B0,  // 紫色
            0x25A0       // 方块
        ));

        // 社交类应用
        availableApps.add(new AppStoreItem(
            "chat",
            "聊天室",
            "与服务器玩家聊天",
            new ItemStack(Items.BOOK),
            AppStoreItem.AppType.SOCIAL,
            GuiHandler.GUI_CHAT,
            0xFF2196F3,  // 蓝色
            0x272F       // 对话气泡
        ));

        availableApps.add(new AppStoreItem(
            "mail",
            "邮件",
            "发送和接收邮件",
            new ItemStack(Items.FEATHER),
            AppStoreItem.AppType.SOCIAL,
            GuiHandler.GUI_MAIL,
            0xFFFF9800,  // 橙色
            0x2709       // 信封
        ));

        // 生产力类应用
        availableApps.add(new AppStoreItem(
            "notes",
            "备忘录",
            "记录重要事项",
            new ItemStack(Items.WRITABLE_BOOK),
            AppStoreItem.AppType.PRODUCTIVITY,
            GuiHandler.GUI_NOTES,
            0xFFFFEB3B,  // 黄色
            0x270E       // 铅笔
        ));

        availableApps.add(new AppStoreItem(
            "calendar",
            "日历",
            "查看游戏内日期",
            new ItemStack(Items.PAPER),
            AppStoreItem.AppType.PRODUCTIVITY,
            GuiHandler.GUI_CALENDAR,
            0xFFE91E63,  // 粉红色
            0x1F4C5      // 日历符号
        ));

        // 娱乐类应用
        availableApps.add(new AppStoreItem(
            "music",
            "音乐播放器",
            "播放游戏音乐",
            new ItemStack(Items.RECORD_13),
            AppStoreItem.AppType.ENTERTAINMENT,
            GuiHandler.GUI_MUSIC,
            0xFF00BCD4,  // 青色
            0x266B       // 音符
        ));

        availableApps.add(new AppStoreItem(
            "gallery",
            "相册",
            "查看拍摄的照片",
            new ItemStack(Items.PAINTING),
            AppStoreItem.AppType.ENTERTAINMENT,
            GuiHandler.GUI_GALLERY,
            0xFFFF5722,  // 深橙色
            0x25A1       // 图片框
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
