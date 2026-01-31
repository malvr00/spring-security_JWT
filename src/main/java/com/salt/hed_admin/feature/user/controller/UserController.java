package com.salt.hed_admin.feature.user.controller;

import com.salt.hed_admin.feature.user.dto.UserSaveDto;
import com.salt.hed_admin.feature.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.salt.hed_admin.common.Router.API_VERSION.V1_BASE_PATH;
import static com.salt.hed_admin.common.Router.BASE_URL.ADMIN;
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
}
