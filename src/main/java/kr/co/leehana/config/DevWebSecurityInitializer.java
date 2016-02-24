package kr.co.leehana.config;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.servlet.support.AbstractDispatcherServletInitializer;

/**
 * @author Hana Lee
 * @since 2016-02-24 20:52
 */
public class DevWebSecurityInitializer extends AbstractSecurityWebApplicationInitializer {

	@Override
	protected String getDispatcherWebApplicationContextSuffix() {
		return AbstractDispatcherServletInitializer.DEFAULT_SERVLET_NAME;
	}
}
