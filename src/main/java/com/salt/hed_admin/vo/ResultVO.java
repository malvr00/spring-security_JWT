package com.salt.hed_admin.vo;

import com.salt.hed_admin.common.Const;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResultVO {

    private String status;

    private int code;

    private String message;

    private String timestamp;

    public ResultVO(String status, int code, String message, String timestamp) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.timestamp = timestamp;
    }

    public static ResultVO createSuccess(int httpCode, String message) {
        return new ResultVO(Const.RESPONSE_STATUS.SUCCESS, httpCode, message, LocalDateTime.now().toString());
    }

    /**
     * Error Response
     * @return ErrorVO class
     */
    public static ResultVO createError(int httpCode, String message) {
        return new ResultVO(Const.RESPONSE_STATUS.FAIL, httpCode, message, LocalDateTime.now().toString());
    }
}
