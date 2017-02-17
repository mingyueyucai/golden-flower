package com.cbtsoft.pokercenter.core.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StandardDeck implements Deck {
    private List<Card> cards = new ArrayList<>(54);

    public StandardDeck() {
        List<Integer> valueList = Arrays.asList(2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14);  //magic numbers for 2 to 10 and J, Q, K, A
        cards = new ArrayList<>();
        for (Card.Suit suit : Card.Suit.values()) {
            for (int value : valueList) {
                cards.add(new Card(suit, value));
            }
        }

        Collections.shuffle(cards);
    }

    @Override
    public Card deal() {
        if (cards.isEmpty()) {
            throw new IllegalArgumentException("n is greater than cards left.");
        } else {
            return cards.remove(0);
        }
    }

    @Override
    public List<Card> deal(int n) {
        List<Card> temp = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            temp.add(deal());
        }
        return temp;
    }
}
