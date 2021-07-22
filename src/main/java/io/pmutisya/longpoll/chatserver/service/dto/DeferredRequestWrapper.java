package io.pmutisya.longpoll.chatserver.service.dto;

import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;

public class DeferredRequestWrapper {

    private final String phoneNumber;

    private final boolean getAll;

    private final DeferredResult<List<ChatMessageDTO>> listDeferredResult;

    private final RegisterUserDTO registerUserDTO;

    private boolean responded = false;

    public DeferredRequestWrapper(String phoneNumber, boolean getAll, DeferredResult<List<ChatMessageDTO>> listDeferredResult, RegisterUserDTO registerUserDTO) {
        this.phoneNumber = phoneNumber;
        this.getAll = getAll;
        this.listDeferredResult = listDeferredResult;
        this.registerUserDTO = registerUserDTO;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public boolean isGetAll() {
        return getAll;
    }

    public DeferredResult<List<ChatMessageDTO>> getListDeferredResult() {
        return listDeferredResult;
    }

    public RegisterUserDTO getRegisterUserDTO() {
        return registerUserDTO;
    }

    public boolean isResponded() {
        return responded;
    }

    public void setResponded(boolean responded) {
        this.responded = responded;
    }
}
