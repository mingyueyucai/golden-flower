package com.cbtsoft.pokercenter.goldenflower.controller;

import com.cbtsoft.pokercenter.core.helper.SessionHelper;
import com.cbtsoft.pokercenter.core.model.Room;
import com.cbtsoft.pokercenter.core.pojo.Message;
import com.cbtsoft.pokercenter.core.service.PlayerService;
import com.cbtsoft.pokercenter.core.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class RoomController {
    @Autowired
    private SimpMessageSendingOperations messageTemplate;

    @Autowired
    private RoomService roomService;

    @Autowired
    private PlayerService playerService;

    @MessageMapping("/gf/enterRoom")
    public void enterRoom(Map<String, Integer> request, SimpMessageHeaderAccessor messageHeaderAccessor) {
        String userName = SessionHelper.getUserName(messageHeaderAccessor);
        Room room;
        // TODO: check request fields
        if ((room = roomService.getRoom(request.get("roomNum"))) == null) {
            messageTemplate.convertAndSendToUser(userName, "/topic/message", "Error room number.");
            return;
        }
        room.sit(request.get("seatNum"), playerService.getPlayerByUserName(userName));
    }
}
