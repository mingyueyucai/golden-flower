package com.cbtsoft.pokercenter.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by changsheng on 2017/2/11.
 *
 * 标准扑克牌一副,无大小王
 */
public class StandardSingleDeck implements Deck {


    private List<Card> cards;

    private static int N = 52;

    public StandardSingleDeck() {

        Card.Suit[] SuitSet = {Card.Suit.CLUB, Card.Suit.DIAMOND, Card.Suit.HEART, Card.Suit.SPADE};
        int[] ValueSet = {2, 3 , 4 , 5 , 6 , 7 , 8 , 9 , 10, 11, 12, 13, 14}; //magic numbers for 2, 3, 4, 5, 6, 7, 8, 9, 10, J, Q, K, A
        cards = new ArrayList<>();
        for (Card.Suit suit : SuitSet) {
            for (int value : ValueSet) {
                Card card = new Card(suit,value);
                cards.add(card);
            }
        }

        Collections.shuffle(cards);

    }

    @Override
    public Card deal() {
        if (cards.isEmpty()) {
            return null;
        } else {
            return cards.remove(0);
        }
    }

    @Override
    public List<Card> deal(int n) {
        if (cards.size() < n) {
            return null;
        } else {
            List<Card> temp = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                temp.add(deal());
            }
            return temp;
        }
    }
}
