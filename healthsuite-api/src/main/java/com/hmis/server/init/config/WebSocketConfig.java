package com.hmis.server.init.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


import static com.hmis.server.hmis.common.constant.HmisConfigConstants.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer { // AbstractWebSocketMessageBrokerConfigurer

	@Value( "${hmis.frontend.url}" )
	private String frontEndUrl;

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint(SOCKET_ENDPOINT).setAllowedOrigins(frontEndUrl).withSockJS();
	}


	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.enableSimpleBroker(SOCKET_TOPIC);
		registry.setApplicationDestinationPrefixes(SOCKET_DESTINATION_PREFIX);
	}
}
