package com.salt.hed_admin.common.handler;

import com.salt.hed_admin.common.exception.ApiCustomException;
import com.salt.hed_admin.common.exception.ErrorEnum;
import com.salt.hed_admin.vo.ErrorVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class CustomExceptionHandler {

//    private final TokenProvider tokenProvider;

    /**
     * Custom Exception
     *
     * @param e - ApiCustomException
     * @return - error response
     */
    @ExceptionHandler(ApiCustomException.class)
    protected ResponseEntity<ErrorVO> handleCustomException(
            ApiCustomException e, HandlerMethod handlerMethod, HttpServletRequest request) {
        exactErrorLog(e, e.getErrorCode(), handlerMethod, request, e.getFormat());
        return ErrorVO.toResponseEntity(e.getErrorCode(), e.getFormat());
    }

    /**
     * Valid Exception
     *
     * @param e - MethodArgumentNotValidException
     * @return - error response
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorVO> processValidationError(
            MethodArgumentNotValidException e, HandlerMethod handlerMethod, HttpServletRequest request) {
        log.error("========= start method name = {} =========", handlerMethod.getMethod()
                .getDeclaringClass().getSimpleName());

        BindingResult bindingResult = e.getBindingResult();

        StringBuilder field = new StringBuilder();
        Map<String, String> validMessage = new HashMap<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            field.append(fieldError.getField() + ",");
            // 에러 메시지 {getDefaultMessage(), getRejectedValue()}
            validMessage.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        field.deleteCharAt(field.length() - 1);

        exactErrorLog(e, ErrorEnum.VALID_BAD_REQUEST, handlerMethod, request, null);
        return ErrorVO.toValidException(e, field.toString(), validMessage);
    }

    /**
     * Custom exception
     * @param e             - 에러 정보
     * @param errorEnum     - custom http
     * @param handlerMethod - handlerMethod
     * @param request       - servlet
     */
    private void exactErrorLog(Exception e, ErrorEnum errorEnum,
                               HandlerMethod handlerMethod, HttpServletRequest request, String format) {
        String errorDate = LocalTime.now().toString();
        String requestURI = request.getRequestURI();
        String exceptionName = e.getClass().getSimpleName();
        String status = errorEnum.getHttpStatus().toString();
        String controllerName = handlerMethod.getMethod().getDeclaringClass().getSimpleName();
        String methodName = handlerMethod.getMethod().getName();
        String message = format == null ? errorEnum.getMessage() : errorEnum.formatMessage(format);
        String lineNumber = String.valueOf(e.getStackTrace()[0].getLineNumber());

//        TokenVO cookiesToken = tokenProvider.getCookiesToken(request.getCookies(), type);
//
//        if (request.getRequestURI().contains(Const.BASE.ADMIN)) {
//            if (cookiesToken != null) {
//                exceptionSaveDB(errorEnum, request, message, cookiesToken);
//            }
//        }

        log.error("|  Time = {}", errorDate + " (UTC + 0)");
        log.error("|  Class = {}", controllerName);
        log.error("|  Method = {}", methodName);
        log.error("|  LineNumber = {}", lineNumber);
        log.error("|  Path = {}", requestURI);
        log.error("|  Exception = {}", exceptionName);
        log.error("|  Status = {}", status);
        log.error("|  message = {}", message);
        log.error("========= end method name = {} =========", methodName);
    }

}
