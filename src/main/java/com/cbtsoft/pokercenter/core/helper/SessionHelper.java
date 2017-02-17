package com.cbtsoft.pokercenter.core.helper;

import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

import java.security.Principal;

public class SessionHelper {
    public static String getUserName(SimpMessageHeaderAccessor simpMessageHeaderAccessor) {
        Principal principal = simpMessageHeaderAccessor.getUser();
        return principal.getName();
    }
}
