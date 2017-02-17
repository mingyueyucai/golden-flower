package com.cbtsoft.pokercenter.core.controller;

import com.cbtsoft.pokercenter.core.helper.SessionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
public class MessageController {

    private SimpMessageSendingOperations messageTemplate;

    @Autowired
    public MessageController(SimpMessageSendingOperations messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    @MessageMapping("/room/{roomId}/message")
    public void sendRoomMessage(String helloMessage, @DestinationVariable String roomId, SimpMessageHeaderAccessor messageHeaderAccessor) {
        String userName = SessionHelper.getUserName(messageHeaderAccessor);
        messageTemplate.convertAndSendToUser(userName, "/topic/message", "{\"content\":\"you are in room " + roomId + ".\"}");
    }
}
