package com.cbtsoft.pokercenter.goldenflower.helper;

import com.cbtsoft.pokercenter.core.model.Card;
import com.cbtsoft.pokercenter.goldenflower.wrapper.GoldenFlowerCardListWrapper;

import java.util.Comparator;
import java.util.List;

public class GoldenFlowerWrapperComparator implements Comparator<GoldenFlowerCardListWrapper> {
    private GoldenFlowerComparator goldenFlowerComparator;

    public GoldenFlowerWrapperComparator(List<Card> cardList) {
        goldenFlowerComparator = new GoldenFlowerComparator(cardList);
    }

    @Override
    public int compare(GoldenFlowerCardListWrapper o1, GoldenFlowerCardListWrapper o2) {
        return goldenFlowerComparator.compare(o1.getCardList(), o2.getCardList());
    }
}
