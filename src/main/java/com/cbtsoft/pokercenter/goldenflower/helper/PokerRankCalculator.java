package com.cbtsoft.pokercenter.goldenflower.helper;

import com.cbtsoft.pokercenter.core.model.Card;

import java.util.List;

public interface PokerRankCalculator {

    /**
     *计算手牌的排名
     *
     * @param cards
     * @return 当前手牌在所有牌型中的排名
     */
    int rank(List<Card> cards);

    /**
     * 手牌的牌的数量
     *
     * @return number of cards in hand
     */
    int numberOfHand();
}
