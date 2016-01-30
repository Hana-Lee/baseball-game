package kr.co.leehana.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author Hana Lee
 * @since 2016-01-14 22-40
 */
@EnableWebSecurity
@Profile(value = {"dev", "test"})
public class DevWebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
//		http.csrf().disable();
//		http.httpBasic();
//		http.authorizeRequests().anyRequest().permitAll();

		httpSecurity.authorizeRequests().antMatchers("/").permitAll().and()
				.authorizeRequests().antMatchers("/console/**").permitAll();

		httpSecurity.csrf().disable();
		httpSecurity.headers().frameOptions().disable();
	}
}
