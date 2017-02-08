package com.cbtsoft.pokercenter.model;

public class Card {
    public enum Suit {
        HEART,
        SPADE,
        DIAMOND,
        CLUB
    }

    private Suit suit;
    private int value;

    public Suit getSuit() {
        return suit;
    }

    public void setSuit(Suit suit) {
        this.suit = suit;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        if (value <= 0 || value > 13) {  //magic number: 13 equals King(K)
            throw new IllegalArgumentException("Card value out of bound.");
        }
        this.value = value;
    }
}
