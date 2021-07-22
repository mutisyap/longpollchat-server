package io.pmutisya.longpoll.chatserver.service.dto;

public class RegisterUserDTO {

    private String phoneNumber;

    private String name;

    private long mostRecentMessageSentTime = 0;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getMostRecentMessageSentTime() {
        return mostRecentMessageSentTime;
    }

    public void setMostRecentMessageSentTime(long mostRecentMessageSentTime) {
        this.mostRecentMessageSentTime = mostRecentMessageSentTime;
    }

    @Override
    public String toString() {
        return "RegisterUserDTO{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", name='" + name + '\'' +
                ", mostRecentMessageSentTime=" + mostRecentMessageSentTime +
                '}';
    }
}
