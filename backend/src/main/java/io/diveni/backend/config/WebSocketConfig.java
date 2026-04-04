/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 Diveni Team, AUME-Team 21/22, HTWG Konstanz
*/
package io.diveni.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

import io.diveni.backend.handler.PrincipalWebSocketHandler;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Value("${SERVER_URL:#{null}}")
  private String SERVER_URL;

  private TaskScheduler messageBrokerTaskScheduler;

  @Autowired
  public void setMessageBrokerTaskScheduler(@Lazy TaskScheduler taskScheduler) {
    this.messageBrokerTaskScheduler = taskScheduler;
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry
        .enableSimpleBroker("/updates")
        .setHeartbeatValue(new long[] {10000, 10000})
        .setTaskScheduler(this.messageBrokerTaskScheduler);
    registry.setApplicationDestinationPrefixes("/ws");
    registry.setUserDestinationPrefix("/users");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry
        .addEndpoint("/connect")
        .setHandshakeHandler(new PrincipalWebSocketHandler())
        .setAllowedOrigins(SERVER_URL);
  }

  @Override
  public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
    registration
        .setMessageSizeLimit(128 * 1024)
        .setSendTimeLimit(15 * 1000)
        .setSendBufferSizeLimit(512 * 1024);
  }
}
