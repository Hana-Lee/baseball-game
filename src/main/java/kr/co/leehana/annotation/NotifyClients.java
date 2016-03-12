package kr.co.leehana.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Spring RestController 에서 필요시 이 어노테이션을 이용하여 클라이언트에 WebSocket 으로 Notify 를 보낸다.</p>
 * <p>AspectJ 를 이용.</p>
 *
 * @author Hana Lee
 * @since 2016-03-05 01:21
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface NotifyClients {
	/**
	 * <p>(필수) topic 을 보낼 url 지정</p>
	 */
	String[] url();

	/**
	 * <p>(필수) webix 전용 operation</p>
	 * <p>{@link kr.co.leehana.enums.WebixOperation}</p>
 	 */
	String[] operation();
}
