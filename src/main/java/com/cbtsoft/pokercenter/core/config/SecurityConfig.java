package com.cbtsoft.pokercenter.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .inMemoryAuthentication()
                    .withUser("internjiang").password("internjiang").roles("USER").and()
                    .withUser("bosscang").password("bosscang").roles("USER").and()
                    .withUser("managerzhou").password("managerzhou").roles("USER").and()
                    .withUser("didicout").password("didicout").roles("USER").and()
                    .withUser("juyuan").password("juyuan").roles("USER");
    }
}
