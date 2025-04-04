package com.kbsw.spring_jpa;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("/uploads/**") //웹에서 접근할 경러 설정 http://localhost:7777/upload/a.jpg2
                .addResourceLocations("file:///C:/fullstack/uploads/");
    }
}
