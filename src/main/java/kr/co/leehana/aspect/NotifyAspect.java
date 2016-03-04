package kr.co.leehana.aspect;

import kr.co.leehana.annotation.NotifyClients;
import kr.co.leehana.dto.MessagingDto;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/**
 * @author Hana Lee
 * @since 2016-03-05 01:23
 */
@Aspect
public class NotifyAspect {

	@Autowired
	private SimpMessagingTemplate template;

	@Pointcut(value = "@annotation(notifyClients)", argNames = "notifyClients")
	public void notifyPointcut(NotifyClients notifyClients) {
	}

	@Pointcut("execution(* kr.co.leehana.controller.*.*(..))")
	public void methodPointcut() {
	}

	@AfterReturning(
			value = "methodPointcut() && notifyPointcut(notifyClients)",
			argNames = "notifyClients,returnValue",
			returning = "returnValue")
	public void notifyClients(NotifyClients notifyClients, Object returnValue) {
		final String topicUrl = notifyClients.url();
		final String operation = notifyClients.operation();

		MessagingDto messagingDto = new MessagingDto();
		if (returnValue instanceof ResponseEntity) {
			ResponseEntity responseEntity = (ResponseEntity) returnValue;
			messagingDto.setData(responseEntity.getBody());
			messagingDto.setOperation(operation);
		}
		template.convertAndSend(topicUrl, messagingDto);
	}
}
