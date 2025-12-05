package com.themoneygame.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // наш фронт на Vite
                .allowedOrigins("http://localhost:5173")
                // какие методы разрешаем
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // какие заголовки принимаем
                .allowedHeaders("*")
                // чтобы работали куки/сессии
                .allowCredentials(true)
                // сколько кэшировать preflight
                .maxAge(3600);
    }
}
