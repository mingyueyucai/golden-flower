package com.cbtsoft.pokercenter.helper;

import com.cbtsoft.pokercenter.model.Card;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by changsheng on 2017/2/11.
 *
 * 扎金花牌型排名计算
 */
public class GoldenFlowerRank implements PokerRankCalculable {

    /**
     * 扎金花牌型中牌的数量
     *
     * @return 3
     */
    @Override
    public int numberOfHand() {
        return 3; //magic number : 3 扎金花合法牌型需要3张牌
    }

    @Override
    public int rank(List<Card> _cards) {

        if (_cards.size() != 3) {
            throw new IllegalArgumentException("扎金花是三张牌的游戏!");
        }

        List<Card> cards = new ArrayList<>();
        for (Card card : _cards) {
            Card newCard = new Card(card.getSuit(),card.getValue());
            cards.add(newCard);
        }

        Collections.sort(cards,(Card first, Card second) -> {
            if (first.getValue() < second.getValue()) {
                return 1;
            } else if (first.getValue() > second.getValue()) {
                return -1;
            } else {
                return 0;
            }
        });

        if (cards.get(1).getValue() == cards.get(2).getValue() && cards.get(0).getValue() != cards.get(1).getValue()) {
            Card temp = cards.get(2);
            cards.set(2,cards.get(0));
            cards.set(0,temp);
        }

        if (cards.get(0).getValue() == 14 && cards.get(1).getValue() == 3 && cards.get(2).getValue() == 2) {
            Card temp = cards.get(2);
            cards.set(2,cards.get(0));
            cards.set(0,temp);

            temp = cards.get(1);
            cards.set(1,cards.get(0));
            cards.set(0,temp);

            cards.get(2).setValue(1);


        }

        int ans = 0;

        if (cards.get(0).getValue() == cards.get(1).getValue() && cards.get(1).getValue() == cards.get(2).getValue()) {
            ans = (15 - cards.get(0).getValue()) * 4;
        } else {
            if (cards.get(0).getSuit() == cards.get(1).getSuit() && cards.get(1).getSuit() == cards.get(2).getSuit() && cards.get(0).getValue() - cards.get(2).getValue() == 2 ) {
                ans = 52 + (15 - cards.get(0).getValue()) * 4;
            } else {
                if (cards.get(0).getSuit() == cards.get(1).getSuit() && cards.get(1).getSuit() == cards.get(2).getSuit()) {
                    ans = 100 + 4 + (1262 - ( cards.get(0).getValue() * 81 + cards.get(1).getValue() * 9 + cards.get(2).getValue() )) * 1096 /828;
                } else {
                    if (cards.get(0).getValue() - cards.get(2).getValue() == 2 && cards.get(0).getValue() - cards.get(1).getValue() == 1) {
                        ans = 1200 + (15 - cards.get(0).getValue()) * 60;
                    } else {
                        if (cards.get(0).getValue() == cards.get(1).getValue()) {
                            ans = 1920 + ( 170 - ((cards.get(0).getValue() -2) * 13 + (cards.get(2).getValue() -1))) * 3744 / 169;
                        } else {
                            ans = 5644 + 4 + ( 1262 - (cards.get(0).getValue() * 81 + cards.get(1).getValue() * 9 + cards.get(2).getValue() )) * 16400 / 828;
                        }
                    }
                }
            }
        }

        return ans;
    }
}
