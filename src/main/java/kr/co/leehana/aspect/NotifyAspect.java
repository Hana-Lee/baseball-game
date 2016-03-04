package kr.co.leehana.aspect;

import kr.co.leehana.annotation.NotifyClients;
import kr.co.leehana.dto.MessagingDto;
import kr.co.leehana.model.Player;
import kr.co.leehana.security.UserDetailsImpl;
import kr.co.leehana.service.PlayerService;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;

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

	@Pointcut("execution(* kr.co.leehana.controller.*.*(..))")
	public void methodPointcut() {
	}

	@AfterReturning(
			value = "methodPointcut() && notifyPointcut(notifyClients)",
			argNames = "notifyClients,returnValue",
			returning = "returnValue")
	public void notifyClients(NotifyClients notifyClients, Object returnValue) {
		final String[] topicUrl = notifyClients.url();
		final String[] operation = notifyClients.operation();

		for (int i = 0; i < topicUrl.length; i++) {
			MessagingDto messagingDto = new MessagingDto();
			if (returnValue instanceof ResponseEntity) {
				ResponseEntity responseEntity = (ResponseEntity) returnValue;

				// TODO 개선이 필요하다
				if (topicUrl[i].contains("player-list-updated")) {
					UserDetailsImpl joinPlayerImpl = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
							.getPrincipal();
					Player joinPlayer = playerService.getByEmail(joinPlayerImpl.getEmail());
					messagingDto.setData(joinPlayer);
				} else {
					messagingDto.setData(responseEntity.getBody());
				}
				messagingDto.setOperation(operation[i]);
			}
			template.convertAndSend(topicUrl[i], messagingDto);
		}
	}
}
