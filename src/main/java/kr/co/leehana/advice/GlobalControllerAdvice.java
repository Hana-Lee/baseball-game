package kr.co.leehana.advice;

import kr.co.leehana.dto.MessagingDto;
import kr.co.leehana.exception.ErrorResponse;
import kr.co.leehana.exception.GameRoleDuplicatedException;
import kr.co.leehana.exception.GameRoomNotFoundException;
import kr.co.leehana.exception.GameRoomPlayerNotFoundException;
import kr.co.leehana.exception.GameRoomPlayersNotEmpty;
import kr.co.leehana.exception.GameRoomRunningException;
import kr.co.leehana.exception.GameRoomUpdateFieldAllEmptyException;
import kr.co.leehana.exception.OwnerChangeException;
import kr.co.leehana.exception.OwnerDuplicatedException;
import kr.co.leehana.exception.PlayerDuplicatedException;
import kr.co.leehana.exception.PlayerNotFoundException;
import kr.co.leehana.exception.PlayerNotLoggedInException;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * @author Hana Lee
 * @since 2016-03-18 19:39
 */
@RestControllerAdvice(basePackages = "kr.co.leehana.controller")
public class GlobalControllerAdvice {

	@ExceptionHandler(value = {MethodArgumentNotValidException.class})
	public ResponseEntity<ErrorResponse> handlerMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		final ErrorResponse errorResponse = new ErrorResponse();
		String message;
		if (e.getBindingResult().getFieldError() != null) {
			message = e.getBindingResult().getFieldError().getDefaultMessage();
		} else if (e.getBindingResult().getGlobalError() != null) {
			message = e.getBindingResult().getGlobalError().getDefaultMessage();
		} else {
			message = "DTO Object binding error";
		}
		errorResponse.setMessage(message);
		errorResponse.setErrorCode("bad.request");
		return new ResponseEntity<>(errorResponse, BAD_REQUEST);
	}

	@ExceptionHandler(PlayerDuplicatedException.class)
	@ResponseStatus(BAD_REQUEST)
	public ErrorResponse handlePlayerDuplicatedException(PlayerDuplicatedException e) {
		return createErrorResponse(e.getMessage(), e.getErrorCode());
	}

	@ExceptionHandler(PlayerNotFoundException.class)
	@ResponseStatus(BAD_REQUEST)
	public ErrorResponse handlePlayerNotFoundException(PlayerNotFoundException e) {
		return createErrorResponse(e.getMessage(), e.getErrorCode());
	}

	@ExceptionHandler(PlayerNotLoggedInException.class)
	@ResponseStatus(BAD_REQUEST)
	public ErrorResponse handlePlayerNotLoggedInException(PlayerNotLoggedInException e) {
		return createErrorResponse(e.getMessage(), e.getErrorCode());
	}

	@ExceptionHandler(GameRoleDuplicatedException.class)
	@ResponseStatus(BAD_REQUEST)
	public ErrorResponse handleGameRoleDuplicatedException(GameRoleDuplicatedException ex) {
		return createErrorResponse(ex.getMessage(), ex.getErrorCode());
	}

	@ExceptionHandler(OwnerDuplicatedException.class)
	@ResponseStatus(BAD_REQUEST)
	public ErrorResponse handleOwnerDuplicatedException(OwnerDuplicatedException ex) {
		return createErrorResponse(ex.getMessage(), ex.getErrorCode());
	}

	@ExceptionHandler(GameRoomNotFoundException.class)
	@ResponseStatus(BAD_REQUEST)
	public ErrorResponse handleGameRoomNotFoundException(GameRoomNotFoundException ex) {
		return createErrorResponse(ex.getMessage(), ex.getErrorCode());
	}

	@ExceptionHandler(OwnerChangeException.class)
	@ResponseStatus(BAD_REQUEST)
	public ErrorResponse handleOwnerChangeException(OwnerChangeException ex) {
		return createErrorResponse(ex.getMessage(), ex.getErrorCode());
	}

	@ExceptionHandler(GameRoomPlayerNotFoundException.class)
	@ResponseStatus(BAD_REQUEST)
	public ErrorResponse handleGameRoomPlayerNotFoundException(GameRoomPlayerNotFoundException ex) {
		return createErrorResponse(ex.getMessage(), ex.getErrorCode());
	}

	@ExceptionHandler(GameRoomPlayersNotEmpty.class)
	@ResponseStatus(BAD_REQUEST)
	public ErrorResponse handleGameRoomPlayersNotEmptyException(GameRoomPlayersNotEmpty ex) {
		return createErrorResponse(ex.getMessage(), ex.getErrorCode());
	}

	@ExceptionHandler(GameRoomRunningException.class)
	@ResponseStatus(BAD_REQUEST)
	public ErrorResponse handleGameRoomAlreadyRunningException(GameRoomRunningException ex) {
		return createErrorResponse(ex.getMessage(), ex.getErrorCode());
	}

	@ExceptionHandler(GameRoomUpdateFieldAllEmptyException.class)
	@ResponseStatus(BAD_REQUEST)
	public ErrorResponse handleGameRoomUpdateFieldAllEmptyException(GameRoomUpdateFieldAllEmptyException ex) {
		return createErrorResponse(ex.getMessage(), ex.getErrorCode());
	}

	private ErrorResponse createErrorResponse(String message, String errorCode) {
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setMessage(message);
		errorResponse.setErrorCode(errorCode);
		return errorResponse;
	}

	/**
	 * {code @Valid} 어노테이션에 의해 검증 오류가 발생하면 처리 하는 핸들러
	 *
	 * @param exception MethodArgumentNotValidException Class 검증 오류
	 * @return 검증 오류의 메세지를 담은 객체
	 */
	@MessageExceptionHandler(value = {org.springframework.messaging.handler.annotation.support
			.MethodArgumentNotValidException.class})
	@SendToUser(value = {"/topic/errors"}, broadcast = false)
	public MessagingDto handleMethodArgumentNotValidException(org.springframework.messaging.handler.annotation.support
			                                                              .MethodArgumentNotValidException exception) {
		String message = null;
		if (exception.getBindingResult().getGlobalError() != null) {
			message = exception.getBindingResult().getGlobalError().getDefaultMessage();
		} else if (exception.getBindingResult().getFieldError() != null) {
			message = exception.getBindingResult().getFieldError().getDefaultMessage();
		}

		MessagingDto dto = new MessagingDto();
		dto.setErrorMessage(message);
		dto.setErrorCode(exception.getBindingResult().getGlobalError().getCode());
		return dto;
	}
}
