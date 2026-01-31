package com.salt.hed_admin.feature.user.service;

import com.salt.hed_admin.common.exception.ErrorEnum;
import com.salt.hed_admin.domain.permission.PermissionGroup;
import com.salt.hed_admin.domain.user.User;
import com.salt.hed_admin.feature.jwt.dto.JwtUserInfo;
import com.salt.hed_admin.feature.permission.repository.PermissionRepository;
import com.salt.hed_admin.feature.user.dto.CustomUserDetails;
import com.salt.hed_admin.feature.user.dto.UserSaveDto;
import com.salt.hed_admin.feature.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;

    /**
     * Spring security loadUserByUsername method
     * @param username - Admin user pk
     * @return CustomUserDetails.class
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findById(Long.parseLong(username))
                .orElseThrow(() -> new UsernameNotFoundException(ErrorEnum.USER_INFO_02.getMessage()));
        PermissionGroup permissionGroup = permissionRepository.findById(user.getPermissionId())
                .orElseThrow(() -> new UsernameNotFoundException(ErrorEnum.USER_INFO_03.getMessage()));

        JwtUserInfo info = JwtUserInfo.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .name(user.getName())
                .password(user.getPassword())
                .state(user.getState())
                .subType(permissionGroup.getName())
                .permissionType(permissionGroup.getPermissionCategory().name())
                .build();

        return new CustomUserDetails(info);
    }

    /**
     * 회원가입
     * @param param UserSaveDto.class
     * @return 회원가입 성공 시 회원 ID
     */
    @Transactional
    public long signup(UserSaveDto param) {
        return userRepository.save(
                User.builder()
                        .userId(param.getUserId())
                        .password(param.getPassword())  // 편의상 암호화 제외
                        .name(param.getName())
                        .phone(param.getPhone())
                        .build()
        ).getId();
    }
}
