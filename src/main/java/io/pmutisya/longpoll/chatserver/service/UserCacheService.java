package io.pmutisya.longpoll.chatserver.service;

import io.pmutisya.longpoll.chatserver.service.dto.RegisterUserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserCacheService {
    private final Logger logger = LoggerFactory.getLogger(UserCacheService.class);

    private final Map<String, RegisterUserDTO> usersMap = new HashMap<>();

    public void save(RegisterUserDTO registerUserDTO) {
        logger.info("Request to save user : {}", registerUserDTO);

        if (registerUserDTO.getPhoneNumber() == null) {
            registerUserDTO.setPhoneNumber(UUID.randomUUID().toString());
        }

        usersMap.put(registerUserDTO.getPhoneNumber(), registerUserDTO);
    }

    public Optional<RegisterUserDTO> findOne(String phoneNumber) {
        logger.info("Request to find user with phone number : {}", phoneNumber);
        return Optional.ofNullable(usersMap.get(phoneNumber));
    }
}
