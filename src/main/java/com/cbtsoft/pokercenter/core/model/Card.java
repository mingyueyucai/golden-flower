package com.cbtsoft.pokercenter.core.model;

public class Card {
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
        if (value <= 0 || value > 14) {  //magic number: 14 equals King(A)
            throw new IllegalArgumentException("Card value out of bound.");
        }
        this.value = value;
    }

    @Override
    public String toString() {
        return suit + "(" + valueToSign(value) + ")";
    }

    private String valueToSign(int v) {
        if (v <= 10) {
            return String.valueOf(v);
        }
        switch (v) {
            case 11 :
                return "J";
            case 12 :
                return "Q";
            case 13 :
                return "K";
            case 14 :
                return "A";
            default:
                return "?";
        }
    }
}
