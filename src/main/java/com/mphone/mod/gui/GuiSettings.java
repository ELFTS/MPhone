package com.mphone.mod.gui;

import com.mphone.mod.MPhoneMod;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

public class GuiSettings extends GuiScreen {
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(MPhoneMod.MODID, "textures/gui/phone_gui.png");

    // 手机尺寸
    private static final int PHONE_WIDTH = 120;
    private static final int PHONE_HEIGHT = 220;

    // 手机屏幕区域
    private static final int SCREEN_X = 4;
    private static final int SCREEN_Y = 12;
    private static final int SCREEN_WIDTH = 112;
    private static final int SCREEN_HEIGHT = 196;

    // 按钮ID
    private static final int BTN_BACK = 0;
    private static final int BTN_SOUND = 1;
    private static final int BTN_NOTIFICATION = 2;
    private static final int BTN_WALLPAPER = 3;
    private static final int BTN_ABOUT = 4;

    private final InventoryPlayer playerInventory;

    private int guiLeft;
    private int guiTop;
    private int screenLeft;
    private int screenTop;

    // 设置状态
    private boolean soundEnabled = true;
    private boolean notificationEnabled = true;
    private int wallpaperIndex = 0;
    private final String[] wallpapers = {"默认", "蓝色", "绿色", "紫色", "暗色"};
    
    // 壁纸颜色
    private final int[] wallpaperColors = {
        0xFF2E7D32, // 默认 - 绿色
        0xFF1976D2, // 蓝色
        0xFF388E3C, // 绿色
        0xFF7B1FA2, // 紫色
        0xFF424242  // 暗色
    };

    public GuiSettings(InventoryPlayer playerInventory) {
        this.playerInventory = playerInventory;
    }

    @Override
    public void initGui() {
        super.initGui();

        guiLeft = (this.width - PHONE_WIDTH) / 2;
        guiTop = (this.height - PHONE_HEIGHT) / 2;
        screenLeft = guiLeft + SCREEN_X;
        screenTop = guiTop + SCREEN_Y;

        int btnY = screenTop + 25;
        int btnSpacing = 28;

        // 返回按钮
        this.buttonList.add(new GuiButton(BTN_BACK, screenLeft + 2, screenTop + 5, 20, 12, "←"));

        // 设置选项按钮
        this.buttonList.add(new GuiButton(BTN_SOUND, screenLeft + SCREEN_WIDTH - 50, btnY, 46, 18, getSoundText()));
        this.buttonList.add(new GuiButton(BTN_NOTIFICATION, screenLeft + SCREEN_WIDTH - 50, btnY + btnSpacing, 46, 18, getNotificationText()));
        this.buttonList.add(new GuiButton(BTN_WALLPAPER, screenLeft + SCREEN_WIDTH - 50, btnY + btnSpacing * 2, 46, 18, getWallpaperText()));
        this.buttonList.add(new GuiButton(BTN_ABOUT, screenLeft + (SCREEN_WIDTH - 60) / 2, btnY + btnSpacing * 4, 60, 18, "关于手机"));
    }

    private String getSoundText() {
        return soundEnabled ? "开启" : "关闭";
    }

    private String getNotificationText() {
        return notificationEnabled ? "开启" : "关闭";
    }

    private String getWallpaperText() {
        return wallpapers[wallpaperIndex];
    }

    public int getCurrentWallpaperColor() {
        return wallpaperColors[wallpaperIndex];
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(GUI_TEXTURE);

        // 绘制手机纹理
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, PHONE_WIDTH, PHONE_HEIGHT);

        // 绘制壁纸预览
        drawWallpaperPreview();

        // 绘制标题
        String title = "设置";
        int titleWidth = this.fontRenderer.getStringWidth(title);
        this.fontRenderer.drawString(title, screenLeft + (SCREEN_WIDTH - titleWidth) / 2, screenTop + 6, 0xFFFFFF);

        // 绘制设置选项
        drawSettingsList();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawWallpaperPreview() {
        // 在屏幕顶部绘制当前壁纸颜色预览
        int previewX = screenLeft + SCREEN_WIDTH - 20;
        int previewY = screenTop + 6;
        int size = 10;
        
        // 绘制壁纸颜色方块
        drawRect(previewX, previewY, previewX + size, previewY + size, wallpaperColors[wallpaperIndex]);
        // 绘制边框
        drawRect(previewX - 1, previewY - 1, previewX, previewY + size + 1, 0xFFFFFFFF);
        drawRect(previewX + size, previewY - 1, previewX + size + 1, previewY + size + 1, 0xFFFFFFFF);
        drawRect(previewX, previewY - 1, previewX + size, previewY, 0xFFFFFFFF);
        drawRect(previewX, previewY + size, previewX + size, previewY + size + 1, 0xFFFFFFFF);
    }

