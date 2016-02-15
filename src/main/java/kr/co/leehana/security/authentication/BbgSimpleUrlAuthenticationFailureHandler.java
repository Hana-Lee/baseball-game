package kr.co.leehana.security.authentication;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Hana Lee
 * @since 2016-02-14 20:02
 * <p>
 * defaultFailureUrl 을 null 값을 지정하여 login 페이지의 html 소스가 아닌 401 response 를 받기 위해
 * 기존의 simpleUrlAuthenticationFailureHandler 를 사용하지 않고 커스텀 Handler 를 등록
 * <p>
 * simpleUrlAuthenticationFailureHandler 는 null 값을 허용하지 않음
 */
@Component
public class BbgSimpleUrlAuthenticationFailureHandler implements AuthenticationFailureHandler {

	protected final Log logger = LogFactory.getLog(getClass());

	public BbgSimpleUrlAuthenticationFailureHandler() {
	}

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
	                                    AuthenticationException exception) throws IOException, ServletException {
		logger.debug("No failure URL set, sending 401 Unauthorized error");

		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication Failed: " + exception.getMessage());
	}
}
