package com.cbtsoft.pokercenter.core.model;

import java.util.List;

public interface Deck {
    /**
     * Deal one card from the deck.
     *
     * @return one card
     */
    Card deal();

    /**
     * Deal n cards from the deck.
     *
     * @param n
     * @return n cards
     */
    List<Card> deal(int n);
}
