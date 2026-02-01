package com.salt.hed_admin.feature.user.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.salt.hed_admin.vo.TokenVO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserLoginVO {

    private Long id;

    private String userId;

    private String name;

    private String permissionType;

    private String subType;

    @JsonIgnore
    private TokenVO token;
}
