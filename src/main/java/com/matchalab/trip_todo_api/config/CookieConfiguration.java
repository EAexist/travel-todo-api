package com.matchalab.trip_todo_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.security.web.authentication.SpringSessionRememberMeServices;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
public class CookieConfiguration {

    @Bean
    DefaultCookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();

        // SameSite="None"을 사용하려면 Secure도 true로 설정해야 합니다.
        serializer.setSameSite("None");
        serializer.setUseSecureCookie(true);
        serializer.setRememberMeRequestAttribute(SpringSessionRememberMeServices.REMEMBER_ME_LOGIN_ATTR);

        // (선택 사항) Remember-Me 토큰 유효 기간도 설정할 수 있습니다.
        // Spring Security의 rememberMe().tokenValiditySeconds()와 통합됨
        // serializer.setRememberMeRequestAttribute(org.springframework.session.web.authentication.SpringSessionRememberMeServices.REMEMBER_ME_LOGIN_ATTR);

        return serializer;
    }
}