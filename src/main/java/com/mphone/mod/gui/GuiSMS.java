package com.mphone.mod.gui;

import com.mphone.mod.MPhoneMod;
import com.mphone.mod.blocks.BlockBaseStation;
import com.mphone.mod.items.ItemPhone;
import com.mphone.mod.sms.SMSManager;
import com.mphone.mod.sms.SMSMessage;
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
import java.util.List;
import java.util.Map;

public class GuiSMS extends GuiScreen {
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
    private static final int BTN_NEW_MESSAGE = 1;
    private static final int BTN_SEND = 2;
    private static final int BTN_CONVERSATION_START = 10;

    private final InventoryPlayer playerInventory;

    private int guiLeft;
    private int guiTop;
    private int screenLeft;
    private int screenTop;

    // 界面模式
    private enum Mode {
        CONVERSATION_LIST,  // 会话列表
        CHAT_VIEW,          // 聊天界面
        NEW_MESSAGE         // 新建短信
    }
    private Mode currentMode = Mode.CONVERSATION_LIST;

    // 当前选中的联系人
    private String currentContact = null;

    // 输入框
    private GuiTextField phoneInput;
    private GuiTextField messageInput;

    // 滚动偏移
    private int scrollOffset = 0;
    private static final int ITEMS_PER_PAGE = 5;

    // 当前手机号
    private String myPhoneNumber = null;

    public GuiSMS(InventoryPlayer playerInventory) {
        this.playerInventory = playerInventory;
    }

