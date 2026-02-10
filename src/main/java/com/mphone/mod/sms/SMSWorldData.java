package com.mphone.mod.sms;

import com.mphone.mod.MPhoneMod;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

import java.util.*;

/**
 * 世界短信数据存储 - 持久化保存短信到世界存档
 */
public class SMSWorldData extends WorldSavedData {
    private static final String DATA_NAME = MPhoneMod.MODID + "_sms_data";

    // 存储所有短信，按手机号分组
    private Map<String, List<SMSMessage>> messageStore = new HashMap<>();

    // 联系人列表 (owner手机号 -> (联系人手机号 -> 备注名))
    private Map<String, Map<String, String>> contacts = new HashMap<>();

    public SMSWorldData() {
        super(DATA_NAME);
    }

    public SMSWorldData(String name) {
        super(name);
    }

    /**
     * 获取世界短信数据实例
     */
    public static SMSWorldData getInstance(World world) {
        MapStorage storage = world.getMapStorage();
        SMSWorldData data = (SMSWorldData) storage.getOrLoadData(SMSWorldData.class, DATA_NAME);

        if (data == null) {
            data = new SMSWorldData();
            storage.setData(DATA_NAME, data);
        }

        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        messageStore.clear();
        contacts.clear();

        // 读取短信数据
        NBTTagCompound messagesNBT = nbt.getCompoundTag("messages");
        for (String phoneNumber : messagesNBT.getKeySet()) {
            NBTTagList messageList = messagesNBT.getTagList(phoneNumber, 10);
            List<SMSMessage> messages = new ArrayList<>();

            for (int i = 0; i < messageList.tagCount(); i++) {
                NBTTagCompound msgNBT = messageList.getCompoundTagAt(i);
                SMSMessage msg = readMessageFromNBT(msgNBT);
                if (msg != null) {
                    messages.add(msg);
                }
            }

            messageStore.put(phoneNumber, messages);
        }

        // 读取联系人数据 (新格式: owner -> contacts map)
        NBTTagCompound contactsNBT = nbt.getCompoundTag("contacts_v2");
        for (String ownerPhone : contactsNBT.getKeySet()) {
            NBTTagCompound ownerContactsNBT = contactsNBT.getCompoundTag(ownerPhone);
            Map<String, String> ownerContacts = new HashMap<>();
            for (String contactPhone : ownerContactsNBT.getKeySet()) {
                ownerContacts.put(contactPhone, ownerContactsNBT.getString(contactPhone));
            }
            contacts.put(ownerPhone, ownerContacts);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        // 保存短信数据
        NBTTagCompound messagesNBT = new NBTTagCompound();
        for (Map.Entry<String, List<SMSMessage>> entry : messageStore.entrySet()) {
            NBTTagList messageList = new NBTTagList();
            for (SMSMessage msg : entry.getValue()) {
                messageList.appendTag(writeMessageToNBT(msg));
            }
            messagesNBT.setTag(entry.getKey(), messageList);
        }
        nbt.setTag("messages", messagesNBT);

        // 保存联系人数据 (新格式)
        NBTTagCompound contactsNBT = new NBTTagCompound();
        for (Map.Entry<String, Map<String, String>> entry : contacts.entrySet()) {
            NBTTagCompound ownerContactsNBT = new NBTTagCompound();
            for (Map.Entry<String, String> contactEntry : entry.getValue().entrySet()) {
                ownerContactsNBT.setString(contactEntry.getKey(), contactEntry.getValue());
            }
            contactsNBT.setTag(entry.getKey(), ownerContactsNBT);
        }
        nbt.setTag("contacts_v2", contactsNBT);

        return nbt;
    }

    private SMSMessage readMessageFromNBT(NBTTagCompound nbt) {
        try {
            String sender = nbt.getString("sender");
            String receiver = nbt.getString("receiver");
            String content = nbt.getString("content");
            long timestamp = nbt.getLong("timestamp");
            boolean isRead = nbt.getBoolean("isRead");
            boolean isSent = nbt.getBoolean("isSent");

            SMSMessage msg = new SMSMessage(sender, receiver, content, timestamp);
            msg.setRead(isRead);
            msg.setSent(isSent);
            return msg;
        } catch (Exception e) {
            return null;
        }
    }

    private NBTTagCompound writeMessageToNBT(SMSMessage msg) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("sender", msg.getSender());
        nbt.setString("receiver", msg.getReceiver());
        nbt.setString("content", msg.getContent());
        nbt.setLong("timestamp", msg.getTimestamp());
        nbt.setBoolean("isRead", msg.isRead());
        nbt.setBoolean("isSent", msg.isSent());
        return nbt;
    }

    // ========== 数据操作方法 ==========

    public Map<String, List<SMSMessage>> getMessageStore() {
        return messageStore;
    }

    public Map<String, Map<String, String>> getContacts() {
        return contacts;
    }

    public void setMessageStore(Map<String, List<SMSMessage>> store) {
        this.messageStore = store;
        markDirty();
    }

    public void setContacts(Map<String, Map<String, String>> contacts) {
        this.contacts = contacts;
        markDirty();
    }

    /**
     * 从世界数据同步到SMSManager
     */
    public void syncToManager() {
        SMSManager manager = SMSManager.getInstance();
        manager.loadFromWorldData(messageStore, contacts);
    }

    /**
     * 从SMSManager同步到世界数据
     */
    public void syncFromManager() {
        SMSManager manager = SMSManager.getInstance();
        this.messageStore = new HashMap<>(manager.getMessageStoreForSave());
        this.contacts = new HashMap<>(manager.getContactsForSave());
        markDirty();
    }
}
