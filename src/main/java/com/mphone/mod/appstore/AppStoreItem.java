package com.mphone.mod.appstore;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * 应用商店中的应用定义
 * 注意：此类只定义应用的基本信息，不包含安装状态
 * 安装状态存储在每个手机的NBT中
 */
public class AppStoreItem {
    private final String id;
    private final String name;
    private final String description;
    private final ItemStack icon;
    private final AppType type;
    private final int guiId;
    private final int iconColor;
    private final int iconChar;
    private final ResourceLocation iconTexture;

    public enum AppType {
        UTILITY("工具"),
        GAME("游戏"),
        SOCIAL("社交"),
        PRODUCTIVITY("生产力"),
        ENTERTAINMENT("娱乐");

        private final String displayName;

        AppType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public AppStoreItem(String id, String name, String description, ItemStack icon, AppType type, 
                        int guiId, int iconColor, int iconChar) {
        this(id, name, description, icon, type, guiId, iconColor, iconChar, null);
    }

    public AppStoreItem(String id, String name, String description, ItemStack icon, AppType type, 
                        int guiId, int iconColor, int iconChar, ResourceLocation iconTexture) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.type = type;
        this.guiId = guiId;
        this.iconColor = iconColor;
        this.iconChar = iconChar;
        this.iconTexture = iconTexture;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public AppType getType() {
        return type;
    }

    public int getGuiId() {
        return guiId;
    }

    public int getIconColor() {
        return iconColor;
    }

    public int getIconChar() {
        return iconChar;
    }

    public ResourceLocation getIconTexture() {
        return iconTexture;
    }

    public boolean hasIconTexture() {
        return iconTexture != null;
    }
}
