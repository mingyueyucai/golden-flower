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

    private Status status = Status.WAITING;

    // all players including players are watching and players are playing
    final private List<Player> allPlayers = new ArrayList<>();

    final private Map<Integer, Player> table = new HashMap<>();

    public Room(SimpMessageSendingOperations messageTemplate, Dealer dealer) {
        this.template = messageTemplate;
        this.dealer = dealer;
        dealer.activate(this);
    }

    public void sendMessage(Message message) {
        message.setMessageBody("Public: " + message.getMessageBody());
        synchronized (allPlayers) {
            for (Player player : allPlayers) {
                template.convertAndSendToUser(player.getUserName(), "/topic/message", message);
            }
        }
    }

    public void sendMessage(Player player, Message message) {
        message.setMessageBody("Private: " + message.getMessageBody());
        if (!allPlayers.contains(player)) {
            return;
        }
        template.convertAndSendToUser(player.getUserName(), "/topic/message", message);
    }

    public boolean enter(Player player) {
        synchronized (allPlayers) {
            if (!allPlayers.contains(player)) {
                boolean added = allPlayers.add(player);
                if (added) {
                    sendMessage(new Message(player.getUserName() + " entered this room."));
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
                sendMessage(new Message(player.getUserName() + " left this room."));
            }
            return removed;
        }
    }

    public boolean sit(int seatNum, Player player) {
        enter(player);
        synchronized (table) {
            if (status != Status.WAITING) {
                return false;
            }
            for (Player one : table.values()) {
                if (one.equals(player)) {
                    sendMessage(player, new Message("You have already sitten."));
                    return false;
                }
            }
            Player previous = table.putIfAbsent(seatNum, player);
            boolean result = previous == null;
            sendMessage(player, new Message("Someone else is sitting here."));
            if (result) {
                dealer.sit(player);
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

    public Map<Integer, Player> begin() {
        synchronized (table) {
            status = Status.PLAYING;
            return table;
        }
    }

    public void act(Action action) {
        dealer.handleAction(action);
    }

    public void end() {
        status = Status.WAITING;
    }

    public Map<Integer, Player> getTable() {
        synchronized (table) {
            return new HashMap<>(table);
        }
    }

    private enum Status {
        WAITING,
        PLAYING
    }

}
