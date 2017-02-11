package com.cbtsoft.pokercenter.model;

public class Card  {
    public enum Suit {
        HEART,
        SPADE,
        DIAMOND,
        CLUB
    }

    private Suit suit;
    private int value;

    public Card(Suit suit, int value) {
        this.suit = suit;
        this.value = value;
    }

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
        if (value <= 0 || value > 14) {  //magic number: 13 equals King(K), 14 equals A.
            throw new IllegalArgumentException("Card value out of bound.");
        }
        this.value = value;
    }


}