    /**
     * 设置当前联系人并切换到聊天界面
     */
    public void setCurrentContact(String contactNumber) {
        this.currentContact = contactNumber;
        this.currentMode = Mode.CHAT_VIEW;
        // 标记为已读
        if (myPhoneNumber != null && contactNumber != null) {
            SMSManager.getInstance().markAsRead(myPhoneNumber, contactNumber);
        }
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

        if (currentMode == Mode.CONVERSATION_LIST) {
            // 新建短信按钮
            this.buttonList.add(new GuiButton(BTN_NEW_MESSAGE, screenLeft + SCREEN_WIDTH - 40, screenTop + 5, 38, 12, "新建"));

            // 会话按钮
            if (myPhoneNumber != null) {
                Map<String, SMSMessage> conversations = SMSManager.getInstance().getConversations(myPhoneNumber);
                List<String> contacts = new ArrayList<>(conversations.keySet());

                int startY = screenTop + 30;
                int itemHeight = 32;

                for (int i = 0; i < ITEMS_PER_PAGE; i++) {
                    int contactIndex = scrollOffset + i;
                    if (contactIndex >= contacts.size()) break;

                    String contact = contacts.get(contactIndex);
                    this.buttonList.add(new GuiButton(BTN_CONVERSATION_START + contactIndex, screenLeft + SCREEN_WIDTH - 30, startY + i * itemHeight + 10, 28, 12, "查看"));
                }
            }
        } else if (currentMode == Mode.CHAT_VIEW || currentMode == Mode.NEW_MESSAGE) {
            // 发送按钮
            this.buttonList.add(new GuiButton(BTN_SEND, screenLeft + SCREEN_WIDTH - 30, screenTop + SCREEN_HEIGHT - 25, 28, 12, "发送"));

            // 输入框
            if (currentMode == Mode.NEW_MESSAGE) {
                phoneInput = new GuiTextField(0, this.fontRenderer, screenLeft + 45, screenTop + 28, 60, 12);
                phoneInput.setMaxStringLength(6);
                phoneInput.setText("");
            }

            messageInput = new GuiTextField(1, this.fontRenderer, screenLeft + 4, screenTop + SCREEN_HEIGHT - 45, SCREEN_WIDTH - 8, 18);
            messageInput.setMaxStringLength(100);
            messageInput.setText("");
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
            case CONVERSATION_LIST:
                drawConversationList(mouseX, mouseY);
                break;
            case CHAT_VIEW:
                drawChatView(mouseX, mouseY);
                break;
            case NEW_MESSAGE:
                drawNewMessageView(mouseX, mouseY);
                break;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawConversationList(int mouseX, int mouseY) {
        // 标题
        String title = "短信";
        int titleWidth = this.fontRenderer.getStringWidth(title);
        this.fontRenderer.drawString(title, screenLeft + (SCREEN_WIDTH - titleWidth) / 2, screenTop + 6, 0xFFFFFF);

        if (myPhoneNumber == null) {
            // 没有SIM卡
            String msg = "请先插入SIM卡";
            int msgWidth = this.fontRenderer.getStringWidth(msg);
            this.fontRenderer.drawString(msg, screenLeft + (SCREEN_WIDTH - msgWidth) / 2, screenTop + 80, 0xAAAAAA);
            return;
        }

        // 获取会话列表
        Map<String, SMSMessage> conversations = SMSManager.getInstance().getConversations(myPhoneNumber);

        if (conversations.isEmpty()) {
            String msg = "暂无短信";
            int msgWidth = this.fontRenderer.getStringWidth(msg);
            this.fontRenderer.drawString(msg, screenLeft + (SCREEN_WIDTH - msgWidth) / 2, screenTop + 80, 0xAAAAAA);
            return;
        }

        // 绘制会话列表
        int startY = screenTop + 30;
        int itemHeight = 32;
        int index = 0;

        for (Map.Entry<String, SMSMessage> entry : conversations.entrySet()) {
            if (index < scrollOffset) {
                index++;
                continue;
            }
            if (index >= scrollOffset + ITEMS_PER_PAGE) break;

            String contact = entry.getKey();
            SMSMessage lastMessage = entry.getValue();
            int y = startY + (index - scrollOffset) * itemHeight;

            // 背景
            boolean hovered = mouseX >= screenLeft + 2 && mouseX < screenLeft + SCREEN_WIDTH - 2 &&
                    mouseY >= y && mouseY < y + itemHeight - 2;
            int bgColor = hovered ? 0x55333333 : 0x33222222;
            drawRect(screenLeft + 2, y, screenLeft + SCREEN_WIDTH - 2, y + itemHeight - 2, bgColor);

            // 联系人名称 - 使用当前手机的联系人列表
            String contactName = myPhoneNumber != null ? 
                SMSManager.getInstance().getContactName(myPhoneNumber, contact) : contact;
            int nameColor = lastMessage.isRead() || lastMessage.getSender().equals(myPhoneNumber) ? 0xFFFFFF : 0x4CAF50;
            this.fontRenderer.drawString(contactName, screenLeft + 6, y + 4, nameColor);

            // 未读标记
            if (!lastMessage.isRead() && !lastMessage.getSender().equals(myPhoneNumber)) {
                drawRect(screenLeft + 6 + this.fontRenderer.getStringWidth(contactName) + 4, y + 6, screenLeft + 6 + this.fontRenderer.getStringWidth(contactName) + 8, y + 10, 0xFFFF0000);
            }

            // 预览内容
            String preview = lastMessage.getPreviewContent(12);
            this.fontRenderer.drawString(preview, screenLeft + 6, y + 16, 0xAAAAAA);

            // 时间
            String time = lastMessage.getFormattedTime();
            int timeWidth = this.fontRenderer.getStringWidth(time);
            this.fontRenderer.drawString(time, screenLeft + SCREEN_WIDTH - timeWidth - 34, y + 4, 0x888888);

            index++;
        }
    }

    private void drawChatView(int mouseX, int mouseY) {
        if (currentContact == null || myPhoneNumber == null) return;

        // 标题 - 联系人名称
        String contactName = SMSManager.getInstance().getContactName(myPhoneNumber, currentContact);
        int titleWidth = this.fontRenderer.getStringWidth(contactName);
        this.fontRenderer.drawString(contactName, screenLeft + (SCREEN_WIDTH - titleWidth) / 2, screenTop + 6, 0xFFFFFF);

        // 获取聊天记录
        List<SMSMessage> messages = SMSManager.getInstance().getMessagesWithContact(myPhoneNumber, currentContact);

        // 绘制消息区域背景
        drawRect(screenLeft + 2, screenTop + 22, screenLeft + SCREEN_WIDTH - 2, screenTop + SCREEN_HEIGHT - 50, 0xFF1A1A1A);

        // 绘制消息
        int msgY = screenTop + SCREEN_HEIGHT - 55;
        int maxBubbleWidth = SCREEN_WIDTH - 26;

        for (int i = messages.size() - 1; i >= 0; i--) {
            SMSMessage msg = messages.get(i);
            boolean isSent = msg.getSender().equals(myPhoneNumber);

            String[] lines = wrapText(msg.getContent(), maxBubbleWidth - 8);
            int lineHeight = 12;
            int msgHeight = lines.length * lineHeight + 6;
            int msgWidth = maxBubbleWidth;

            if (msgY - msgHeight < screenTop + 25) break;

            int msgX = isSent ? screenLeft + SCREEN_WIDTH - msgWidth - 4 : screenLeft + 4;
            int bubbleColor = isSent ? 0xFF4CAF50 : 0xFF424242;

            // 气泡背景
            drawRect(msgX, msgY - msgHeight, msgX + msgWidth, msgY, bubbleColor);

            // 消息内容 (多行)
            for (int lineIdx = 0; lineIdx < lines.length; lineIdx++) {
                String line = lines[lineIdx];
                int textX = msgX + 6;
                if (isSent) {
                    textX = msgX + msgWidth - 6 - this.fontRenderer.getStringWidth(line);
                }
                this.fontRenderer.drawString(line, textX, msgY - msgHeight + 3 + lineIdx * lineHeight, 0xFFFFFF);
            }

            msgY -= msgHeight + 6;
        }

        // 输入框背景
        drawRect(screenLeft + 2, screenTop + SCREEN_HEIGHT - 48, screenLeft + SCREEN_WIDTH - 2, screenTop + SCREEN_HEIGHT - 26, 0xFF2A2A2A);

        // 绘制输入框
        if (messageInput != null) {
            messageInput.drawTextBox();
        }
    }

    /**
     * 文字换行处理
     */
    private String[] wrapText(String text, int maxWidth) {
        if (text == null || text.isEmpty()) {
            return new String[]{""};
        }

        java.util.List<String> lines = new java.util.ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            String testLine = currentLine.length() == 0 ? word : currentLine.toString() + " " + word;
            int testWidth = this.fontRenderer.getStringWidth(testLine);

            if (testWidth <= maxWidth) {
                currentLine = new StringBuilder(testLine);
            } else {
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                }
                // 如果单个单词就超过宽度，强制分割
                if (this.fontRenderer.getStringWidth(word) > maxWidth) {
                    StringBuilder part = new StringBuilder();
                    for (int i = 0; i < word.length(); i++) {
                        char c = word.charAt(i);
                        String testPart = part.toString() + c;
                        if (this.fontRenderer.getStringWidth(testPart) > maxWidth) {
                            lines.add(part.toString());
                            part = new StringBuilder(String.valueOf(c));
                        } else {
                            part.append(c);
                        }
                    }
                    if (part.length() > 0) {
                        currentLine = part;
                    } else {
                        currentLine = new StringBuilder();
                    }
                } else {
                    currentLine = new StringBuilder(word);
                }
            }
        }

        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }

