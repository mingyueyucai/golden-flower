package com.cbtsoft.pokercenter.helper;

import com.cbtsoft.pokercenter.model.Card;

import java.util.List;

/**
 * Created by changsheng on 2017/2/11.
 */
public interface PokerRankCalculable {

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
