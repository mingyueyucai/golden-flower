package com.cbtsoft.pokercenter.core.config;

import com.cbtsoft.pokercenter.core.factory.DealerFactory;
import com.cbtsoft.pokercenter.goldenflower.factory.GoldenFlowerDealerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {

    @Bean("goldenFlowerDealerFactory")
    public DealerFactory dealerFactory() {
        return new GoldenFlowerDealerFactory();
    }

    @Bean("initRoomNum")
    public Integer initRoomNumber() {
        return 10;
    }
}
