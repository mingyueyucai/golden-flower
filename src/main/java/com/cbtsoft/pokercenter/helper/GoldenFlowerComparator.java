package com.cbtsoft.pokercenter.helper;

import com.cbtsoft.pokercenter.model.Card;

import java.util.*;

public class GoldenFlowerComparator implements Comparator<List<Card>> {

    /**
     * @param cardList Cards show on the desk.
     */

    private List<Card> cardList;

    public GoldenFlowerComparator(List<Card> cardList) {
        this.cardList = cardList;
    }

    /**
     *
     * @param o1 Cards from player one
     * @param o2 Cards from player two
     * @return 1 when player one's hand is better
     */
    @Override
    public int compare(List<Card> o1, List<Card> o2) {

        List<Card> list1 = new ArrayList<>(o1);
        list1.addAll(cardList);

        List<Card> list2 = new ArrayList<>(o2);
        list2.addAll(cardList);


        GeneralPokerRankCalculator calculator_1 = new GeneralPokerRankCalculator(list1,new GoldenFlowerRank());
        GeneralPokerRankCalculator calculator_2 = new GeneralPokerRankCalculator(list2,new GoldenFlowerRank());
        int rank_1 = calculator_1.getRank();
        int rank_2 = calculator_2.getRank();

        if (rank_1 < rank_2) {
            return 1;
        } else if (rank_1 > rank_2) {
            return -1;
        } else {
            return 0;
        }



    }



}
