package com.cbtsoft.pokercenter.goldenflower.wrapper;

import com.cbtsoft.pokercenter.core.model.Card;
import com.cbtsoft.pokercenter.core.pojo.Player;

import java.util.List;

public class GoldenFlowerCardListWrapper {
    private List<Card> cardList;
    private Player player;

    public GoldenFlowerCardListWrapper(List<Card> cardList, Player player) {
        this.cardList = cardList;
        this.player = player;
    }

    public List<Card> getCardList() {
        return cardList;
    }

    public Player getPlayer() {
        return player;
    }

}
