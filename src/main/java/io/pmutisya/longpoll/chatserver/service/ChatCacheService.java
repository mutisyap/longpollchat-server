package io.pmutisya.longpoll.chatserver.service;

import io.pmutisya.longpoll.chatserver.service.dto.ChatMessageDTO;
import io.pmutisya.longpoll.chatserver.service.dto.DeferredRequestWrapper;
import io.pmutisya.longpoll.chatserver.service.dto.RegisterUserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ChatCacheService {
    private final Logger logger = LoggerFactory.getLogger(ChatCacheService.class);

    private final Set<ChatMessageDTO> chatMessages = new HashSet<>();

    private final UserCacheService userCacheService;

    private final long deferredRequestTimeoutMs = 60000L;

    private Set<DeferredRequestWrapper> deferedRequests = new HashSet<>();

    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    public ChatCacheService(UserCacheService userCacheService) {
        this.userCacheService = userCacheService;

        scheduledExecutorService.scheduleWithFixedDelay(this::respondWithNewMessages, 1, 2, TimeUnit.SECONDS);
    }

    public ChatMessageDTO sendChatMessage(ChatMessageDTO chatMessageDTO) {
        logger.info("Request to send chat message : {}", chatMessageDTO);
        chatMessageDTO.setSentTime(System.currentTimeMillis());
        chatMessageDTO.setSentDateTime(LocalDateTime.now());

        chatMessages.add(chatMessageDTO);

        return chatMessageDTO;
    }

    private void respondWithNewMessages(){
        for (DeferredRequestWrapper deferredRequestWrapper : deferedRequests){
            if (deferredRequestWrapper.isGetAll()){
                deferredRequestWrapper.getListDeferredResult().setResult(new ArrayList<>(chatMessages));
                deferredRequestWrapper.setResponded(true);
            } else {
                // where did this user last read?
                Optional<RegisterUserDTO> userOptional = userCacheService.findOne(deferredRequestWrapper.getPhoneNumber());

                if (userOptional.isPresent()){
                    RegisterUserDTO registerUserDTO = userOptional.get();
                    long lastMessageTime = registerUserDTO.getMostRecentMessageSentTime();

                    List<ChatMessageDTO> chatMessageDTOList = findMessagesNewerThan(lastMessageTime);
                    chatMessageDTOList.removeIf(chatMessageDTO -> Objects.equals(chatMessageDTO.getSourcePhoneNumber(), deferredRequestWrapper.getPhoneNumber()));

                    if (!chatMessageDTOList.isEmpty()){
                        deferredRequestWrapper.getListDeferredResult().setResult(chatMessageDTOList);
                        deferredRequestWrapper.setResponded(true);

                        lastMessageTime = getLastRefresh(chatMessageDTOList, lastMessageTime);
                        registerUserDTO.setMostRecentMessageSentTime(lastMessageTime);
                        userCacheService.save(registerUserDTO);
                    }
                }
            }
        }

        deferedRequests.removeIf(DeferredRequestWrapper::isResponded);
    }

    public DeferredResult<List<ChatMessageDTO>> getNewMessagesDeferred(String phoneNumber, boolean getAll) {
        DeferredResult<List<ChatMessageDTO>> deferredResult = new DeferredResult<>(deferredRequestTimeoutMs);

        deferredResult.onTimeout(() -> deferredResult.setResult(new ArrayList<>()));

        Optional<RegisterUserDTO> userOptional = userCacheService.findOne(phoneNumber);

        if (userOptional.isEmpty()){
            throw new RuntimeException("User not found");
        }

        DeferredRequestWrapper deferredRequestWrapper =  new DeferredRequestWrapper(phoneNumber, getAll, deferredResult, userOptional.get());
        deferedRequests.add(deferredRequestWrapper);

        return deferredResult;
    }

    public List<ChatMessageDTO> getNewMessages(String phoneNumber, boolean getAll) {

        // return only new messages
        Optional<RegisterUserDTO> userOptional = userCacheService.findOne(phoneNumber);

        List<ChatMessageDTO> newMessages;
        if (userOptional.isPresent()) {
            RegisterUserDTO registerUserDTO = userOptional.get();

            if (getAll) {
                newMessages =  new ArrayList<>(chatMessages);
            } else {
                newMessages = findMessagesNewerThan(registerUserDTO.getMostRecentMessageSentTime());
            }

            if (!newMessages.isEmpty()) {
                // update
                long mostRecentMessageSentTime = getLastRefresh(newMessages, registerUserDTO.getMostRecentMessageSentTime());
                registerUserDTO.setMostRecentMessageSentTime(mostRecentMessageSentTime);

                userCacheService.save(registerUserDTO);
            }
        } else {
            throw new RuntimeException("Invalid user. Please register first");
        }
        return newMessages;
    }

    private List<ChatMessageDTO> findMessagesNewerThan(long lastRefreshTime) {
        return chatMessages.stream().filter(chatMessageDTO -> chatMessageDTO.getSentTime() > lastRefreshTime).collect(Collectors.toList());
    }

    private long getLastRefresh(List<ChatMessageDTO> chatMessages, long lastMessageTimeFetched) {
        long mostRecentMessageTime = lastMessageTimeFetched;
        for (ChatMessageDTO chatMessageDTO : chatMessages) {
            if (chatMessageDTO.getSentTime() > mostRecentMessageTime) {
                mostRecentMessageTime = chatMessageDTO.getSentTime();
            }
        }
        return mostRecentMessageTime;
    }

}
