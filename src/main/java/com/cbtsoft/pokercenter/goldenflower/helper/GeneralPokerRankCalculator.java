package com.cbtsoft.pokercenter.goldenflower.helper;

import com.cbtsoft.pokercenter.core.model.Card;

import java.util.ArrayList;
import java.util.List;

public class GeneralPokerRankCalculator {
    private int n;
    private PokerRankCalculator calculator;
    private List<Card> cards;
    private List<Card> largestHand;

    public GeneralPokerRankCalculator(List<Card> cards, PokerRankCalculator calculator) {
        if (cards.size() < calculator.numberOfHand()) {
            throw new IllegalArgumentException("Error!");
        }
        this.cards = cards;
        this.n = calculator.numberOfHand();
        this.calculator = calculator;
        this.largestHand = null;
    }

    /**
     * 所有可选牌型中,最大的牌型排名
     *
     * mini is better
     *
     * @return the best rank
     */
    public int getRank() {
        int rank = Integer.MAX_VALUE;
        for (List<Card> set : getAllCombination(cards, n)) {
            int temp = calculator.rank(set);
            if (temp < rank) {
                rank = temp;
                largestHand = set;
            }
        }

        return rank;
    }

    /**
     * 所有可选组合中的最大牌型
     *
     * @return 最大牌型
     */
    public List<Card> getLargestHand() {
        if (largestHand != null) {
            return largestHand;
        } else {
            getRank();
            return largestHand;
        }
    }

    /**
     * 所有可选牌型,递归实现
     *
     * @param cards 所有可选牌
     * @param n 牌型需要的牌的数目
     * @return 所有可选牌型
     */
    private List<List<Card>> getAllCombination(List<Card> cards, int n) {
        List<List<Card>> combination = new ArrayList<>();
        if (n == 1) {
            for (Card card : cards) {
                List<Card> list = new ArrayList<>();
                list.add(card);
                combination.add(list);
            }
        } else {
            for (int i = 0; i < cards.size() - n + 1; i++) {
                List<Card> sub = cards.subList(i + 1, cards.size());
                for (List<Card> list : getAllCombination(sub, n - 1)) {
                    list.add(cards.get(i));
                    combination.add(list);
                }

            }
        }

        return combination;
    }
}
