package kr.co.leehana;

import kr.co.leehana.aspect.NotifyAspect;
import kr.co.leehana.controller.GenerationNumberStrategy;
import kr.co.leehana.controller.RandomNumberGenerator;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSessionListener;

/**
 * @author Hana Lee
 * @since 2016-01-14 22-21
 */
@SpringBootApplication
@EnableAspectJAutoProxy
public class App extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		WebApplicationContext rootAppContext = createRootApplicationContext(servletContext);
		if (rootAppContext != null) {
			servletContext.addListener(rootAppContext.getBean(HttpSessionListener.class));
		}
		super.onStartup(servletContext);
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SessionRegistry sessionRegistry() {
		return new SessionRegistryImpl();
	}

	@Bean
	public AccessDeniedHandler accessDeniedHandler() {
		return new AccessDeniedHandlerImpl();
	}

	@Bean
	public HttpSessionListener httpSessionListener() {
		return new HttpSessionEventPublisher();
	}

	@Bean
	public NotifyAspect notifyAspect() {
		return new NotifyAspect();
	}

	@Bean
	public GenerationNumberStrategy generationNumberStrategy() {
		return new RandomNumberGenerator();
	}
}