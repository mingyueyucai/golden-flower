package com.cbtsoft.pokercenter.core.model;

import com.cbtsoft.pokercenter.core.pojo.Action;
import com.cbtsoft.pokercenter.core.pojo.Player;

public abstract class Dealer {

    protected Room room;
    protected Status status;

    public void activate(Room room) {
        if (this.room != null) {
            throw new UnsupportedOperationException("Activate dealer more than one time.");
        }
        this.room = room;
        status = Status.WAITING;
    }

    /**
     * Notify the dealer that the player list or player status on the table has been modified.
     * Every time when a player sit or stand should call these function once.
     * The Room can promise that the table will not be modified before dealer handle this message.
     */
    public abstract void sit(Player player);

    public abstract void stand(Player player);

    public abstract void statusChanged(Player player);

    public abstract void handleAction(Action action);

    public enum Status {
        WAITING,
        SERVING
    }
}
