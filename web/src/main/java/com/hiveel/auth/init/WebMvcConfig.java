package com.hiveel.auth.init;


import com.hiveel.auth.controller.formatter.LocalDateTimeFormatter;
import com.hiveel.auth.controller.interceptor.AuthInterceptor;
import com.hiveel.auth.controller.interceptor.MeInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDateTime;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor())
                .addPathPatterns("/**").excludePathPatterns("/*.html")
                .excludePathPatterns("/api/**", "/login", "/logout", "/token/refresh", "/account/check","/forget/**", "/forget").order(1);
        registry.addInterceptor(new MeInterceptor()).addPathPatterns("/me/**", "/me").order(2);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatterForFieldType(LocalDateTime.class, new LocalDateTimeFormatter());
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedMethods("*").allowedOrigins("*").allowedHeaders("*");
    }


}
