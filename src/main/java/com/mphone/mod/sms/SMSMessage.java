package com.mphone.mod.sms;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 短信消息类
 */
public class SMSMessage {
    private final String id;
    private final String sender;
    private final String receiver;
    private final String content;
    private final long timestamp;
    private boolean isRead;
    private boolean isSent;

    public SMSMessage(String sender, String receiver, String content) {
        this.id = UUID.randomUUID().toString();
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
        this.isRead = false;
        this.isSent = true;
    }

    public SMSMessage(String sender, String receiver, String content, long timestamp) {
        this(sender, receiver, content, timestamp, false, true);
    }

    public SMSMessage(String sender, String receiver, String content, long timestamp, boolean isRead, boolean isSent) {
        this.id = UUID.randomUUID().toString();
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.timestamp = timestamp;
        this.isRead = isRead;
        this.isSent = isSent;
    }

    public String getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public boolean isSent() {
        return isSent;
    }

    public void setSent(boolean sent) {
        isSent = sent;
    }

    /**
     * 获取格式化的时间字符串
     */
    public String getFormattedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
        return sdf.format(new Date(timestamp));
    }

    /**
     * 获取简短的预览内容
     */
    public String getPreviewContent(int maxLength) {
        if (content.length() <= maxLength) {
            return content;
        }
        return content.substring(0, maxLength) + "...";
    }
}
