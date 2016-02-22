package kr.co.leehana.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

/**
 * @author Hana Lee
 * @since 2016-02-21 19:54
 */
@Configuration
@EnableWebSocketMessageBroker
@Profile(value = {"dev", "test"})
public class DevWebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		// subscribe prefix
		config.enableSimpleBroker("/topic");
		// send prefix
		config.setApplicationDestinationPrefixes("/app");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		// entry point
		registry.addEndpoint("/sock").withSockJS();
	}
}
