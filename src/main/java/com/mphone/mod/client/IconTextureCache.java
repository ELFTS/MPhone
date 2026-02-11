package com.mphone.mod.client;

import com.mphone.mod.MPhoneMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 图标纹理缓存
 * 在客户端启动时预加载所有应用图标纹理
 */
@SideOnly(Side.CLIENT)
public class IconTextureCache {
    private static final Map<String, Integer> TEXTURE_CACHE = new HashMap<>();
    
    // 所有应用图标名称
    public static final String[] ICON_NAMES = {
        "contacts", "sms", "camera", "settings", "appstore",
        "compass", "clock", "calculator", "snake", "tetris",
        "chat", "mail", "notes", "calendar", "music", "gallery"
    };
    
    /**
     * 初始化并预加载所有图标纹理
     */
    public static void init() {
        Minecraft mc = Minecraft.getMinecraft();
        
        for (String name : ICON_NAMES) {
            loadTexture(mc, name);
        }
    }
    
    /**
     * 加载单个纹理
     */
    private static void loadTexture(Minecraft mc, String name) {
        try {
            ResourceLocation location = new ResourceLocation(MPhoneMod.MODID, "textures/apps/" + name + ".png");
            
            // 读取图像
            java.awt.image.BufferedImage image = TextureUtil.readBufferedImage(
                mc.getResourceManager().getResource(location).getInputStream()
            );
            
            if (image != null) {
                // 生成纹理ID并上传
                int textureId = TextureUtil.glGenTextures();
                TextureUtil.uploadTextureImage(textureId, image);
                TEXTURE_CACHE.put(name, textureId);
                
                System.out.println("[MPhone] Loaded icon texture: " + name + " (ID: " + textureId + ")");
            }
        } catch (IOException e) {
            System.err.println("[MPhone] Failed to load icon: " + name + " - " + e.getMessage());
        }
    }
    
    /**
     * 获取纹理ID
     */
    public static int getTextureId(String name) {
        return TEXTURE_CACHE.getOrDefault(name, -1);
    }
    
    /**
     * 检查纹理是否已加载
     */
    public static boolean hasTexture(String name) {
        return TEXTURE_CACHE.containsKey(name);
    }
    
    /**
     * 从ResourceLocation获取纹理ID
     */
    public static int getTextureId(ResourceLocation location) {
        if (location == null) return -1;
        
        // 1.12.2 中使用 toString() 获取完整路径
        String fullPath = location.toString();
        // 格式: modid:path
        String prefix = MPhoneMod.MODID + ":textures/apps/";
        if (fullPath.startsWith(prefix) && fullPath.endsWith(".png")) {
            String name = fullPath.substring(prefix.length(), fullPath.length() - 4);
            return getTextureId(name);
        }
        return -1;
    }
}
