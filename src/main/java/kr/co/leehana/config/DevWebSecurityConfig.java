package kr.co.leehana.config;

import kr.co.leehana.controller.GameRoomController;
import kr.co.leehana.controller.PlayerController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

/**
 * @author Hana Lee
 * @since 2016-01-14 22-40
 */
@EnableWebSecurity
@Profile(value = {"dev"})
public class DevWebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private AuthenticationFailureHandler bbgUrlAuthenticationFailureHandler;

	@Autowired
	private LogoutSuccessHandler logoutSuccessHandler;

	@Autowired
	private SessionRegistry sessionRegistry;

	@Autowired
	private AccessDeniedHandler accessDeniedHandler;

	@Autowired
	private AuthenticationEntryPoint authenticationEntryPoint;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.authorizeRequests()
				.antMatchers("/console/**").hasRole("USER")
				.antMatchers(HttpMethod.DELETE, PlayerController.URL_VALUE + "/**").hasRole("USER")
				.antMatchers(HttpMethod.GET, PlayerController.URL_VALUE + "/**").hasRole("USER")
				.antMatchers(HttpMethod.PUT, PlayerController.URL_VALUE + "/**").hasRole("USER")
				.antMatchers(HttpMethod.DELETE, GameRoomController.URL_VALUE + "/**").hasRole("ADMIN")
				.antMatchers(HttpMethod.GET, GameRoomController.URL_VALUE + "/**").hasRole("USER")
				.antMatchers(HttpMethod.PUT, GameRoomController.URL_VALUE + "/**").hasRole("USER")
				.antMatchers(HttpMethod.POST, GameRoomController.URL_VALUE + "/**").hasRole("USER")
				.anyRequest().permitAll();

		httpSecurity.exceptionHandling().accessDeniedHandler(accessDeniedHandler).authenticationEntryPoint(authenticationEntryPoint);

		httpSecurity.formLogin()
				.usernameParameter("email").passwordParameter("password")
				.failureHandler(bbgUrlAuthenticationFailureHandler);

		httpSecurity.logout()
				.invalidateHttpSession(true)
				.clearAuthentication(true)
				.logoutSuccessHandler(logoutSuccessHandler);

		httpSecurity.csrf().disable();

		httpSecurity.headers().frameOptions().disable();

		httpSecurity.sessionManagement()
				.maximumSessions(1)
				.maxSessionsPreventsLogin(true)
				.sessionRegistry(sessionRegistry);
	}
}
