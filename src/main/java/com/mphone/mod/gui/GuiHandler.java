package com.mphone.mod.gui;

import com.mphone.mod.MPhoneMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
    public static final int GUI_PHONE = 0;
    public static final int GUI_APP_STORE = 1;
    public static final int GUI_SETTINGS = 2;
    public static final int GUI_SMS = 3;
    public static final int GUI_CONTACTS = 4;

    // 应用商店应用GUI ID (100-199)
    public static final int GUI_COMPASS = 100;
    public static final int GUI_CLOCK = 101;
    public static final int GUI_CALCULATOR = 102;
    public static final int GUI_SNAKE_GAME = 103;
    public static final int GUI_TETRIS_GAME = 104;
    public static final int GUI_CHAT = 105;
    public static final int GUI_MAIL = 106;
    public static final int GUI_NOTES = 107;
    public static final int GUI_CALENDAR = 108;
    public static final int GUI_MUSIC = 109;
    public static final int GUI_GALLERY = 110;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        // 手机GUI是纯客户端界面，服务端不需要处理
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == GUI_PHONE) {
            return new GuiPhone(player.inventory);
        } else if (ID == GUI_APP_STORE) {
            return new GuiAppStore(player.inventory);
        } else if (ID == GUI_SETTINGS) {
            return new GuiSettings(player.inventory);
        } else if (ID == GUI_SMS) {
            return new GuiSMS(player.inventory);
        } else if (ID == GUI_CONTACTS) {
            return new GuiContacts(player.inventory);
        }
        return null;
    }
}
