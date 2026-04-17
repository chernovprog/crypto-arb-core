package com.achernov.cryptoarb.config;

import com.achernov.cryptoarb.dto.metadata.MetadataSection;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    registry.addViewController("/{path:[^.]*}")
            .setViewName("forward:/index.html");
  }

  @Override
  public void addFormatters(FormatterRegistry registry) {
    registry.addConverter(String.class, MetadataSection.class, MetadataSection::fromValue);
  }
}
