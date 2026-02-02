package com.salt.hed_admin.feature.user.service;

import com.salt.hed_admin.common.exception.ErrorEnum;
import com.salt.hed_admin.common.handler.TokenProvider;
import com.salt.hed_admin.config.Sha256;
import com.salt.hed_admin.domain.permission.PermissionGroup;
import com.salt.hed_admin.domain.permission.enums.PlatformType;
import com.salt.hed_admin.domain.token.Token;
import com.salt.hed_admin.domain.user.User;
import com.salt.hed_admin.feature.jwt.dto.JwtUserInfo;
import com.salt.hed_admin.feature.permission.repository.PermissionRepository;
import com.salt.hed_admin.feature.token.service.TokenService;
import com.salt.hed_admin.feature.user.dto.CustomUserDetails;
import com.salt.hed_admin.feature.user.dto.UserLoginDto;
import com.salt.hed_admin.feature.user.dto.UserSaveDto;
import com.salt.hed_admin.feature.user.repository.UserRepository;
import com.salt.hed_admin.feature.user.vo.UserLoginVO;
import com.salt.hed_admin.vo.TokenVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    @Value("${jwt.secret}")
    private String secret;

    private final TokenProvider tokenProvider;

    private final TokenService tokenService;

    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;

    /**
     * Spring security loadUserByUsername method
     *
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
     *
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

    /**
     * 유저 로그인
     * @param dto UserLoginDto.class
     * @return UserLoginVO.class
     */
    public UserLoginVO login(@Valid UserLoginDto dto) {
        User user = userRepository.findByUserId(dto.getUserId()).get();
        PermissionGroup permission = permissionRepository.findById(user.getPermissionId()).get();

        /*
         * ===========================================
         * ---------       검증 제외         ---------
         * ===========================================
         */

        TokenVO token = createToken(user, permission.getPermissionCategory().name());
        tokenService.save(Token.builder()
                .userId(user.getId())
                .platform(PlatformType.ADMIN)
                .refreshJti(token.getRefreshJti())
                .refreshHash(Sha256.hmacSha256(secret, token.getRefreshToken()))
                .refreshExpiresAt(Timestamp.from(Instant.parse(token.getRefreshExpireTime())))
                .revokedAt(new Timestamp(System.currentTimeMillis()))
                .build());

        return new UserLoginVO(user.getId(), user.getUserId(),
                user.getName(), permission.getPermissionCategory().name(), permission.getName(), token);
    }

    /**
     * 토큰 생성
     * @param user 유저 정보
     * @return TokenVO.class
     */
    protected TokenVO createToken(User user, String permissionType) {
        JwtUserInfo jwtUserInfo = JwtUserInfo.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .name(user.getName())
                .permissionType(permissionType)
                .build();

        return tokenProvider.createToken(jwtUserInfo);
    }

    /**
     * 로그아웃
     * @param accessToken 비활성화 대상
     * @return 로그아웃된 사용자 id
     */
    @Transactional
    public long logout(String accessToken) {
        Long id = tokenProvider.getId(accessToken);
        String sid = tokenProvider.getSid(accessToken);

        tokenService.updateRevoked(sid, id, PlatformType.ADMIN, true);

        return id;
    }
    
}
