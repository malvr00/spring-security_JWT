package com.salt.hed_admin.vo;

import com.salt.hed_admin.common.Const;
import com.salt.hed_admin.common.exception.ErrorEnum;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class ErrorVO implements Serializable {

    private String status;
    private int code;
    private String message;
    private Map<String, String> validError;
    private String timestamp;

    public static ResponseEntity<ErrorVO> toResponseEntity(ErrorEnum e, String format){
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(ErrorVO.builder()
                        .status(Const.RESPONSE_STATUS.FAIL)
                        .code(e.getHttpStatus().value())
                        .message(format == null ? e.getMessage() : e.getMessage().formatted(format))
                        .timestamp(LocalDateTime.now().toString())
                        .build()
                );
    }

    public static ResponseEntity<ErrorVO> toValidException(MethodArgumentNotValidException e, String message,
                                                                 Map<String, String> validError) {
        return ResponseEntity
                .status(e.getStatusCode())
                .body(ErrorVO.builder()
                        .status(Const.RESPONSE_STATUS.FAIL)
                        .code(e.getStatusCode().value())
                        .message(ErrorEnum.VALID_BAD_REQUEST.getMessage()+ " " + message)
                        .validError(validError)
                        .timestamp(LocalDateTime.now().toString())
                        .build());
    }
}
