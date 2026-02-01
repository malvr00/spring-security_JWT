package com.salt.hed_admin.feature.user.controller;

import com.salt.hed_admin.common.Const;
import com.salt.hed_admin.feature.user.dto.UserLoginDto;
import com.salt.hed_admin.feature.user.dto.UserSaveDto;
import com.salt.hed_admin.feature.user.service.UserService;
import com.salt.hed_admin.feature.user.vo.UserLoginVO;
import com.salt.hed_admin.vo.ResultVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.salt.hed_admin.common.Router.API_VERSION.V1_BASE_PATH;
import static com.salt.hed_admin.common.Router.BASE_URL.ADMIN;
import static com.salt.hed_admin.common.Router.CERTIFIED_URI.LOGIN;
import static com.salt.hed_admin.common.Router.CERTIFIED_URI.SIGN;
import static com.salt.hed_admin.common.Router.USER_URI.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(V1_BASE_PATH + ADMIN + BASE)
public class UserController {

    private final UserService userService;

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
}
