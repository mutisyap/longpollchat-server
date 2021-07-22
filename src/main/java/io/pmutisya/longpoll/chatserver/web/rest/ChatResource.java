package io.pmutisya.longpoll.chatserver.web.rest;

import io.pmutisya.longpoll.chatserver.service.ChatCacheService;
import io.pmutisya.longpoll.chatserver.service.UserCacheService;
import io.pmutisya.longpoll.chatserver.service.dto.ChatMessageDTO;
import io.pmutisya.longpoll.chatserver.service.dto.RegisterUserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;
@RestController
@RequestMapping("/api")
public class ChatResource {
    private final Logger logger = LoggerFactory.getLogger(ChatResource.class);

    private final UserCacheService userCacheService;
    private final ChatCacheService chatCacheService;

    public ChatResource(UserCacheService userCacheService, ChatCacheService chatCacheService) {
        this.userCacheService = userCacheService;
        this.chatCacheService = chatCacheService;
    }

    @PostMapping("/user")
    public void registerUser(@RequestBody RegisterUserDTO registerUserDTO) {
        logger.info("Request to register user : {}", registerUserDTO);
        userCacheService.save(registerUserDTO);
    }

    @PostMapping("/chat")
    public ChatMessageDTO chat(@RequestBody ChatMessageDTO chatMessageDTO){
        logger.info("Chat message from : {}", chatMessageDTO);
       return chatCacheService.sendChatMessage(chatMessageDTO);
    }

    @GetMapping("/messages/{userPhoneNumber}")
    public DeferredResult<List<ChatMessageDTO>> getMessages(@PathVariable String userPhoneNumber, @RequestParam(required = false) Boolean getAll) {
        logger.info("REST request to get messages for user : {}, get all : {}", userPhoneNumber, getAll);

        if (getAll == null) {
            getAll = false;
        }

        return chatCacheService.getNewMessagesDeferred(userPhoneNumber, getAll);
    }
}
