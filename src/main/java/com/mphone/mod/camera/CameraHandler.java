package com.mphone.mod.camera;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

@SideOnly(Side.CLIENT)
public class CameraHandler {
    
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    
    /**
     * 拍摄照片并保存
     */
    public static void takePhoto() {
        try {
            // 确保当前没有打开GUI
            if (mc.currentScreen != null) {
                mc.displayGuiScreen(null);
                // 等待一帧确保GUI关闭
                Thread.sleep(50);
            }
            
            // 获取当前时间作为文件名
            String timestamp = dateFormat.format(new Date());
            String fileName = "MPhone_Photo_" + timestamp + ".png";
            
            // 创建截图目录
            File screenshotDir = new File("screenshots/mphone");
            if (!screenshotDir.exists()) {
                screenshotDir.mkdirs();
            }
            
            File screenshotFile = new File(screenshotDir, fileName);
            
            // 获取屏幕尺寸
            int width = mc.displayWidth;
            int height = mc.displayHeight;
            
            // 读取屏幕像素
            BufferedImage screenshot = captureScreenshot(width, height);
            
            // 添加水印
            screenshot = addWatermark(screenshot);
            
            // 保存图片
            ImageIO.write(screenshot, "PNG", screenshotFile);
            
            // 发送成功消息
            if (mc.player != null) {
                mc.player.sendMessage(new TextComponentString("§a[MPhone] §f照片已保存: " + fileName));
            }
            
        } catch (Exception e) {
            if (mc.player != null) {
                mc.player.sendMessage(new TextComponentString("§c[MPhone] §f拍照失败: " + e.getMessage()));
            }
            e.printStackTrace();
        }
    }
    
    /**
     * 捕获屏幕截图 - 使用RGB格式避免透明问题
     */
    private static BufferedImage captureScreenshot(int width, int height) {
        // 创建字节缓冲区 (RGB = 3 bytes per pixel)
        ByteBuffer pixelBuffer = BufferUtils.createByteBuffer(width * height * 3);
        
        // 设置像素存储模式
        GlStateManager.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
        GlStateManager.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        
        // 绑定帧缓冲区
        Framebuffer framebuffer = mc.getFramebuffer();
        framebuffer.bindFramebuffer(true);
        
        // 读取像素 (使用RGB格式，不包含Alpha)
        GL11.glReadPixels(0, 0, width, height, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, pixelBuffer);
        
        // 创建BufferedImage (使用RGB格式，不透明)
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        // 将缓冲区数据复制到图像 (需要翻转Y轴)
        byte[] pixels = new byte[width * height * 3];
        pixelBuffer.get(pixels);
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int srcIndex = (y * width + x) * 3;
                // 翻转Y轴 (OpenGL坐标系原点在左下角，Java在左上角)
                int destY = height - 1 - y;
                
                // RGB -> int
                int r = pixels[srcIndex] & 0xFF;
                int g = pixels[srcIndex + 1] & 0xFF;
                int b = pixels[srcIndex + 2] & 0xFF;
                int rgb = (r << 16) | (g << 8) | b;
                
                image.setRGB(x, destY, rgb);
            }
        }
        
        return image;
    }
    
    /**
     * 添加水印到照片
     */
    public static BufferedImage addWatermark(BufferedImage image) {
        // 创建新的BufferedImage (使用RGB格式)
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        result.getGraphics().drawImage(image, 0, 0, null);
        
        // 添加文字水印
        java.awt.Graphics2D g2d = result.createGraphics();
        g2d.setColor(java.awt.Color.WHITE);
        g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 20));
        
        String watermark = "MPhone Camera";
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        
        // 绘制水印在右下角
        int x = image.getWidth() - 200;
        int y = image.getHeight() - 40;
        
        g2d.drawString(watermark, x, y);
        g2d.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
        g2d.drawString(timestamp, x, y + 20);
        
        g2d.dispose();
        return result;
    }
}
