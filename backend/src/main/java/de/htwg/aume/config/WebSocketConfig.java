package de.htwg.aume.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import de.htwg.aume.handler.PrincipalWebSocketHandler;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.enableSimpleBroker("/updates");
		// prefix for client sending a websocket message
		registry.setApplicationDestinationPrefixes("/ws");
		registry.setUserDestinationPrefix("/users");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/connect").setHandshakeHandler(new PrincipalWebSocketHandler())
				.setAllowedOrigins("http://localhost:8080", "https://pp.vnmz.de").withSockJS();
	}
}
