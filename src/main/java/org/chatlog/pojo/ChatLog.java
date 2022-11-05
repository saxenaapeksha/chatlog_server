package org.chatlog.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatLog {
    private String message;
    private LocalDateTime timestamp;
    private boolean isSent;

    public ChatLog() {}
    public ChatLog(String message,LocalDateTime timestamp,boolean isSent) {
        this.message = message;
        this.timestamp = timestamp;
        this.isSent = isSent;
    }
}
