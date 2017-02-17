package com.cbtsoft.pokercenter.goldenflower.factory;

import com.cbtsoft.pokercenter.core.factory.DealerFactory;
import com.cbtsoft.pokercenter.core.model.Dealer;
import com.cbtsoft.pokercenter.goldenflower.model.GoldenFlowerDealer;

public class GoldenFlowerDealerFactory implements DealerFactory {
    @Override
    public Dealer createDealer() {
        return new GoldenFlowerDealer();
    }
}
