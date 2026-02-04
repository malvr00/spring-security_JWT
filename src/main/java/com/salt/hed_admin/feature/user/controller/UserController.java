package com.salt.hed_admin.feature.user.controller;

import com.salt.hed_admin.common.Const;
import com.salt.hed_admin.common.exception.ApiCustomException;
import com.salt.hed_admin.common.exception.ErrorEnum;
import com.salt.hed_admin.common.handler.TokenProvider;
import com.salt.hed_admin.feature.user.dto.UserLoginDto;
import com.salt.hed_admin.feature.user.dto.UserSaveDto;
import com.salt.hed_admin.feature.user.service.UserService;
import com.salt.hed_admin.feature.user.vo.UserLoginVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.salt.hed_admin.common.Router.API_VERSION.V1_BASE_PATH;
import static com.salt.hed_admin.common.Router.BASE_URL.ADMIN;
import static com.salt.hed_admin.common.Router.CERTIFIED_URI.*;
import static com.salt.hed_admin.common.Router.USER_URI.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(V1_BASE_PATH + ADMIN + BASE)
public class UserController {

    private final UserService userService;

    private final TokenProvider tokenProvider;

    /**
     * 간편 회원가입
     * @param dto 유저 정보
     * @return 회원가입 성공 시 회원 ID
     */
    @PostMapping(value = SIGN)
    public long signup(
            @Valid @RequestBody UserSaveDto dto
    ) {
        return userService.signup(dto);
    }

    /**
     * 간편 로그인
     * @param request HttpServletRequest.class
     * @param response HttpServletResponse.class
     * @param dto UserLoginDto.class
     * @return 로그인 성공 시 회원 ID
     */
    @PostMapping(value = LOGIN)
    public long login(
            HttpServletRequest request, HttpServletResponse response,
            @Valid @RequestBody UserLoginDto dto
    ) {

        UserLoginVO res = userService.login(dto);

        response.setHeader(Const.JWT.TOKEN_HEADER, res.getToken().getAccessToken());
        response.setHeader(Const.JWT.REFRESH_TOKEN_HEADER, res.getToken().getRefreshToken());

        return res.getId();
    }

    /**
     * 로그아웃
     * @param accessToken access token
     * @return user id
     */
    @PostMapping(value = LOGOUT)
    @PreAuthorize(value = "hasAnyRole('ROLE_ADMIN', 'ROLE_CS')")
    public long logout(
            @RequestHeader(Const.JWT.TOKEN_HEADER) String accessToken
    ) {
        if (accessToken == null) throw new ApiCustomException(ErrorEnum.VALID_BAD_REQUEST);
        return userService.logout(tokenProvider.getHeaderToken(accessToken).getAccessToken());
    }

    /**
     * 토큰 재발급
     * @param response HttpServletResponse.class
     * @param accessToken access token
     * @param refreshToken refresh token
     * @return user id
     */
    @PostMapping(value = REFRESH)
    @PreAuthorize(value = "hasAnyRole('ROLE_ADMIN', 'ROLE_CS')")
    public long refresh(
            HttpServletResponse response,
            @RequestHeader(Const.JWT.TOKEN_HEADER) String accessToken,
            @RequestHeader(Const.JWT.REFRESH_TOKEN_HEADER) String refreshToken
    ) {
        if(accessToken == null || refreshToken == null) throw new ApiCustomException(ErrorEnum.TOKEN_EMPTY_ERROR);

        String newAccessToken =
                userService.refreshToken(tokenProvider.getHeaderToken(accessToken).getAccessToken(), refreshToken);

        response.setHeader(Const.JWT.TOKEN_HEADER, newAccessToken);
        response.setHeader(Const.JWT.REFRESH_TOKEN_HEADER, refreshToken);

        return 0L;
    }
}
