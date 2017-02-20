package com.cbtsoft.pokercenter.goldenflower.helper;

import com.cbtsoft.pokercenter.core.model.Card;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GoldenFlowerComparator implements Comparator<List<Card>> {

    private List<Card> cardList;

    /**
     * @param cardList Cards show on the desk.
     */
    public GoldenFlowerComparator(List<Card> cardList) {
        this.cardList = cardList;
    }

    /**
     *
     * @param o1 Cards from player one
     * @param o2 Cards from player two
     * @return
     */
    @Override
    public int compare(List<Card> o1, List<Card> o2) {
        List<Card> cardList1 = new ArrayList<>(o1);
        cardList1.addAll(cardList);

        List<Card> cardList2 = new ArrayList<>(o2);
        cardList2.addAll(cardList);

        GoldenFlowerRankCalculator goldenFlowerRankCalculator = new GoldenFlowerRankCalculator();
        GeneralPokerRankCalculator calculator1 = new GeneralPokerRankCalculator(cardList1, goldenFlowerRankCalculator);
        GeneralPokerRankCalculator calculator2 = new GeneralPokerRankCalculator(cardList2, goldenFlowerRankCalculator);
        return calculator1.getRank() - calculator2.getRank();
    }
}
