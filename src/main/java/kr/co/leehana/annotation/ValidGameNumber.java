package kr.co.leehana.annotation;

import kr.co.leehana.validator.GameNumberValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>클라이언트에서 수비 플레이어가 생성한 숫자를 검증</p>
 * <p>만약 입력이 없어 객체가 null 일 경우 검증 값은 항상 true</p>
 *
 * <p>* 빈값을 입력했는가?</p>
 * <p>* 숫자만 입력했는가?</p>
 * <p>* 중복되지 않은 숫자인가?</p>
 * <p>* 랜덤 숫자 갯수 설정과 같은 자리수인가?</p>
 * <p>* 공백 입력은 자동 제거 처리.</p>
 *
 * @author Hana Lee
 * @since 2016-02-04 11:25
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = GameNumberValidator.class)
@Documented
public @interface ValidGameNumber {
	String message() default "Invalid game number";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
