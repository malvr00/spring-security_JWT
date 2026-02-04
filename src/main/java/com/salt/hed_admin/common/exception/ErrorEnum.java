package com.salt.hed_admin.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorEnum {

    /*
        USER_XXX
        유저관련 에러
     */
    USER_INFO_00(HttpStatus.CONFLICT, "usr(0) This user is already registered."),
    USER_INFO_01(HttpStatus.BAD_REQUEST, "usr(1) Password is different."),
    USER_INFO_02(HttpStatus.BAD_REQUEST, "usr(2) This user does not exist."),
    USER_INFO_03(HttpStatus.BAD_REQUEST, "usr(3) The permission setting is incorrect."),
    USER_INFO_04(HttpStatus.BAD_REQUEST, "usr(4) This is a duplicate email."),
    USER_INFO_05(HttpStatus.BAD_REQUEST, "usr(5) This is a login type that does not exist."),
    USER_INFO_06(HttpStatus.BAD_REQUEST, "usr(6) The hospital already has a registered account."),
    USER_INFO_07(HttpStatus.BAD_REQUEST, "usr(7) Invalid privilege selection"),

    USER_LOGIN_00(HttpStatus.UNAUTHORIZED, "lon(0) This user is blocked"),
    USER_LOGIN_01(HttpStatus.UNAUTHORIZED, "lon(1) Token information is incorrect."),
    USER_LOGIN_02(HttpStatus.UNAUTHORIZED, "lon(2) Please log in"),
    USER_LOGIN_03(HttpStatus.UNAUTHORIZED, "lon(3) Withdrawn user"),
    USER_LOGIN_04(HttpStatus.UNAUTHORIZED, "lon(4) Token information is not valid."),

    USER_FORBIDDEN_00(HttpStatus.FORBIDDEN, "fob(0) Access to the requested resource is forbidden."),
    USER_FORBIDDEN_01(HttpStatus.FORBIDDEN, "fob(1) Invalid JWT Token"),
    USER_FORBIDDEN_02(HttpStatus.FORBIDDEN, "fob(2) Expired JWT Token"),
    USER_FORBIDDEN_03(HttpStatus.FORBIDDEN, "fob(3) Unsupported JWT Token"),
    USER_FORBIDDEN_04(HttpStatus.FORBIDDEN, "fob(4) JWT Claims string is empty"),
    USER_FORBIDDEN_05(HttpStatus.FORBIDDEN, "fob(5) This is a blocked user"),
    USER_FORBIDDEN_06(HttpStatus.FORBIDDEN, "fob(6) Expired Refresh Token"),


    /*
        SERVER_XXX
        서버 내부 오류
     */
    SERVER_NOT_PERMISSION(HttpStatus.INTERNAL_SERVER_ERROR, "ser(0) Permission not registered"),
    SERVER_S3_NOT_CONNECT(HttpStatus.INTERNAL_SERVER_ERROR, "ser(1) Image save failed"),
    SERVER_S3_DELETE(HttpStatus.INTERNAL_SERVER_ERROR, "ser(2) Error deleting file from S3"),
    SERVER_S3_GET(HttpStatus.INTERNAL_SERVER_ERROR, "ser(3) Error get file from S3"),
    SERVER_S3_GET_IMAGE_SPEC(HttpStatus.INTERNAL_SERVER_ERROR, "ser(4) ImageSpec buffer fail"),

    SERVER_SSE_SUBSCRIBE(HttpStatus.INTERNAL_SERVER_ERROR, "ser(0) SSE subscribe fail!!!"),
    SERVER_SSE_COUNT(HttpStatus.INTERNAL_SERVER_ERROR, "ser(1) SSE count fail"),

    SERVER_KEY_IO_1(HttpStatus.INTERNAL_SERVER_ERROR, "sek(0) OAuth2 Key file IOException"),
    SERVER_KEY_IO_2(HttpStatus.INTERNAL_SERVER_ERROR, "sek(1) OAuth2 privateKey error"),
    SERVER_KEY_IO_3(HttpStatus.INTERNAL_SERVER_ERROR, "sek(2) OAuth2 publicKey error"),
    SERVER_KEY_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "sek(3) OAuth2 Key file not found"),
    SERVER_KEY_PARSE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "sek(4) OAuth2 token parse error"),

    SERVER_FIREBASE_KEY(HttpStatus.INTERNAL_SERVER_ERROR, "fir(0) Firebase secretKey error"),


    /*
        IMAGE_XXX
        이미지 관련 오류
     */
    IMAGE_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "img(0) Image not found"),
    IMAGE_NOT_FOUND_ID(HttpStatus.BAD_REQUEST, "img(1) Image ID not found"),
    IMAGE_WIDTH_ERROR(HttpStatus.BAD_REQUEST, "img(2) %s width error"),
    IMAGE_HEIGHT_ERROR(HttpStatus.BAD_REQUEST, "img(3) %s height error"),
    IMAGE_SIZE_ERROR(HttpStatus.BAD_REQUEST, "img(4) %s size error"),
    IMAGE_EXTENSION_ERROR(HttpStatus.BAD_REQUEST, "img(5) %s extension error"),

    /*
        VALID_XXX
        파라미터 관련 오류
     */
    VALID_BAD_REQUEST(HttpStatus.BAD_REQUEST, "Valid fail")
    ;


    private final HttpStatus httpStatus;
    private final String message;

    public String formatMessage(Object... args) {
        return String.format(message, args);
    }
}
