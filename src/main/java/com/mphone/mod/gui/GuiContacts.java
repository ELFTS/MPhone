package com.mphone.mod.gui;

import com.mphone.mod.MPhoneMod;
import com.mphone.mod.items.ItemPhone;
import com.mphone.mod.sms.SMSManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiContacts extends GuiScreen {
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
    private static final int BTN_NEW_CONTACT = 1;
    private static final int BTN_SAVE = 2;
    private static final int BTN_DELETE = 3;
    private static final int BTN_EDIT = 4;
    private static final int BTN_SEND_SMS = 5;
    private static final int BTN_CONTACT_START = 10;

    private final InventoryPlayer playerInventory;

    private int guiLeft;
    private int guiTop;
    private int screenLeft;
    private int screenTop;

    // 界面模式
    private enum Mode {
        CONTACT_LIST,   // 联系人列表
        CONTACT_VIEW,   // 查看联系人
        CONTACT_EDIT,   // 编辑/新建联系人
        CONTACT_DELETE  // 删除确认
    }
    private Mode currentMode = Mode.CONTACT_LIST;

    // 当前选中的联系人
    private String currentPhoneNumber = null;
    private String currentContactName = null;

    // 输入框
    private GuiTextField nameInput;
    private GuiTextField phoneInput;

    // 滚动偏移
    private int scrollOffset = 0;
    private static final int ITEMS_PER_PAGE = 6;

    // 当前手机号
    private String myPhoneNumber = null;

    public GuiContacts(InventoryPlayer playerInventory) {
        this.playerInventory = playerInventory;
    }

    @Override
    public void initGui() {
        super.initGui();

        guiLeft = (this.width - PHONE_WIDTH) / 2;
        guiTop = (this.height - PHONE_HEIGHT) / 2;
        screenLeft = guiLeft + SCREEN_X;
        screenTop = guiTop + SCREEN_Y;

        // 获取当前手机号
        if (mc.player != null) {
            ItemStack heldItem = mc.player.getHeldItemMainhand();
            if (heldItem.getItem() instanceof ItemPhone) {
                myPhoneNumber = ItemPhone.getSIMNumber(heldItem);
            }
        }

        updateButtons();
    }

    private void updateButtons() {
        this.buttonList.clear();

        // 返回按钮
        this.buttonList.add(new GuiButton(BTN_BACK, screenLeft + 2, screenTop + 5, 20, 12, "←"));

        if (currentMode == Mode.CONTACT_LIST) {
            // 新建联系人按钮
            this.buttonList.add(new GuiButton(BTN_NEW_CONTACT, screenLeft + SCREEN_WIDTH - 40, screenTop + 5, 38, 12, "新建"));

            // 联系人按钮 - 使用当前手机的联系人列表
            Map<String, String> contacts = myPhoneNumber != null ? 
                SMSManager.getInstance().getAllContacts(myPhoneNumber) : new HashMap<>();
            List<String> phoneNumbers = new ArrayList<>(contacts.keySet());

            int startY = screenTop + 30;
            int itemHeight = 26;

            for (int i = 0; i < ITEMS_PER_PAGE; i++) {
                int contactIndex = scrollOffset + i;
                if (contactIndex >= phoneNumbers.size()) break;

                this.buttonList.add(new GuiButton(BTN_CONTACT_START + contactIndex, screenLeft + SCREEN_WIDTH - 30, startY + i * itemHeight + 6, 28, 12, "查看"));
            }
        } else if (currentMode == Mode.CONTACT_VIEW) {
            // 发短信按钮
            this.buttonList.add(new GuiButton(BTN_SEND_SMS, screenLeft + 10, screenTop + SCREEN_HEIGHT - 60, 92, 14, "发短信"));
            // 编辑按钮
            this.buttonList.add(new GuiButton(BTN_EDIT, screenLeft + 10, screenTop + SCREEN_HEIGHT - 40, 44, 14, "编辑"));
            // 删除按钮
            this.buttonList.add(new GuiButton(BTN_DELETE, screenLeft + SCREEN_WIDTH - 54, screenTop + SCREEN_HEIGHT - 40, 44, 14, "删除"));
        } else if (currentMode == Mode.CONTACT_EDIT) {
            // 保存按钮
            this.buttonList.add(new GuiButton(BTN_SAVE, screenLeft + SCREEN_WIDTH - 35, screenTop + 5, 33, 12, "保存"));

            // 输入框
            nameInput = new GuiTextField(0, this.fontRenderer, screenLeft + 50, screenTop + 50, 56, 12);
            nameInput.setMaxStringLength(20);
            nameInput.setText(currentContactName != null ? currentContactName : "");

            phoneInput = new GuiTextField(1, this.fontRenderer, screenLeft + 50, screenTop + 70, 56, 12);
            phoneInput.setMaxStringLength(6);
            phoneInput.setText(currentPhoneNumber != null ? currentPhoneNumber : "");
            
            // 新建模式下允许编辑手机号，编辑模式下不允许
            if (currentMode == Mode.CONTACT_EDIT && currentPhoneNumber != null) {
                phoneInput.setEnabled(false);
            } else {
                phoneInput.setEnabled(true);
            }
        } else if (currentMode == Mode.CONTACT_DELETE) {
            // 确认删除按钮
            this.buttonList.add(new GuiButton(BTN_DELETE, screenLeft + 10, screenTop + SCREEN_HEIGHT - 40, 40, 14, "确认"));
            // 取消按钮
            this.buttonList.add(new GuiButton(BTN_BACK, screenLeft + SCREEN_WIDTH - 50, screenTop + SCREEN_HEIGHT - 40, 40, 14, "取消"));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(GUI_TEXTURE);

        // 绘制手机纹理
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, PHONE_WIDTH, PHONE_HEIGHT);

        // 根据模式绘制不同内容
        switch (currentMode) {
            case CONTACT_LIST:
                drawContactList(mouseX, mouseY);
                break;
            case CONTACT_VIEW:
                drawContactView(mouseX, mouseY);
                break;
            case CONTACT_EDIT:
                drawContactEdit(mouseX, mouseY);
                break;
            case CONTACT_DELETE:
                drawContactDelete(mouseX, mouseY);
                break;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawContactList(int mouseX, int mouseY) {
        // 标题
        String title = "联系人";
        int titleWidth = this.fontRenderer.getStringWidth(title);
        this.fontRenderer.drawString(title, screenLeft + (SCREEN_WIDTH - titleWidth) / 2, screenTop + 6, 0xFFFFFF);

        // 获取联系人列表 - 使用当前手机的联系人
        Map<String, String> contacts = myPhoneNumber != null ? 
            SMSManager.getInstance().getAllContacts(myPhoneNumber) : new HashMap<>();

        if (contacts.isEmpty()) {
            String msg = "暂无联系人";
            int msgWidth = this.fontRenderer.getStringWidth(msg);
            this.fontRenderer.drawString(msg, screenLeft + (SCREEN_WIDTH - msgWidth) / 2, screenTop + 80, 0xAAAAAA);
            
            String hint = "点击\"新建\"添加";
            int hintWidth = this.fontRenderer.getStringWidth(hint);
            this.fontRenderer.drawString(hint, screenLeft + (SCREEN_WIDTH - hintWidth) / 2, screenTop + 95, 0x888888);
            return;
        }

        // 绘制联系人列表
        int startY = screenTop + 30;
        int itemHeight = 26;
        int index = 0;

        List<Map.Entry<String, String>> contactList = new ArrayList<>(contacts.entrySet());

        for (Map.Entry<String, String> entry : contactList) {
            if (index < scrollOffset) {
                index++;
                continue;
            }
            if (index >= scrollOffset + ITEMS_PER_PAGE) break;

            String phone = entry.getKey();
            String name = entry.getValue();
            int y = startY + (index - scrollOffset) * itemHeight;

            // 背景
            boolean hovered = mouseX >= screenLeft + 2 && mouseX < screenLeft + SCREEN_WIDTH - 2 &&
                    mouseY >= y && mouseY < y + itemHeight - 2;
            int bgColor = hovered ? 0x55333333 : 0x33222222;
            drawRect(screenLeft + 2, y, screenLeft + SCREEN_WIDTH - 2, y + itemHeight - 2, bgColor);

            // 联系人头像（圆形背景）
            int avatarColor = getAvatarColor(name);
            drawRect(screenLeft + 8, y + 3, screenLeft + 20, y + 15, avatarColor);
            // 头像文字（名字首字）
            String initial = name.isEmpty() ? "?" : name.substring(0, 1);
            int initialWidth = this.fontRenderer.getStringWidth(initial);
            this.fontRenderer.drawString(initial, screenLeft + 14 - initialWidth / 2, y + 5, 0xFFFFFF);

            // 联系人名称
            this.fontRenderer.drawString(name, screenLeft + 26, y + 4, 0xFFFFFF);

            // 电话号码
            this.fontRenderer.drawString(phone, screenLeft + 26, y + 14, 0x888888);

            index++;
        }
    }

    private void drawContactView(int mouseX, int mouseY) {
        if (currentPhoneNumber == null || currentContactName == null) return;

        // 标题
        String title = "联系人详情";
        int titleWidth = this.fontRenderer.getStringWidth(title);
        this.fontRenderer.drawString(title, screenLeft + (SCREEN_WIDTH - titleWidth) / 2, screenTop + 6, 0xFFFFFF);

        // 大头像
        int avatarY = screenTop + 35;
        int avatarSize = 40;
        int avatarX = screenLeft + (SCREEN_WIDTH - avatarSize) / 2;
        int avatarColor = getAvatarColor(currentContactName);
        
        // 头像背景
        drawRect(avatarX, avatarY, avatarX + avatarSize, avatarY + avatarSize, avatarColor);
        
        // 头像文字
        String initial = currentContactName.isEmpty() ? "?" : currentContactName.substring(0, 1);
        int initialWidth = this.fontRenderer.getStringWidth(initial);
        this.fontRenderer.drawString(initial, avatarX + (avatarSize - initialWidth) / 2, avatarY + 14, 0xFFFFFF);

        // 联系人名称
        int nameY = avatarY + avatarSize + 10;
        int nameWidth = this.fontRenderer.getStringWidth(currentContactName);
        this.fontRenderer.drawString(currentContactName, screenLeft + (SCREEN_WIDTH - nameWidth) / 2, nameY, 0xFFFFFF);

        // 电话号码
        int phoneY = nameY + 15;
        int phoneWidth = this.fontRenderer.getStringWidth(currentPhoneNumber);
        this.fontRenderer.drawString(currentPhoneNumber, screenLeft + (SCREEN_WIDTH - phoneWidth) / 2, phoneY, 0x888888);

        // 信息标签
        int infoY = phoneY + 25;
        drawRect(screenLeft + 8, infoY, screenLeft + SCREEN_WIDTH - 8, infoY + 1, 0xFF444444);
        
        this.fontRenderer.drawString("号码信息", screenLeft + 8, infoY + 8, 0xAAAAAA);
        this.fontRenderer.drawString("类型: 手机", screenLeft + 8, infoY + 22, 0x888888);
    }

    private void drawContactEdit(int mouseX, int mouseY) {
        // 标题
        String title = currentPhoneNumber == null ? "新建联系人" : "编辑联系人";
        int titleWidth = this.fontRenderer.getStringWidth(title);
        this.fontRenderer.drawString(title, screenLeft + (SCREEN_WIDTH - titleWidth) / 2, screenTop + 6, 0xFFFFFF);

        // 标签
        this.fontRenderer.drawString("姓名:", screenLeft + 8, screenTop + 52, 0xAAAAAA);
        this.fontRenderer.drawString("电话:", screenLeft + 8, screenTop + 72, 0xAAAAAA);

        // 绘制输入框
        if (nameInput != null) {
            nameInput.drawTextBox();
        }
        if (phoneInput != null) {
            phoneInput.drawTextBox();
        }

        // 提示文字
        if (currentPhoneNumber == null) {
            String hint = "请输入6位电话号码";
            int hintWidth = this.fontRenderer.getStringWidth(hint);
            this.fontRenderer.drawString(hint, screenLeft + (SCREEN_WIDTH - hintWidth) / 2, screenTop + 100, 0x666666);
        }
    }

    private void drawContactDelete(int mouseX, int mouseY) {
        // 标题
        String title = "删除联系人";
        int titleWidth = this.fontRenderer.getStringWidth(title);
        this.fontRenderer.drawString(title, screenLeft + (SCREEN_WIDTH - titleWidth) / 2, screenTop + 6, 0xFFFFFF);

        // 警告图标
        int iconY = screenTop + 50;
        String warning = "!";
        int warningWidth = this.fontRenderer.getStringWidth(warning);
        this.fontRenderer.drawString(warning, screenLeft + (SCREEN_WIDTH - warningWidth) / 2, iconY, 0xFFFF0000);

        // 确认文字
        String confirm = "确定要删除吗?";
        int confirmWidth = this.fontRenderer.getStringWidth(confirm);
        this.fontRenderer.drawString(confirm, screenLeft + (SCREEN_WIDTH - confirmWidth) / 2, iconY + 25, 0xFFFFFF);

        if (currentContactName != null) {
            int nameWidth = this.fontRenderer.getStringWidth(currentContactName);
            this.fontRenderer.drawString(currentContactName, screenLeft + (SCREEN_WIDTH - nameWidth) / 2, iconY + 40, 0xAAAAAA);
        }

        // 提示
        String hint = "此操作不可撤销";
        int hintWidth = this.fontRenderer.getStringWidth(hint);
        this.fontRenderer.drawString(hint, screenLeft + (SCREEN_WIDTH - hintWidth) / 2, iconY + 60, 0x888888);
    }

    private int getAvatarColor(String name) {
        if (name == null || name.isEmpty()) {
            return 0xFF2196F3;
        }
        // 根据名字生成颜色
        int hash = name.hashCode();
        int[] colors = {
            0xFF2196F3, // 蓝
            0xFF4CAF50, // 绿
            0xFFFF5722, // 橙
            0xFF9C27B0, // 紫
            0xFFFF9800, // 黄
            0xFFE91E63, // 粉
            0xFF00BCD4, // 青
            0xFF795548  // 棕
        };
        return colors[Math.abs(hash) % colors.length];
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case BTN_BACK:
                if (currentMode == Mode.CONTACT_LIST) {
                    // 返回主界面，清除当前应用状态
                    ItemStack heldItem = mc.player.getHeldItemMainhand();
                    if (heldItem.getItem() instanceof com.mphone.mod.items.ItemPhone) {
                        com.mphone.mod.items.ItemPhone.clearCurrentApp(heldItem);
                    }
                    this.mc.displayGuiScreen(new GuiPhone(playerInventory));
                } else if (currentMode == Mode.CONTACT_DELETE) {
                    // 取消删除，返回查看界面
                    currentMode = Mode.CONTACT_VIEW;
                    updateButtons();
                } else {
                    // 返回列表
                    currentMode = Mode.CONTACT_LIST;
                    currentPhoneNumber = null;
                    currentContactName = null;
                    updateButtons();
                }
                return;
            case BTN_NEW_CONTACT:
                currentMode = Mode.CONTACT_EDIT;
                currentPhoneNumber = null;
                currentContactName = null;
                updateButtons();
                return;
            case BTN_SAVE:
                saveContact();
                return;
            case BTN_DELETE:
                if (currentMode == Mode.CONTACT_VIEW) {
                    // 进入删除确认界面
                    currentMode = Mode.CONTACT_DELETE;
                    updateButtons();
                } else if (currentMode == Mode.CONTACT_DELETE) {
                    // 确认删除
                    deleteContact();
                }
                return;
            case BTN_EDIT:
                currentMode = Mode.CONTACT_EDIT;
                updateButtons();
                return;
            case BTN_SEND_SMS:
                openSMS();
                return;
        }

        // 处理联系人按钮点击
        if (button.id >= BTN_CONTACT_START) {
            int contactIndex = button.id - BTN_CONTACT_START;
            Map<String, String> contacts = myPhoneNumber != null ? 
                SMSManager.getInstance().getAllContacts(myPhoneNumber) : new HashMap<>();
            List<String> phoneNumbers = new ArrayList<>(contacts.keySet());
            if (contactIndex >= 0 && contactIndex < phoneNumbers.size()) {
                currentPhoneNumber = phoneNumbers.get(contactIndex);
                currentContactName = contacts.get(currentPhoneNumber);
                currentMode = Mode.CONTACT_VIEW;
                updateButtons();
            }
        }
    }

    private void saveContact() {
        if (nameInput == null || phoneInput == null) return;

        String name = nameInput.getText().trim();
        String phone = phoneInput.getText().trim();

        if (name.isEmpty()) {
            mc.player.sendMessage(new net.minecraft.util.text.TextComponentString("§c[MPhone] §f请输入联系人姓名"));
            return;
        }

        if (phone.isEmpty()) {
            mc.player.sendMessage(new net.minecraft.util.text.TextComponentString("§c[MPhone] §f请输入电话号码"));
            return;
        }

        // 验证电话号码格式（6位数字）
        if (!phone.matches("\\d{6}")) {
            mc.player.sendMessage(new net.minecraft.util.text.TextComponentString("§c[MPhone] §f电话号码必须是6位数字"));
            return;
        }

        // 保存联系人 - 使用当前手机的联系人列表
        if (myPhoneNumber != null) {
            SMSManager.getInstance().addContact(myPhoneNumber, phone, name);
            mc.player.sendMessage(new net.minecraft.util.text.TextComponentString("§a[MPhone] §f联系人已保存"));
        } else {
            mc.player.sendMessage(new net.minecraft.util.text.TextComponentString("§c[MPhone] §f请先插入SIM卡"));
            return;
        }

        // 返回列表
        currentMode = Mode.CONTACT_LIST;
        currentPhoneNumber = null;
        currentContactName = null;
        updateButtons();
    }

    private void deleteContact() {
        if (currentPhoneNumber == null || myPhoneNumber == null) return;

        // 从当前手机的联系人列表中删除
        SMSManager.getInstance().deleteContact(myPhoneNumber, currentPhoneNumber);
        mc.player.sendMessage(new net.minecraft.util.text.TextComponentString("§a[MPhone] §f联系人已删除"));

        // 返回列表
        currentMode = Mode.CONTACT_LIST;
        currentPhoneNumber = null;
        currentContactName = null;
        updateButtons();
    }

    private void openSMS() {
        if (currentPhoneNumber == null) return;

        // 保存当前应用状态为短信
        ItemStack heldItem = mc.player.getHeldItemMainhand();
        if (heldItem.getItem() instanceof ItemPhone) {
            ItemPhone.setCurrentApp(heldItem, GuiHandler.GUI_SMS);
        }

        // 打开短信界面，并设置当前联系人为该联系人
        GuiSMS smsGui = new GuiSMS(playerInventory);
        smsGui.setCurrentContact(currentPhoneNumber);
        this.mc.displayGuiScreen(smsGui);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        if (nameInput != null) {
            nameInput.textboxKeyTyped(typedChar, keyCode);
        }
        if (phoneInput != null) {
            phoneInput.textboxKeyTyped(typedChar, keyCode);
        }

        // 回车保存
        if (keyCode == Keyboard.KEY_RETURN && currentMode == Mode.CONTACT_EDIT) {
            saveContact();
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (nameInput != null) {
            nameInput.mouseClicked(mouseX, mouseY, mouseButton);
        }
        if (phoneInput != null) {
            phoneInput.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
