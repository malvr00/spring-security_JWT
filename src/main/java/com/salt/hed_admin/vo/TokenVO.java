package com.salt.hed_admin.vo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TokenVO {

    private String accessToken;
    private String refreshToken;
    private String accessExpireTime;
    private String refreshExpireTime;

    public void updateAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

}
