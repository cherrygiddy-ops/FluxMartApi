package com.fluxmartApi.uploads;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
            // Map URL path /uploads/** to the physical uploads folder
            registry.addResourceHandler("/uploads/**")
                    .addResourceLocations("file:uploads/");
        }

}