package com.salt.hed_admin.feature.jwt.dto;

import com.salt.hed_admin.domain.user.enums.UserStateEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class JwtUserInfo {

    private Long id;
    private String userId;
    private String name;
    private String password;
    private String permissionType;
    private String subType;
    private UserStateEnum state;

}
