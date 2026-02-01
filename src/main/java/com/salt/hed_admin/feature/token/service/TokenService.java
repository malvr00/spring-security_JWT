package com.salt.hed_admin.feature.token.service;

import com.salt.hed_admin.common.exception.ApiCustomException;
import com.salt.hed_admin.common.exception.ErrorEnum;
import com.salt.hed_admin.domain.permission.enums.PlatformType;
import com.salt.hed_admin.domain.token.Token;
import com.salt.hed_admin.feature.token.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;

    /**
     * 검증용 토큰 저장
     * @param token Token.class
     */
    @Transactional
    public void save(Token token) {
        tokenRepository.save(token);
    }

    /**
     * Refresh token 조회 및 검증
     * @param refreshJti Refresh sid
     * @param userId 사용자 ID
     * @param platform 플랫폼 타입
     * @return Token.class
     */
    public Token findByRefreshJtiAndUserIdAndPlatformAndRevokedFalse(String refreshJti,
                                                                     Long userId, PlatformType platform) {
        return tokenRepository.findByRefreshJtiAndUserIdAndPlatformAndRevokedFalse(refreshJti, userId, platform)
                .orElseThrow(() -> new ApiCustomException(ErrorEnum.USER_LOGIN_04));
    }

    /**
     * Revoked 변경
     * @param refreshJti Refresh sid
     * @param userId 사용자 ID
     * @param platform 플랫폼 타입
     */
    @Transactional
    public void updateRevoked(String refreshJti, Long userId, PlatformType platform, boolean isRevoked) {
        Token token = findByRefreshJtiAndUserIdAndPlatformAndRevokedFalse(refreshJti, userId, platform);

        token.updateRevoked(isRevoked);
    }
}
