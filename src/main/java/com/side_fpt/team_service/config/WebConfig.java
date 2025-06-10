package com.side_fpt.team_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 프로젝트 static/images 아래 리소스
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");

        // 업로드된 투표 이미지 (D:/uploads 경로)
        registry.addResourceHandler("/uploaded/**")
                .addResourceLocations("file:///D:/uploads/");
    }
}
