package kr.co.leehana.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

/**
 * @author Hana Lee
 * @since 2016-02-24 20:28
 */
@Configuration
@Profile(value = {"dev"})
public class DevWebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

	@Override
	protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
		messages.nullDestMatcher().authenticated()
				.simpDestMatchers("/sock/**").hasRole("USER")
				.simpDestMatchers("/app/**").hasRole("USER")
				.simpSubscribeDestMatchers("/topic/**").hasRole("USER")
				.simpSubscribeDestMatchers("/user/**").hasRole("USER")
				.simpTypeMatchers(SimpMessageType.MESSAGE, SimpMessageType.SUBSCRIBE).denyAll()
				.anyMessage().denyAll();
	}

	// CSRF disable
	@Override
	protected boolean sameOriginDisabled() {
		return true;
	}
}
