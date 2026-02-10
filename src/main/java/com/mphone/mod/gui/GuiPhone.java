package com.mphone.mod.gui;

import com.mphone.mod.MPhoneMod;
import com.mphone.mod.appstore.AppStoreItem;
import com.mphone.mod.appstore.AppStoreManager;
import com.mphone.mod.blocks.BlockBaseStation;
import com.mphone.mod.camera.CameraHandler;
import com.mphone.mod.items.ItemPhone;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuiPhone extends GuiScreen {
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(MPhoneMod.MODID, "textures/gui/phone_gui.png");

    // 手机尺寸
    private static final int PHONE_WIDTH = 120;
    private static final int PHONE_HEIGHT = 220;

    // 手机屏幕区域
    private static final int SCREEN_X = 4;
    private static final int SCREEN_Y = 12;
    private static final int SCREEN_WIDTH = 112;
    private static final int SCREEN_HEIGHT = 196;

    // 应用按钮配置
    private static final int BTN_WIDTH = 30;
    private static final int BTN_HEIGHT = 30;
    private static final int BTN_MARGIN_X = 8;
    private static final int BTN_MARGIN_Y = 4;
    private static final int GRID_COLS = 3;
    private static final int GRID_ROWS = 5;
    private static final int APPS_PER_PAGE = 15;

    // 按钮ID
    private static final int BTN_PREV_PAGE = 100;
    private static final int BTN_NEXT_PAGE = 101;

    private final InventoryPlayer playerInventory;
    private List<AppDisplayInfo> displayApps;
    private int currentPage = 0;

    private int guiLeft;
    private int guiTop;
    private int screenLeft;
    private int screenTop;

    // 系统应用信息
    private static class SystemAppInfo {
        final String name;
        final int iconColor;
        final int iconChar;
        final int guiId;

        SystemAppInfo(String name, int iconColor, int iconChar, int guiId) {
            this.name = name;
            this.iconColor = iconColor;
            this.iconChar = iconChar;
            this.guiId = guiId;
        }
    }

    // 显示的应用信息（系统应用或已安装应用）
    private static class AppDisplayInfo {
        final String name;
        final int iconColor;
        final int iconChar;
        final int guiId;
        final boolean isSystemApp;
        final String appId; // 对于应用商店应用

        AppDisplayInfo(String name, int iconColor, int iconChar, int guiId, boolean isSystemApp, String appId) {
            this.name = name;
            this.iconColor = iconColor;
            this.iconChar = iconChar;
            this.guiId = guiId;
            this.isSystemApp = isSystemApp;
            this.appId = appId;
        }
    }

    private static final SystemAppInfo[] SYSTEM_APPS = {
        new SystemAppInfo("联系人", 0xFF2196F3, 0x263A, GuiHandler.GUI_CONTACTS),
        new SystemAppInfo("短信",   0xFF4CAF50, 0x2709, GuiHandler.GUI_SMS),
        new SystemAppInfo("相机",   0xFFFF5722, 0x25CF, -1), // 相机特殊处理
        new SystemAppInfo("设置",   0xFF9E9E9E, 0x2699, GuiHandler.GUI_SETTINGS),
        new SystemAppInfo("商店",   0xFFFF9800, 0x2605, GuiHandler.GUI_APP_STORE)
    };

    public GuiPhone(InventoryPlayer playerInventory) {
        this.playerInventory = playerInventory;
        this.displayApps = new ArrayList<>();
    }

    @Override
    public void initGui() {
        super.initGui();
        guiLeft = (this.width - PHONE_WIDTH) / 2;
        guiTop = (this.height - PHONE_HEIGHT) / 2;
        screenLeft = guiLeft + SCREEN_X;
        screenTop = guiTop + SCREEN_Y;

        // 加载应用列表
        loadApps();

        // 添加翻页按钮
        int btnY = screenTop + SCREEN_HEIGHT - 20;
        this.buttonList.add(new GuiButton(BTN_PREV_PAGE, screenLeft + 10, btnY, 20, 14, "<"));
        this.buttonList.add(new GuiButton(BTN_NEXT_PAGE, screenLeft + SCREEN_WIDTH - 30, btnY, 20, 14, ">"));

        updatePageButtons();
    }

    private void loadApps() {
        displayApps.clear();

        // 添加系统应用
        for (SystemAppInfo app : SYSTEM_APPS) {
            displayApps.add(new AppDisplayInfo(app.name, app.iconColor, app.iconChar, app.guiId, true, null));
        }

        // 添加已安装的应用商店应用
        ItemStack heldItem = mc.player.getHeldItemMainhand();
        if (heldItem.getItem() instanceof ItemPhone) {
            List<String> installedApps = ItemPhone.getInstalledApps(heldItem);
            for (String appId : installedApps) {
                AppStoreItem app = AppStoreManager.getInstance().getAppById(appId);
                if (app != null) {
                    displayApps.add(new AppDisplayInfo(
                        app.getName(),
                        app.getIconColor(),
                        app.getIconChar(),
                        app.getGuiId(),
                        false,
                        appId
                    ));
                }
            }
        }
    }

    private void updatePageButtons() {
        int totalPages = (displayApps.size() + APPS_PER_PAGE - 1) / APPS_PER_PAGE;
        if (totalPages <= 1) {
            // 只有一页时隐藏翻页按钮
            for (GuiButton btn : buttonList) {
                if (btn.id == BTN_PREV_PAGE || btn.id == BTN_NEXT_PAGE) {
                    btn.visible = false;
                }
            }
        } else {
            for (GuiButton btn : buttonList) {
                if (btn.id == BTN_PREV_PAGE) {
                    btn.visible = true;
                    btn.enabled = currentPage > 0;
                } else if (btn.id == BTN_NEXT_PAGE) {
                    btn.visible = true;
                    btn.enabled = currentPage < totalPages - 1;
                }
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(GUI_TEXTURE);

        // 绘制手机纹理
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, PHONE_WIDTH, PHONE_HEIGHT);

        // 绘制状态栏
        drawStatusBar(screenLeft, screenTop);

        // 绘制应用网格
        drawAppGrid(screenLeft, screenTop, mouseX, mouseY);

        // 绘制页码
        drawPageIndicator();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawStatusBar(int screenLeft, int screenTop) {
        int statusBarHeight = 14;
        drawRect(screenLeft, screenTop, screenLeft + SCREEN_WIDTH, screenTop + statusBarHeight, 0xFF1A1A1A);

        // 获取手持手机
        ItemStack heldItem = mc.player.getHeldItemMainhand();
        boolean hasSIM = heldItem.getItem() instanceof ItemPhone && ItemPhone.hasSIMCard(heldItem);

        // 计算信号强度
        int signalStrength = 0;
        if (hasSIM && mc.world != null) {
            signalStrength = BlockBaseStation.getSignalStrength(mc.world, mc.player.posX, mc.player.posY, mc.player.posZ);
        }

        int centerY = screenTop + (statusBarHeight - 8) / 2;

        // 左侧：信号强度
        String signalText = getSignalText(signalStrength, hasSIM);
        this.fontRenderer.drawString(signalText, screenLeft + 4, centerY, getSignalColor(signalStrength, hasSIM));

        // 中间：时间
        String timeString = getGameTimeString();
        int timeWidth = this.fontRenderer.getStringWidth(timeString);
        this.fontRenderer.drawString(timeString, screenLeft + (SCREEN_WIDTH - timeWidth) / 2, centerY, 0xFFFFFF);

        // 右侧：SIM卡状态
        String simText = hasSIM ? "●" : "○";
        int simColor = hasSIM ? 0x4CAF50 : 0x888888;
        int simWidth = this.fontRenderer.getStringWidth(simText);
        this.fontRenderer.drawString(simText, screenLeft + SCREEN_WIDTH - simWidth - 4, centerY, simColor);
    }

    private String getSignalText(int strength, boolean hasSIM) {
        if (!hasSIM) return "○○○○";
        switch (strength) {
            case 4: return "●●●●";
            case 3: return "●●●○";
            case 2: return "●●○○";
            case 1: return "●○○○";
            default: return "○○○○";
        }
    }

    private int getSignalColor(int strength, boolean hasSIM) {
        if (!hasSIM) return 0x888888;
        if (strength == 0) return 0xFF0000;
        if (strength <= 2) return 0xFF9800;
        return 0x4CAF50;
    }

    private void drawAppGrid(int screenLeft, int screenTop, int mouseX, int mouseY) {
        int totalGridWidth = GRID_COLS * BTN_WIDTH + (GRID_COLS - 1) * BTN_MARGIN_X;
        int startX = screenLeft + (SCREEN_WIDTH - totalGridWidth) / 2;
        int startY = screenTop + 20;

        int startIndex = currentPage * APPS_PER_PAGE;

        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLS; col++) {
                int index = startIndex + row * GRID_COLS + col;
                if (index >= displayApps.size()) continue;

                int bx = startX + col * (BTN_WIDTH + BTN_MARGIN_X);
                int by = startY + row * (BTN_HEIGHT + BTN_MARGIN_Y);
                AppDisplayInfo app = displayApps.get(index);

                boolean hovered = mouseX >= bx && mouseX < bx + BTN_WIDTH && mouseY >= by && mouseY < by + BTN_HEIGHT;
                drawAppButton(bx, by, BTN_WIDTH, BTN_HEIGHT, app, hovered);
            }
        }
    }

    private void drawAppButton(int x, int y, int width, int height, AppDisplayInfo app, boolean hovered) {
        int cornerRadius = 6;
        int baseColor = app.iconColor;
        int bgColor = hovered ? brightenColor(baseColor, 1.2f) : baseColor;
        int shadowColor = darkenColor(baseColor, 0.7f);

        drawRoundedRect(x, y, width, height, cornerRadius, bgColor);
        drawRoundedRectBottomShadow(x, y + height - 3, width, 3, cornerRadius, shadowColor);
        drawRoundedRectTopHighlight(x, y, width, 3, cornerRadius, 0x40FFFFFF);

        String icon = String.valueOf((char) app.iconChar);
        int iconX = x + (width - 16) / 2;
        int iconY = y + 4;

        GlStateManager.pushMatrix();
        GlStateManager.scale(1.2f, 1.2f, 1.0f);
        this.fontRenderer.drawString(icon, (int)((iconX + 2) / 1.2f), (int)(iconY / 1.2f), 0xFFFFFF);
        GlStateManager.popMatrix();

        int textWidth = this.fontRenderer.getStringWidth(app.name);
        int textX = x + (width - textWidth) / 2;
        int textY = y + height + 2;

        this.fontRenderer.drawString(app.name, textX + 1, textY + 1, 0x55000000);
        this.fontRenderer.drawString(app.name, textX, textY, 0xFFFFFF);
    }

    private void drawPageIndicator() {
        int totalPages = (displayApps.size() + APPS_PER_PAGE - 1) / APPS_PER_PAGE;
        if (totalPages <= 1) return;

        String pageStr = (currentPage + 1) + "/" + totalPages;
        int pageWidth = this.fontRenderer.getStringWidth(pageStr);
        int pageX = screenLeft + (SCREEN_WIDTH - pageWidth) / 2;
        int pageY = screenTop + SCREEN_HEIGHT - 16;

        this.fontRenderer.drawString(pageStr, pageX, pageY, 0xAAAAAA);
    }

    private void drawRoundedRect(int x, int y, int width, int height, int radius, int color) {
        drawRect(x + radius, y, x + width - radius, y + height, color);
        drawRect(x, y + radius, x + width, y + height - radius, color);

        drawCircleCorner(x + radius, y + radius, radius, color, 0);
        drawCircleCorner(x + width - radius, y + radius, radius, color, 1);
        drawCircleCorner(x + radius, y + height - radius, radius, color, 2);
        drawCircleCorner(x + width - radius, y + height - radius, radius, color, 3);
    }

    private void drawRoundedRectBottomShadow(int x, int y, int width, int height, int radius, int color) {
        drawRect(x + radius, y, x + width - radius, y + height, color);
        drawCircleCorner(x + radius, y + height, radius, color, 2);
        drawCircleCorner(x + width - radius, y + height, radius, color, 3);
    }

    private void drawRoundedRectTopHighlight(int x, int y, int width, int height, int radius, int color) {
        drawRect(x + radius, y, x + width - radius, y + height, color);
    }

    private void drawCircleCorner(int cx, int cy, int r, int color, int quadrant) {
        int steps = 3;
        for (int i = 0; i < steps; i++) {
            for (int j = 0; j < steps; j++) {
                int dx = i;
                int dy = j;
                if (quadrant == 0) { dx = -dx - 1; dy = -dy - 1; }
                else if (quadrant == 1) { dy = -dy - 1; }
                else if (quadrant == 2) { dx = -dx - 1; }

                if (dx * dx + dy * dy <= r * r) {
                    drawRect(cx + dx, cy + dy, cx + dx + 1, cy + dy + 1, color);
                }
            }
        }
    }

    private int brightenColor(int color, float factor) {
        int a = (color >> 24) & 0xFF;
        int r = Math.min(255, (int)(((color >> 16) & 0xFF) * factor));
        int g = Math.min(255, (int)(((color >> 8) & 0xFF) * factor));
        int b = Math.min(255, (int)((color & 0xFF) * factor));
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private int darkenColor(int color, float factor) {
        int a = (color >> 24) & 0xFF;
        int r = (int)(((color >> 16) & 0xFF) * factor);
        int g = (int)(((color >> 8) & 0xFF) * factor);
        int b = (int)((color & 0xFF) * factor);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private String getGameTimeString() {
        if (mc.world == null) return "12:00";

        long worldTime = mc.world.getWorldTime();
        int totalMinutes = (int) ((worldTime + 6000) % 24000 / 10);
        int hours = (totalMinutes / 60) % 24;
        int minutes = totalMinutes % 60;

        String ampm = hours >= 12 ? "PM" : "AM";
        hours = hours % 12;
        if (hours == 0) hours = 12;

        return String.format("%d:%02d %s", hours, minutes, ampm);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == BTN_PREV_PAGE && currentPage > 0) {
            currentPage--;
            updatePageButtons();
            return;
        }
        if (button.id == BTN_NEXT_PAGE) {
            int totalPages = (displayApps.size() + APPS_PER_PAGE - 1) / APPS_PER_PAGE;
            if (currentPage < totalPages - 1) {
                currentPage++;
                updatePageButtons();
            }
            return;
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseButton != 0) return;

        int totalGridWidth = GRID_COLS * BTN_WIDTH + (GRID_COLS - 1) * BTN_MARGIN_X;
        int startX = screenLeft + (SCREEN_WIDTH - totalGridWidth) / 2;
        int startY = screenTop + 20;

        int startIndex = currentPage * APPS_PER_PAGE;

        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLS; col++) {
                int index = startIndex + row * GRID_COLS + col;
                if (index >= displayApps.size()) continue;

                int bx = startX + col * (BTN_WIDTH + BTN_MARGIN_X);
                int by = startY + row * (BTN_HEIGHT + BTN_MARGIN_Y);

                if (isMouseOver(mouseX, mouseY, bx, by, BTN_WIDTH, BTN_HEIGHT)) {
                    handleAppClick(displayApps.get(index));
                    return;
                }
            }
        }
    }

    private void handleAppClick(AppDisplayInfo app) {
        // 相机特殊处理
        if (app.name.equals("相机")) {
            mc.player.sendMessage(new net.minecraft.util.text.TextComponentString("§a[MPhone] §f3秒后拍照，请准备好..."));
            mc.displayGuiScreen(null);
            new Thread(() -> {
                try {
                    for (int i = 3; i > 0; i--) {
                        Thread.sleep(1000);
                        final int count = i;
                        mc.addScheduledTask(() -> {
                            if (mc.player != null && count > 1) {
                                mc.player.sendMessage(new net.minecraft.util.text.TextComponentString("§a[MPhone] §f" + (count - 1) + "..."));
                            }
                        });
                    }
                    mc.addScheduledTask(() -> CameraHandler.takePhoto());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
            return;
        }

        // 检查是否是已实现的GUI
        if (app.guiId >= 100) {
            // 应用商店的应用尚未实现，显示提示
            mc.player.sendMessage(new net.minecraft.util.text.TextComponentString(
                "§e[MPhone] §f" + app.name + " 功能开发中..."));
            return;
        }

        // 保存当前应用并打开GUI
        ItemStack heldItem = mc.player.getHeldItemMainhand();
        if (heldItem.getItem() instanceof ItemPhone) {
            if (app.guiId >= 0) {
                ItemPhone.setCurrentApp(heldItem, app.guiId);
            }
        }

        // 打开对应GUI
        if (app.guiId >= 0) {
            mc.player.openGui(MPhoneMod.instance, app.guiId, mc.world, (int) mc.player.posX, (int) mc.player.posY, (int) mc.player.posZ);
        }
    }

    private boolean isMouseOver(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
