package com.cbtsoft.pokercenter.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {
    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
                .nullDestMatcher().authenticated()
                .simpSubscribeDestMatchers("/user/queue/errors").permitAll()
                .simpDestMatchers("/app/**").hasRole("USER")
                .simpSubscribeDestMatchers("/user/**", "/topic/**").hasRole("USER")
                .simpTypeMatchers(SimpMessageType.MESSAGE, SimpMessageType.SUBSCRIBE).denyAll()
                .anyMessage().denyAll();

    }

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }
}
