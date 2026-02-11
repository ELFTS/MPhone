package com.mphone.mod.gui;

import com.mphone.mod.MPhoneMod;
import com.mphone.mod.appstore.AppStoreItem;
import com.mphone.mod.appstore.AppStoreManager;
import com.mphone.mod.items.ItemPhone;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.util.List;

/**
 * 应用商店GUI
 * 注意：安装状态从手机NBT中读取，每个手机独立
 */
public class GuiAppStore extends GuiScreen {
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(MPhoneMod.MODID, "textures/gui/phone_gui.png");

    // 手机尺寸
    private static final int PHONE_WIDTH = 120;
    private static final int PHONE_HEIGHT = 220;

    // 手机屏幕区域
    private static final int SCREEN_X = 4;
    private static final int SCREEN_Y = 12;
    private static final int SCREEN_WIDTH = 112;
    private static final int SCREEN_HEIGHT = 196;

    private final InventoryPlayer playerInventory;
    private List<AppStoreItem> currentApps;
    private AppStoreItem.AppType currentFilter = null;
    private int scrollOffset = 0;
    private static final int APPS_PER_PAGE = 4;

    // 按钮ID
    private static final int BTN_BACK = 0;
    private static final int BTN_ALL = 1;
    private static final int BTN_UTILITY = 2;
    private static final int BTN_GAME = 3;
    private static final int BTN_SOCIAL = 4;
    private static final int BTN_PRODUCTIVITY = 5;
    private static final int BTN_ENTERTAINMENT = 6;
    private static final int BTN_SCROLL_UP = 7;
    private static final int BTN_SCROLL_DOWN = 8;
    private static final int BTN_APP_START = 10;

    private int guiLeft;
    private int guiTop;
    private int screenLeft;
    private int screenTop;

    public GuiAppStore(InventoryPlayer playerInventory) {
        this.playerInventory = playerInventory;
        this.currentApps = AppStoreManager.getInstance().getAllApps();
    }

    @Override
    public void initGui() {
        super.initGui();

        guiLeft = (this.width - PHONE_WIDTH) / 2;
        guiTop = (this.height - PHONE_HEIGHT) / 2;
        screenLeft = guiLeft + SCREEN_X;
        screenTop = guiTop + SCREEN_Y;

        int btnY = screenTop + 5;

        // 返回按钮
        this.buttonList.add(new GuiButton(BTN_BACK, screenLeft + 2, btnY, 20, 12, "←"));

        // 分类按钮
        int filterBtnX = screenLeft + 24;
        this.buttonList.add(new GuiButton(BTN_ALL, filterBtnX, btnY, 18, 12, "全"));
        this.buttonList.add(new GuiButton(BTN_UTILITY, filterBtnX + 19, btnY, 18, 12, "具"));
        this.buttonList.add(new GuiButton(BTN_GAME, filterBtnX + 38, btnY, 18, 12, "游"));
        this.buttonList.add(new GuiButton(BTN_SOCIAL, filterBtnX + 57, btnY, 18, 12, "社"));

        // 滚动按钮
        this.buttonList.add(new GuiButton(BTN_SCROLL_UP, screenLeft + SCREEN_WIDTH - 18, screenTop + 20, 16, 12, "↑"));
        this.buttonList.add(new GuiButton(BTN_SCROLL_DOWN, screenLeft + SCREEN_WIDTH - 18, screenTop + SCREEN_HEIGHT - 30, 16, 12, "↓"));

        updateAppButtons();
    }

    /**
     * 检查应用是否已安装（从手机NBT读取）
     */
    private boolean isAppInstalled(AppStoreItem app) {
        ItemStack heldItem = mc.player.getHeldItemMainhand();
        if (!(heldItem.getItem() instanceof ItemPhone)) {
            return false;
        }
        return ItemPhone.hasAppInstalled(heldItem, app.getId());
    }

