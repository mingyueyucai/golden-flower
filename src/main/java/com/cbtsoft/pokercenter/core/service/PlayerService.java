package com.cbtsoft.pokercenter.core.service;

import com.cbtsoft.pokercenter.core.pojo.Player;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PlayerService {
    private Map<String ,Player> loggedInUsers = new HashMap<>();

    public Player getPlayerByUserName(String userName) {
        if (loggedInUsers.containsKey(userName)) {
            return loggedInUsers.get(userName);
        } else {
            Player player = new Player(userName);
            player.setChips(1000);
            loggedInUsers.put(userName, player);
            return player;
        }
    }
}
