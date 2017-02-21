package com.cbtsoft.pokercenter.goldenflower.controller;

import com.cbtsoft.pokercenter.core.helper.SessionHelper;
import com.cbtsoft.pokercenter.core.model.Room;
import com.cbtsoft.pokercenter.core.pojo.Action;
import com.cbtsoft.pokercenter.core.pojo.Player;
import com.cbtsoft.pokercenter.core.service.PlayerService;
import com.cbtsoft.pokercenter.core.service.RoomService;
import com.cbtsoft.pokercenter.goldenflower.model.GoldenFlowerDealer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
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

    @MessageMapping("/gf/leaveRoom")
    public void leaveRoom(Map<String, Integer> request, SimpMessageHeaderAccessor messageHeaderAccessor) {
        String userName = SessionHelper.getUserName(messageHeaderAccessor);
        Room room;
        // TODO: check request fields
        if ((room = roomService.getRoom(request.get("roomNum"))) == null) {
            messageTemplate.convertAndSendToUser(userName, "/topic/message", "Error room number.");
            return;
        }
        room.leave(playerService.getPlayerByUserName(userName));
    }

    @MessageMapping("/gf/room/{roomNum}/action")
    public void action(Action action, @DestinationVariable("roomNum")Integer roomNum, SimpMessageHeaderAccessor messageHeaderAccessor) {
        String userName = SessionHelper.getUserName(messageHeaderAccessor);
        if (action.getActionType() != GoldenFlowerDealer.ActionType.CALL.getValue()
                && action.getActionType() != GoldenFlowerDealer.ActionType.FOLD.getValue()) {
            messageTemplate.convertAndSendToUser(userName, "/topic/message", "Illegal action type.");
            return;
        }
        Room room;
        if ((room = roomService.getRoom(roomNum)) == null) {
            messageTemplate.convertAndSendToUser(userName, "/topic/message", "Error room number.");
            return;
        }
        Player player = playerService.getPlayerByUserName(userName);
        action.setPlayer(player);
        room.act(action);
    }

    @MessageMapping("/gf/room/{roomNum}/leave")
    public void leaveRoom(@DestinationVariable("roomNum")Integer roomNum, SimpMessageHeaderAccessor messageHeaderAccessor) {
        String userName = SessionHelper.getUserName(messageHeaderAccessor);
        Room room;
        if ((room = roomService.getRoom(roomNum)) == null) {
            messageTemplate.convertAndSendToUser(userName, "/topic/message", "Error room number.");
            return;
        }
        Player player = playerService.getPlayerByUserName(userName);
        room.leave(player);
    }

    @MessageMapping("/gf/room/{roomNum}/allPlayerList")
    public void getAllPlayerList(@DestinationVariable("roomNum")Integer roomNum, SimpMessageHeaderAccessor messageHeaderAccessor) {
        String userName = SessionHelper.getUserName(messageHeaderAccessor);

        Room room;
        if ((room = roomService.getRoom(roomNum)) == null) {
            return;
        }
        Map<Integer, Player> table = room.getTable();
        Map<String, Object> result = new HashMap<>();
        messageTemplate.convertAndSendToUser(userName, "/topic/message", table);
    }

    @MessageMapping("/gf/room/{roomNum}/gameDetail")
    public void getGameDetail(@DestinationVariable("roomNum")Integer roomNum, SimpMessageHeaderAccessor messageHeaderAccessor) {
        String userName = SessionHelper.getUserName(messageHeaderAccessor);

        Room room;
        if ((room = roomService.getRoom(roomNum)) == null) {
            return;
        }
        Map<String, Object> result = room.getDetail();
        messageTemplate.convertAndSendToUser(userName, "/topic/message", result);
    }

    @MessageMapping("/gf/whoami")
    public void whoAmI(SimpMessageHeaderAccessor messageHeaderAccessor) {
        String userName = SessionHelper.getUserName(messageHeaderAccessor);

        messageTemplate.convertAndSendToUser(userName, "/topic/message", userName);
    }
}