    private void updateAppButtons() {
        // 清除之前的应用按钮
        this.buttonList.removeIf(btn -> btn.id >= BTN_APP_START);

        // 添加应用按钮
        int appStartY = screenTop + 35;
        int appHeight = 38;

        for (int i = 0; i < APPS_PER_PAGE; i++) {
            int appIndex = scrollOffset + i;
            if (appIndex >= currentApps.size()) break;

            int btnY = appStartY + i * appHeight;
            AppStoreItem app = currentApps.get(appIndex);

            // 安装/卸载按钮 - 从手机NBT检查安装状态
            String btnText = isAppInstalled(app) ? "卸载" : "安装";
            this.buttonList.add(new GuiButton(BTN_APP_START + appIndex, screenLeft + SCREEN_WIDTH - 40, btnY + 20, 36, 14, btnText));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(GUI_TEXTURE);

        // 绘制手机纹理
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, PHONE_WIDTH, PHONE_HEIGHT);

        // 绘制标题
        String title = "应用商店";
        int titleWidth = this.fontRenderer.getStringWidth(title);
        this.fontRenderer.drawString(title, screenLeft + (SCREEN_WIDTH - titleWidth) / 2, screenTop + 4, 0xFFFFFF);

        // 绘制应用列表
        drawAppList(mouseX, mouseY);

        // 绘制滚动指示器
        drawScrollIndicator();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawAppList(int mouseX, int mouseY) {
        int appStartY = screenTop + 35;
        int appHeight = 38;

        for (int i = 0; i < APPS_PER_PAGE; i++) {
            int appIndex = scrollOffset + i;
            if (appIndex >= currentApps.size()) break;

            int appY = appStartY + i * appHeight;
            AppStoreItem app = currentApps.get(appIndex);

            // 绘制应用项背景
            boolean hovered = mouseX >= screenLeft + 2 && mouseX < screenLeft + SCREEN_WIDTH - 2 &&
                    mouseY >= appY && mouseY < appY + appHeight - 2;
            int bgColor = hovered ? 0x55333333 : 0x33222222;
            drawRect(screenLeft + 2, appY, screenLeft + SCREEN_WIDTH - 2, appY + appHeight - 2, bgColor);

            // 绘制应用图标
            drawAppIcon(app, screenLeft + 6, appY + 4);

            // 绘制应用名称
            this.fontRenderer.drawString(app.getName(), screenLeft + 26, appY + 4, 0xFFFFFF);

            // 绘制应用类型
            String typeStr = app.getType().getDisplayName();
            this.fontRenderer.drawString(typeStr, screenLeft + 26, appY + 15, 0xAAAAAA);

            // 绘制状态 - 从手机NBT检查
            boolean installed = isAppInstalled(app);
            String statusStr = installed ? "已安装" : "未安装";
            int statusColor = installed ? 0x4CAF50 : 0xAAAAAA;
            this.fontRenderer.drawString(statusStr, screenLeft + 26, appY + 26, statusColor);
        }
    }

    private void drawAppIcon(AppStoreItem app, int x, int y) {
        int iconSize = 24;
        
        // 尝试从缓存获取纹理ID
        int textureId = com.mphone.mod.client.IconTextureCache.getTextureId(app.getId());
        
        if (textureId > 0) {
            // 使用缓存的纹理ID直接绘制
            drawIconWithTextureId(textureId, x, y, iconSize);
        } else {
            // 备用：使用颜色块
            drawRect(x, y, x + iconSize, y + iconSize, app.getIconColor());
        }
    }
    
    private void drawIconWithTextureId(int textureId, int x, int y, int iconSize) {
        // 保存当前状态
        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();
        
        // 绑定纹理 - 使用GL11直接绑定
        org.lwjgl.opengl.GL11.glBindTexture(org.lwjgl.opengl.GL11.GL_TEXTURE_2D, textureId);
        
        // 设置OpenGL状态
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        
        // 手动绘制四边形（使用GL_QUADS）
        float u = 0.0F, v = 0.0F;
        float u2 = 1.0F, v2 = 1.0F;
        
        net.minecraft.client.renderer.BufferBuilder buffer = net.minecraft.client.renderer.Tessellator.getInstance().getBuffer();
        buffer.begin(org.lwjgl.opengl.GL11.GL_QUADS, net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_TEX);
        
        buffer.pos(x, y + iconSize, 0).tex(u, v2).endVertex();
        buffer.pos(x + iconSize, y + iconSize, 0).tex(u2, v2).endVertex();
        buffer.pos(x + iconSize, y, 0).tex(u2, v).endVertex();
        buffer.pos(x, y, 0).tex(u, v).endVertex();
        
        net.minecraft.client.renderer.Tessellator.getInstance().draw();
        
        // 恢复状态
        GlStateManager.disableBlend();
        GlStateManager.popAttrib();
        GlStateManager.popMatrix();
    }

    private void drawScrollIndicator() {
        int totalPages = (currentApps.size() + APPS_PER_PAGE - 1) / APPS_PER_PAGE;
        int currentPage = scrollOffset / APPS_PER_PAGE + 1;

        if (totalPages > 1) {
            String pageStr = currentPage + "/" + totalPages;
            int pageWidth = this.fontRenderer.getStringWidth(pageStr);
            this.fontRenderer.drawString(pageStr, screenLeft + SCREEN_WIDTH - 20 - pageWidth / 2, screenTop + SCREEN_HEIGHT - 15, 0xAAAAAA);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case BTN_BACK:
                // 返回主界面，清除当前应用状态
                ItemStack heldItem = mc.player.getHeldItemMainhand();
                if (heldItem.getItem() instanceof ItemPhone) {
                    ItemPhone.clearCurrentApp(heldItem);
                }
                this.mc.displayGuiScreen(new GuiPhone(playerInventory));
                return;
            case BTN_ALL:
                currentFilter = null;
                currentApps = AppStoreManager.getInstance().getAllApps();
                scrollOffset = 0;
                break;
            case BTN_UTILITY:
                currentFilter = AppStoreItem.AppType.UTILITY;
                currentApps = AppStoreManager.getInstance().getAppsByType(AppStoreItem.AppType.UTILITY);
                scrollOffset = 0;
                break;
            case BTN_GAME:
                currentFilter = AppStoreItem.AppType.GAME;
                currentApps = AppStoreManager.getInstance().getAppsByType(AppStoreItem.AppType.GAME);
                scrollOffset = 0;
                break;
            case BTN_SOCIAL:
                currentFilter = AppStoreItem.AppType.SOCIAL;
                currentApps = AppStoreManager.getInstance().getAppsByType(AppStoreItem.AppType.SOCIAL);
                scrollOffset = 0;
                break;
            case BTN_SCROLL_UP:
                if (scrollOffset > 0) {
                    scrollOffset -= APPS_PER_PAGE;
                    if (scrollOffset < 0) scrollOffset = 0;
                }
                break;
            case BTN_SCROLL_DOWN:
                if (scrollOffset + APPS_PER_PAGE < currentApps.size()) {
                    scrollOffset += APPS_PER_PAGE;
                }
                break;
        }

        // 处理应用按钮点击
        if (button.id >= BTN_APP_START) {
            int appIndex = button.id - BTN_APP_START;
            if (appIndex >= 0 && appIndex < currentApps.size()) {
                handleAppAction(currentApps.get(appIndex));
            }
        }

        updateAppButtons();
    }

    private void handleAppAction(AppStoreItem app) {
        // 获取当前手持的手机
        ItemStack heldItem = mc.player.getHeldItemMainhand();
        if (!(heldItem.getItem() instanceof ItemPhone)) {
            mc.player.sendMessage(new net.minecraft.util.text.TextComponentString(
                "§c[MPhone] §f请手持手机进行操作"));
            return;
        }

        // 检查当前安装状态（从手机NBT）
        boolean currentlyInstalled = ItemPhone.hasAppInstalled(heldItem, app.getId());

        if (currentlyInstalled) {
            // 从手机NBT中卸载应用
            ItemPhone.uninstallApp(heldItem, app.getId());
            mc.player.sendMessage(new net.minecraft.util.text.TextComponentString(
                "§a[MPhone] §f已卸载 " + app.getName()));
        } else {
            // 安装应用到手机NBT
            ItemPhone.installApp(heldItem, app.getId());
            mc.player.sendMessage(new net.minecraft.util.text.TextComponentString(
                "§a[MPhone] §f已安装 " + app.getName()));
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
