package com.achernov.cryptoarb.config;

import com.achernov.cryptoarb.config.properties.CorsProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private final CorsProperties properties;

  public WebSocketConfig(CorsProperties properties) {
    this.properties = properties;
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableSimpleBroker("/topic");
    config.setApplicationDestinationPrefixes("/app");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    StompWebSocketEndpointRegistration registration = registry.addEndpoint("/ws");

    List<String> origins = properties.allowedOrigins();

    if (!origins.isEmpty()) {
      registration.setAllowedOriginPatterns(origins.toArray(new String[0]));
    }
  }
}
