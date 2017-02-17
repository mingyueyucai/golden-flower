package com.cbtsoft.pokercenter.core.service;

import com.cbtsoft.pokercenter.core.factory.DealerFactory;
import com.cbtsoft.pokercenter.core.model.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RoomService {
    private Map<Integer, Room> roomMap = new ConcurrentHashMap<>();
    private int count = 0;

    @Autowired
    private DealerFactory dealerFactory;

    @Autowired
    private SimpMessageSendingOperations messageTemplate;

    @Autowired
    @Qualifier("initRoomNum")
    private Integer initRoomNum;

    @PostConstruct
    public void init() {
        for (int i = 0; i < initRoomNum; i++) {
            createRoom();
        }
    }

    public Map<Integer, Room> getRoomMap() {
        return new HashMap<>(roomMap);
    }

    public Room createRoom() {
        Room room = new Room(messageTemplate, dealerFactory.createDealer());
        roomMap.put(count++, room);
        return room;
    }

    public Room getRoom(int roomNumber) {
        return roomMap.get(roomNumber);
    }

}