    private void drawSettingsList() {
        int startY = screenTop + 30;
        int lineHeight = 28;
        int labelX = screenLeft + 8;

        // 声音
        drawSettingItem(labelX, startY, "声音", soundEnabled ? 0x4CAF50 : 0xFF5555);

        // 通知
        drawSettingItem(labelX, startY + lineHeight, "通知", notificationEnabled ? 0x4CAF50 : 0xFF5555);

        // 壁纸
        drawSettingItem(labelX, startY + lineHeight * 2, "壁纸", 0xFFFFFF);

        // 分隔线
        drawRect(screenLeft + 4, startY + lineHeight * 3 + 10, screenLeft + SCREEN_WIDTH - 4, startY + lineHeight * 3 + 11, 0x33FFFFFF);

        // 关于信息
        String version = "版本: 1.0.0";
        String mcVersion = "MC: 1.12.2";
        String author = "作者: MPhone Team";

        int infoY = startY + lineHeight * 4 + 10;
        this.fontRenderer.drawString(version, screenLeft + (SCREEN_WIDTH - this.fontRenderer.getStringWidth(version)) / 2, infoY, 0xAAAAAA);
        this.fontRenderer.drawString(mcVersion, screenLeft + (SCREEN_WIDTH - this.fontRenderer.getStringWidth(mcVersion)) / 2, infoY + 12, 0xAAAAAA);
        this.fontRenderer.drawString(author, screenLeft + (SCREEN_WIDTH - this.fontRenderer.getStringWidth(author)) / 2, infoY + 24, 0xAAAAAA);
    }

    private void drawSettingItem(int x, int y, String label, int statusColor) {
        // 绘制标签
        this.fontRenderer.drawString(label, x, y + 4, 0xFFFFFF);

        // 绘制状态指示点
        if (statusColor != 0xFFFFFF) {
            drawRect(x + this.fontRenderer.getStringWidth(label) + 4, y + 7, x + this.fontRenderer.getStringWidth(label) + 8, y + 11, statusColor);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case BTN_BACK:
                // 返回主界面，清除当前应用状态
                net.minecraft.item.ItemStack heldItem = mc.player.getHeldItemMainhand();
                if (heldItem.getItem() instanceof com.mphone.mod.items.ItemPhone) {
                    com.mphone.mod.items.ItemPhone.clearCurrentApp(heldItem);
                }
                this.mc.displayGuiScreen(new GuiPhone(playerInventory));
                return;
            case BTN_SOUND:
                soundEnabled = !soundEnabled;
                button.displayString = getSoundText();
                mc.player.sendMessage(new net.minecraft.util.text.TextComponentString(
                    "§a[MPhone] §f声音已" + (soundEnabled ? "开启" : "关闭")));
                break;
            case BTN_NOTIFICATION:
                notificationEnabled = !notificationEnabled;
                button.displayString = getNotificationText();
                mc.player.sendMessage(new net.minecraft.util.text.TextComponentString(
                    "§a[MPhone] §f通知已" + (notificationEnabled ? "开启" : "关闭")));
                break;
            case BTN_WALLPAPER:
                wallpaperIndex = (wallpaperIndex + 1) % wallpapers.length;
                button.displayString = getWallpaperText();
                mc.player.sendMessage(new net.minecraft.util.text.TextComponentString(
                    "§a[MPhone] §f壁纸已更换为 " + wallpapers[wallpaperIndex]));
                break;
            case BTN_ABOUT:
                mc.player.sendMessage(new net.minecraft.util.text.TextComponentString("§a[MPhone] §fMPhone 手机模组 v1.0.0"));
                mc.player.sendMessage(new net.minecraft.util.text.TextComponentString("§a[MPhone] §f支持 Minecraft 1.12.2"));
                mc.player.sendMessage(new net.minecraft.util.text.TextComponentString("§a[MPhone] §f功能: 相机、应用商店、设置"));
                break;
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
