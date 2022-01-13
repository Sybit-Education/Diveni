package de.htwg.aume.config;

import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
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

	// @Override
	// public boolean configureMessageConverters(List<MessageConverter>
	// messageConverters) {
	// MappingJackson2MessageConverter converter = new
	// MappingJackson2MessageConverter();
	// // Avoid creating many ObjectMappers which have the same configuration
	// ObjectMapper mapper = new ObjectMapper();
	// mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
	// // mapper.enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
	// converter.setObjectMapper(mapper);
	// messageConverters.add(converter);

	// // Don't add default converters.
	// return false;
	// }

}
