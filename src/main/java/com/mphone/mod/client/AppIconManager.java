package com.mphone.mod.client;

import com.mphone.mod.MPhoneMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Map;

/**
 * 应用图标纹理管理器
 * 负责预加载和缓存应用图标纹理
 */
@SideOnly(Side.CLIENT)
public class AppIconManager {
    private static final Map<String, ResourceLocation> ICON_CACHE = new HashMap<>();
    
    // 系统应用图标
    public static final String ICON_CONTACTS = "contacts";
    public static final String ICON_SMS = "sms";
    public static final String ICON_CAMERA = "camera";
    public static final String ICON_SETTINGS = "settings";
    public static final String ICON_APPSTORE = "appstore";
    
    // 应用商店应用图标
    public static final String ICON_COMPASS = "compass";
    public static final String ICON_CLOCK = "clock";
    public static final String ICON_CALCULATOR = "calculator";
    public static final String ICON_SNAKE = "snake";
    public static final String ICON_TETRIS = "tetris";
    public static final String ICON_CHAT = "chat";
    public static final String ICON_MAIL = "mail";
    public static final String ICON_NOTES = "notes";
    public static final String ICON_CALENDAR = "calendar";
    public static final String ICON_MUSIC = "music";
    public static final String ICON_GALLERY = "gallery";
    
    /**
     * 初始化并预加载所有图标纹理
     */
    public static void init() {
        // 预加载所有图标
        String[] icons = {
            ICON_CONTACTS, ICON_SMS, ICON_CAMERA, ICON_SETTINGS, ICON_APPSTORE,
            ICON_COMPASS, ICON_CLOCK, ICON_CALCULATOR, ICON_SNAKE, ICON_TETRIS,
            ICON_CHAT, ICON_MAIL, ICON_NOTES, ICON_CALENDAR, ICON_MUSIC, ICON_GALLERY
        };
        
        for (String icon : icons) {
            loadIcon(icon);
        }
    }
    
    /**
     * 加载单个图标纹理
     */
    private static void loadIcon(String name) {
        ResourceLocation location = new ResourceLocation(MPhoneMod.MODID, "textures/apps/" + name + ".png");
        
        try {
            Minecraft mc = Minecraft.getMinecraft();
            // 检查纹理是否已加载
            if (mc.getTextureManager().getTexture(location) == null) {
                // 创建并加载纹理
                SimpleTexture texture = new SimpleTexture(location);
                mc.getTextureManager().loadTexture(location, texture);
            }
            ICON_CACHE.put(name, location);
        } catch (Exception e) {
            System.err.println("[MPhone] Failed to load icon: " + name + " - " + e.getMessage());
        }
    }
    
    /**
     * 获取图标纹理资源位置
     */
    public static ResourceLocation getIcon(String name) {
        return ICON_CACHE.get(name);
    }
    
    /**
     * 检查图标是否已加载
     */
    public static boolean isIconLoaded(String name) {
        ResourceLocation location = ICON_CACHE.get(name);
        if (location == null) return false;
        
        try {
            return Minecraft.getMinecraft().getTextureManager().getTexture(location) != null;
        } catch (Exception e) {
            return false;
        }
    }
}