        return lines.toArray(new String[0]);
    }

    private void drawNewMessageView(int mouseX, int mouseY) {
        // 标题
        String title = "新建短信";
        int titleWidth = this.fontRenderer.getStringWidth(title);
        this.fontRenderer.drawString(title, screenLeft + (SCREEN_WIDTH - titleWidth) / 2, screenTop + 6, 0xFFFFFF);

        // 收件人标签
        this.fontRenderer.drawString("收件人:", screenLeft + 6, screenTop + 30, 0xAAAAAA);

        // 绘制输入框
        if (phoneInput != null) {
            phoneInput.drawTextBox();
        }

        // 输入框背景
        drawRect(screenLeft + 2, screenTop + SCREEN_HEIGHT - 48, screenLeft + SCREEN_WIDTH - 2, screenTop + SCREEN_HEIGHT - 26, 0xFF2A2A2A);

        // 绘制消息输入框
        if (messageInput != null) {
            messageInput.drawTextBox();
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case BTN_BACK:
                if (currentMode == Mode.CONVERSATION_LIST) {
                    // 返回主界面，清除当前应用状态
                    ItemStack heldItem = mc.player.getHeldItemMainhand();
                    if (heldItem.getItem() instanceof com.mphone.mod.items.ItemPhone) {
                        com.mphone.mod.items.ItemPhone.clearCurrentApp(heldItem);
                    }
                    this.mc.displayGuiScreen(new GuiPhone(playerInventory));
                } else {
                    // 返回会话列表
                    currentMode = Mode.CONVERSATION_LIST;
                    currentContact = null;
                    updateButtons();
                }
                return;
            case BTN_NEW_MESSAGE:
                currentMode = Mode.NEW_MESSAGE;
                currentContact = null;
                updateButtons();
                return;
            case BTN_SEND:
                sendMessage();
                return;
        }

        // 处理会话按钮点击
        if (button.id >= BTN_CONVERSATION_START) {
            int contactIndex = button.id - BTN_CONVERSATION_START;
            if (myPhoneNumber != null) {
                Map<String, SMSMessage> conversations = SMSManager.getInstance().getConversations(myPhoneNumber);
                List<String> contacts = new ArrayList<>(conversations.keySet());
                if (contactIndex >= 0 && contactIndex < contacts.size()) {
                    currentContact = contacts.get(contactIndex);
                    currentMode = Mode.CHAT_VIEW;
                    // 标记为已读
                    SMSManager.getInstance().markAsRead(myPhoneNumber, currentContact);
                    updateButtons();
                }
            }
        }
    }

    private void sendMessage() {
        if (myPhoneNumber == null) {
            mc.player.sendMessage(new net.minecraft.util.text.TextComponentString("§c[MPhone] §f请先插入SIM卡"));
            return;
        }

        // 检查信号
        if (mc.world != null && mc.player != null) {
            int signalStrength = BlockBaseStation.getSignalStrength(mc.world, mc.player.posX, mc.player.posY, mc.player.posZ);
            if (signalStrength == 0) {
                mc.player.sendMessage(new net.minecraft.util.text.TextComponentString("§c[MPhone] §f无信号，无法发送短信"));
                return;
            }
        }

        String receiver;
        if (currentMode == Mode.NEW_MESSAGE) {
            receiver = phoneInput != null ? phoneInput.getText().trim() : "";
        } else {
            receiver = currentContact;
        }

        String content = messageInput != null ? messageInput.getText().trim() : "";

        if (receiver.isEmpty()) {
            mc.player.sendMessage(new net.minecraft.util.text.TextComponentString("§c[MPhone] §f请输入收件人号码"));
            return;
        }

        if (content.isEmpty()) {
            mc.player.sendMessage(new net.minecraft.util.text.TextComponentString("§c[MPhone] §f请输入短信内容"));
            return;
        }

        // 发送短信
        SMSManager.getInstance().sendMessage(myPhoneNumber, receiver, content);
        mc.player.sendMessage(new net.minecraft.util.text.TextComponentString("§a[MPhone] §f短信已发送"));

        // 清空输入
        if (messageInput != null) {
            messageInput.setText("");
        }

        // 如果是新建短信，切换到聊天界面
        if (currentMode == Mode.NEW_MESSAGE) {
            currentContact = receiver;
            currentMode = Mode.CHAT_VIEW;
            updateButtons();
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        if (phoneInput != null) {
            phoneInput.textboxKeyTyped(typedChar, keyCode);
        }
        if (messageInput != null) {
            messageInput.textboxKeyTyped(typedChar, keyCode);
        }

        // 回车发送
        if (keyCode == Keyboard.KEY_RETURN && messageInput != null && messageInput.isFocused()) {
            sendMessage();
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (phoneInput != null) {
            phoneInput.mouseClicked(mouseX, mouseY, mouseButton);
        }
        if (messageInput != null) {
            messageInput.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
