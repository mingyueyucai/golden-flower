package com.cbtsoft.pokercenter.core.pojo;

public class Player {
    private String userName;
    private long chips;

    public Player(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getChips() {
        return chips;
    }

    public void setChips(long chips) {
        this.chips = chips;
    }

    public boolean deduct(long v) {
        if (chips < v) {
            return false;
        }
        chips -= v;
        return true;
    }

    /**
     * Two players are equal when their userNames are equal.
     *
     * @param o another Player instance.
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Player)) {
            return false;
        }
        return userName.equals(((Player)o).userName);
    }

}
