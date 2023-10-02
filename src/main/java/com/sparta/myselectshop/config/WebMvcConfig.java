package com.sparta.myselectshop.config;

import com.sparta.myselectshop.HandleInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  @Bean
  public HandleInterceptor handleInterceptor() {
    return new HandleInterceptor();
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(handleInterceptor()).addPathPatterns("/api/user/signup");
  }
}
