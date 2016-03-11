package kr.co.leehana.aspect;

import kr.co.leehana.annotation.NotifyClients;
import kr.co.leehana.dto.MessagingDto;
import kr.co.leehana.dto.PlayerDto;
import kr.co.leehana.model.Player;
import kr.co.leehana.security.UserDetailsImpl;
import kr.co.leehana.service.PlayerService;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;

import java.lang.annotation.Annotation;

/**
 * @author Hana Lee
 * @since 2016-03-05 01:23
 */
@Aspect
public class NotifyAspect {

	@Autowired
	private SimpMessagingTemplate template;

	@Autowired
	private PlayerService playerService;

	@Pointcut(value = "@annotation(notifyClients)", argNames = "notifyClients")
	public void notifyPointcut(NotifyClients notifyClients) {
	}

	@Pointcut(value = "execution(* kr.co.leehana.controller.*.*(..))")
	public void methodPointcut() {
	}

	@AfterReturning(
			value = "methodPointcut() && notifyPointcut(notifyClients)",
			argNames = "joinPoint,notifyClients,returnValue",
			returning = "returnValue")
	public void notifyClients(JoinPoint joinPoint, NotifyClients notifyClients, Object returnValue) throws
			NoSuchMethodException {
		final String[] topicUrl = notifyClients.url();
		final String[] operation = notifyClients.operation();

		for (int i = 0; i < topicUrl.length; i++) {
			MessagingDto messagingDto = new MessagingDto();
			if (returnValue instanceof ResponseEntity) {
				ResponseEntity responseEntity = (ResponseEntity) returnValue;

				// TODO 개선이 필요하다
				if (topicUrl[i].contains("player/list/updated")) {
					UserDetailsImpl joinPlayerImpl = (UserDetailsImpl) SecurityContextHolder.getContext()
							.getAuthentication().getPrincipal();
					Player joinPlayer = playerService.getByEmail(joinPlayerImpl.getEmail());
					messagingDto.setData(joinPlayer);
				} else {
					messagingDto.setData(responseEntity.getBody());
				}
			} else if (returnValue instanceof PlayerDto.Response) {
				PlayerDto.Response responseDto = (PlayerDto.Response) returnValue;
				messagingDto.setData(responseDto);
			}

			messagingDto.setOperation(operation[i]);

			String url;
			if (topicUrl[i].contains("{id}")) {
				String idValue = getIdValue(joinPoint);
				url = topicUrl[i].replace("{id}", idValue);
			} else {
				url = topicUrl[i];
			}
			template.convertAndSend(url, messagingDto);
		}
	}

	private String getIdValue(JoinPoint joinPoint) {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		String[] parameterNames = signature.getParameterNames();
		Class[] parameterTypes = signature.getParameterTypes();
		Annotation[][] parameterAnnotations = signature.getMethod().getParameterAnnotations();

		String idValue = "";
		for (int i = 0; i < parameterAnnotations.length; i++) {
			for (int j = 0; j < parameterAnnotations[i].length; j++) {
				if (parameterAnnotations[i][j].annotationType().getName().equals(PathVariable.class.getName())) {
					if (parameterNames[i].equals("id") && parameterTypes[i].getName().equals(Long.class.getName())) {
						idValue = String.valueOf(joinPoint.getArgs()[i]);
					}
					break;
				}
			}
			if (StringUtils.isNotBlank(idValue)) {
				break;
			}
		}

		return idValue;
	}
}
