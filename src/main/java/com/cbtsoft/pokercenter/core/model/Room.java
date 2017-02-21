package com.cbtsoft.pokercenter.core.model;

import com.cbtsoft.pokercenter.core.pojo.Action;
import com.cbtsoft.pokercenter.core.pojo.Message;
import com.cbtsoft.pokercenter.core.pojo.Player;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Room {

    private SimpMessageSendingOperations template;

    private Dealer dealer;

    // all players including players are watching and players are playing
    final private List<Player> allPlayers = new ArrayList<>();

    final private Map<Integer, Player> table = new HashMap<>();

    public Room(SimpMessageSendingOperations messageTemplate, Dealer dealer) {
        this.template = messageTemplate;
        this.dealer = dealer;
        dealer.activate(this);
    }

    public void sendMessage(Message message) {
        synchronized (allPlayers) {
            for (Player player : allPlayers) {
                template.convertAndSendToUser(player.getUserName(), "/topic/message", message);
            }
        }
    }

    public void sendMessage(Player player, Message message) {
        if (!allPlayers.contains(player)) {
            return;
        }
        template.convertAndSendToUser(player.getUserName(), "/topic/message", message);
    }

    public void sendMessageWithTableInfo(Message message) {
        Map<String, Object> body = new HashMap<>();
        body.put("tableInfo", getTable());
        body.put("text", message.getMessageBody());
        sendMessage(new Message(message.getType(), body));
    }

    public boolean enter(Player player) {
        synchronized (allPlayers) {
            if (!allPlayers.contains(player)) {
                boolean added = allPlayers.add(player);
                if (added) {
                    sendMessageWithTableInfo(new Message(5000, player.getUserName() + " entered this room."));
                }
                return added;
            }
            return false;
        }
    }

    public boolean leave(Player player) {
        stand(player);
        synchronized (allPlayers) {
            boolean removed = allPlayers.remove(player);
            if (removed) {
                sendMessageWithTableInfo(new Message(5000, player.getUserName() + " left this room."));
            }
            return removed;
        }
    }

    public boolean sit(int seatNum, Player player) {
        synchronized (table) {
            for (Player one : table.values()) {
                if (one.equals(player)) {
                    sendMessage(player, new Message("You have already sitten."));
                    return false;
                }
            }
            Player previous = table.putIfAbsent(seatNum, player);
            enter(player);
            boolean result = previous == null;
            if (result) {
                dealer.sit(player);
            } else {
                sendMessage(player, new Message("Someone else is sitting here."));
            }
            return result;
        }
    }

    public boolean stand(Player player) {
        synchronized (table) {
            for (Map.Entry<Integer, Player> entry : table.entrySet()) {
                if (entry.getValue().equals(player)) {
                    table.remove(entry.getKey());
                    dealer.stand(player);
                    return true;
                }
            }
            return false;
        }
    }

    public void act(Action action) {
        dealer.handleAction(action);
    }

    public Map<Integer, Player> getTable() {
        synchronized (table) {
            return new HashMap<>(table);
        }
    }

    public Map<String, Object> getDetail() {
        return dealer.getDetail();
    }
}
