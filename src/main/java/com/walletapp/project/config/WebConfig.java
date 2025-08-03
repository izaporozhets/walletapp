package com.walletapp.project.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Разрешаем все эндпоинты
                .allowedOrigins("http://localhost:3000") // Разрешаем фронт
                .allowedMethods("*") // Разрешаем все методы: GET, POST, PUT и т.д.
                .allowedHeaders("*") // Все заголовки
                .allowCredentials(true); // Разрешаем куки и auth headers, если надо
    }
}