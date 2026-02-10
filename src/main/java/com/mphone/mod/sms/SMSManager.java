package com.mphone.mod.sms;

import java.util.*;

/**
 * 短信管理器 - 管理所有短信的发送和接收
 */
public class SMSManager {
    private static SMSManager instance;

    // 存储所有短信，按手机号分组
    private final Map<String, List<SMSMessage>> messageStore;

    // 联系人列表 ( owner's手机号 -> (联系人手机号 -> 备注名) )
    private final Map<String, Map<String, String>> contacts;

    private SMSManager() {
        this.messageStore = new HashMap<>();
        this.contacts = new HashMap<>();
    }

    public static SMSManager getInstance() {
        if (instance == null) {
            instance = new SMSManager();
        }
        return instance;
    }

    /**
     * 发送短信
     * @param sender 发送者手机号
     * @param receiver 接收者手机号
     * @param content 短信内容
     * @return 创建的短信对象
     */
    public SMSMessage sendMessage(String sender, String receiver, String content) {
        SMSMessage message = new SMSMessage(sender, receiver, content);

        // 存储到发送者的记录中
        messageStore.computeIfAbsent(sender, k -> new ArrayList<>()).add(message);

        // 存储到接收者的记录中（如果是不同的号码）
        if (!sender.equals(receiver)) {
            SMSMessage receivedMessage = new SMSMessage(sender, receiver, content);
            receivedMessage.setRead(false);
            messageStore.computeIfAbsent(receiver, k -> new ArrayList<>()).add(receivedMessage);
        }

        return message;
    }

    /**
     * 获取与某个联系人的所有短信
     * @param phoneNumber 当前手机号
     * @param contactNumber 联系人手机号
     * @return 短信列表
     */
    public List<SMSMessage> getMessagesWithContact(String phoneNumber, String contactNumber) {
        List<SMSMessage> allMessages = messageStore.getOrDefault(phoneNumber, new ArrayList<>());
        List<SMSMessage> result = new ArrayList<>();

        for (SMSMessage msg : allMessages) {
            if (msg.getSender().equals(contactNumber) || msg.getReceiver().equals(contactNumber)) {
                result.add(msg);
            }
        }

        // 按时间排序
        result.sort(Comparator.comparingLong(SMSMessage::getTimestamp));
        return result;
    }

    /**
     * 获取所有会话列表
     * @param phoneNumber 当前手机号
     * @return 会话列表 (联系人号码 -> 最后一条消息)
     */
    public Map<String, SMSMessage> getConversations(String phoneNumber) {
        List<SMSMessage> allMessages = messageStore.getOrDefault(phoneNumber, new ArrayList<>());
        Map<String, SMSMessage> conversations = new LinkedHashMap<>();

        // 按时间倒序遍历，获取每个联系人的最新消息
        List<SMSMessage> sortedMessages = new ArrayList<>(allMessages);
        sortedMessages.sort((m1, m2) -> Long.compare(m2.getTimestamp(), m1.getTimestamp()));

        for (SMSMessage msg : sortedMessages) {
            String contactNumber = msg.getSender().equals(phoneNumber) ? msg.getReceiver() : msg.getSender();
            if (!conversations.containsKey(contactNumber)) {
                conversations.put(contactNumber, msg);
            }
        }

        return conversations;
    }

    /**
     * 获取未读消息数量
     * @param phoneNumber 当前手机号
     * @return 未读数量
     */
    public int getUnreadCount(String phoneNumber) {
        List<SMSMessage> allMessages = messageStore.getOrDefault(phoneNumber, new ArrayList<>());
        int count = 0;
        for (SMSMessage msg : allMessages) {
            if (!msg.isRead() && !msg.getSender().equals(phoneNumber)) {
                count++;
            }
        }
        return count;
    }

    /**
     * 标记与某个联系人的所有消息为已读
     * @param phoneNumber 当前手机号
     * @param contactNumber 联系人手机号
     */
    public void markAsRead(String phoneNumber, String contactNumber) {
        List<SMSMessage> allMessages = messageStore.getOrDefault(phoneNumber, new ArrayList<>());
        for (SMSMessage msg : allMessages) {
            if (msg.getSender().equals(contactNumber)) {
                msg.setRead(true);
            }
        }
    }

    /**
     * 添加联系人
     * @param ownerPhoneNumber 当前手机主的手机号
     * @param contactPhoneNumber 联系人手机号
     * @param name 备注名
     */
    public void addContact(String ownerPhoneNumber, String contactPhoneNumber, String name) {
        contacts.computeIfAbsent(ownerPhoneNumber, k -> new HashMap<>()).put(contactPhoneNumber, name);
    }

    /**
     * 获取联系人名称
     * @param ownerPhoneNumber 当前手机主的手机号
     * @param contactPhoneNumber 联系人手机号
     * @return 备注名，如果没有则返回手机号
     */
    public String getContactName(String ownerPhoneNumber, String contactPhoneNumber) {
        Map<String, String> ownerContacts = contacts.get(ownerPhoneNumber);
        if (ownerContacts != null) {
            return ownerContacts.getOrDefault(contactPhoneNumber, contactPhoneNumber);
        }
        return contactPhoneNumber;
    }

    /**
     * 获取所有联系人
     * @param ownerPhoneNumber 当前手机主的手机号
     * @return 联系人映射 (联系人手机号 -> 备注名)
     */
    public Map<String, String> getAllContacts(String ownerPhoneNumber) {
        Map<String, String> ownerContacts = contacts.get(ownerPhoneNumber);
        if (ownerContacts != null) {
            return new HashMap<>(ownerContacts);
        }
        return new HashMap<>();
    }

    /**
     * 删除联系人
     * @param ownerPhoneNumber 当前手机主的手机号
     * @param contactPhoneNumber 联系人手机号
     */
    public void deleteContact(String ownerPhoneNumber, String contactPhoneNumber) {
        Map<String, String> ownerContacts = contacts.get(ownerPhoneNumber);
        if (ownerContacts != null) {
            ownerContacts.remove(contactPhoneNumber);
        }
    }

    /**
     * 删除与某个联系人的所有短信
     * @param phoneNumber 当前手机号
     * @param contactNumber 联系人手机号
     */
    public void deleteConversation(String phoneNumber, String contactNumber) {
        List<SMSMessage> allMessages = messageStore.getOrDefault(phoneNumber, new ArrayList<>());
        allMessages.removeIf(msg ->
            msg.getSender().equals(contactNumber) || msg.getReceiver().equals(contactNumber));
    }

    /**
     * 删除单条短信
     * @param phoneNumber 当前手机号
     * @param messageId 短信ID
     */
    public void deleteMessage(String phoneNumber, String messageId) {
        List<SMSMessage> allMessages = messageStore.getOrDefault(phoneNumber, new ArrayList<>());
        allMessages.removeIf(msg -> msg.getId().equals(messageId));
    }

    /**
     * 从世界数据加载短信
     */
    public void loadFromWorldData(Map<String, List<SMSMessage>> worldMessages, Map<String, Map<String, String>> worldContacts) {
        messageStore.clear();
        contacts.clear();
        messageStore.putAll(worldMessages);
        contacts.putAll(worldContacts);
    }

    /**
     * 获取消息存储用于保存
     */
    public Map<String, List<SMSMessage>> getMessageStoreForSave() {
        return new HashMap<>(messageStore);
    }

    /**
     * 获取联系人用于保存
     */
    public Map<String, Map<String, String>> getContactsForSave() {
        return new HashMap<>(contacts);
    }
}
