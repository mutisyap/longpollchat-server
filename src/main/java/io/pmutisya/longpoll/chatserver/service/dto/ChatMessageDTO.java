package io.pmutisya.longpoll.chatserver.service.dto;

import java.time.LocalDateTime;

public class ChatMessageDTO {

    private String message;

    private long sentTime;

    private LocalDateTime sentDateTime;

    private String sourcePhoneNumber;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getSentTime() {
        return sentTime;
    }

    public void setSentTime(long sentTime) {
        this.sentTime = sentTime;
    }

    public String getSourcePhoneNumber() {
        return sourcePhoneNumber;
    }

    public void setSourcePhoneNumber(String sourcePhoneNumber) {
        this.sourcePhoneNumber = sourcePhoneNumber;
    }

    public LocalDateTime getSentDateTime() {
        return sentDateTime;
    }

    public void setSentDateTime(LocalDateTime sentDateTime) {
        this.sentDateTime = sentDateTime;
    }

    @Override
    public String toString() {
        return "ChatMessageDTO{" +
                "message='" + message + '\'' +
                ", sentTime=" + sentTime +
                ", sentDateTime=" + sentDateTime +
                ", sourceUuid='" + sourcePhoneNumber + '\'' +
                '}';
    }
}
