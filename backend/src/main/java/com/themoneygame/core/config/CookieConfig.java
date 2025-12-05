package com.themoneygame.core.config;

import org.springframework.boot.web.server.Cookie;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CookieConfig {

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> cookieCustomizer() {
        return (factory) -> factory.addContextCustomizers(context ->
                context.setCookieProcessor(new org.apache.tomcat.util.http.Rfc6265CookieProcessor())
        );
    }
}
