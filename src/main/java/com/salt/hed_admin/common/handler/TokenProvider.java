package com.salt.hed_admin.common.handler;

import com.salt.hed_admin.common.Const;
import com.salt.hed_admin.common.exception.ApiCustomException;
import com.salt.hed_admin.common.exception.ErrorEnum;
import com.salt.hed_admin.domain.token.Token;
import com.salt.hed_admin.feature.jwt.dto.JwtUserInfo;
import com.salt.hed_admin.feature.token.repository.TokenRepository;
import com.salt.hed_admin.vo.TokenVO;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class TokenProvider {

    private final Key key;
    private final long expiration;
    private final long reExpiration;

    private final TokenRepository tokenRepository;

    public TokenProvider(
            @Value("${jwt.secret}") String key,
            @Value("${jwt.access.expiration}") long expiration,
            @Value("${jwt.refresh.expiration}") long reExpiration,
            Environment env, TokenRepository tokenRepository) {
        byte[] keyBytes = Decoders.BASE64.decode(key);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.expiration = expiration;
        this.reExpiration = reExpiration;
        this.tokenRepository = tokenRepository;
    }

    /**
     * JWT 생성
     * @param userInfo 유저 간단정보
     * @return JWT
     */
    public TokenVO createToken(JwtUserInfo userInfo) {
        String refreshJti = UUID.randomUUID().toString();
        Date accessTokenValidity = getExpiration(true);
        Date refreshTokenValidity = getExpiration(false);

        String accessToken = createAccessToken(userInfo, refreshJti, accessTokenValidity);
        String refreshToken = createRefreshToken(userInfo.getId(), refreshJti, refreshTokenValidity);

        return new TokenVO(accessToken, refreshToken,
                accessTokenValidity.toInstant().toString(), refreshTokenValidity.toInstant().toString());
    }

    /**
     * created JWT access token
     * @param userInfo 유저 간단 정보
     * @return Access token
     */
    private String createAccessToken(JwtUserInfo userInfo, String refreshJti, Date tokenValidity) {
        Claims claims = Jwts.claims();
        claims.put(Const.JWT.CLAIM_ID, userInfo.getId());
        claims.put(Const.JWT.CLAIM_SID, refreshJti);
        claims.put(Const.JWT.CLAIM_USER_ID, userInfo.getUserId());
        claims.put(Const.JWT.CLAIM_USERNAME, userInfo.getName());
        claims.put(Const.JWT.CLAIM_ROLES, userInfo.getPermissionType());

        return Jwts.builder()
                .setClaims(claims)
                .setId(UUID.randomUUID().toString())
                .setSubject(userInfo.getId().toString())
                .setIssuedAt(Date.from(ZonedDateTime.now(ZoneOffset.UTC).toInstant()))
                .setExpiration(Date.from(tokenValidity.toInstant()))
                .signWith(key)
                .compact();

    }

    /**
     * created JWT refresh token
     * @return Refresh token
     */
    private String createRefreshToken(Long id, String refreshJti, Date tokenValidity) {
        return Jwts.builder()
                .setSubject(id.toString())
                .setId(refreshJti)
                .claim(Const.JWT.CLAIM_TYPE, Const.JWT.REFRESH_TOKEN)
                .setIssuedAt(Date.from(ZonedDateTime.now(ZoneOffset.UTC).toInstant()))
                .setExpiration(Date.from(tokenValidity.toInstant()))
                .signWith(key)
                .compact();

    }

    /**
     * Expiration time ( Seconds basis )
     * @param isAccessExpire - true: access, false: refresh
     * @return Date
     */
    public Date getExpiration(boolean isAccessExpire) {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        ZonedDateTime tokenValidity = null;

        if (isAccessExpire) {
            tokenValidity = now.plusSeconds(expiration);
        } else {
            tokenValidity = now.plusSeconds(reExpiration);
        }

        return Date.from(tokenValidity.toInstant());
    }

    /**
     * System API endpoint token
     * @param headerToken accessToken
     * @return Token information
     */
    public TokenVO getHeaderToken(String headerToken) {
        if(headerToken == null) return null;

        if (headerToken.startsWith(Const.JWT.BEARER)) {
            headerToken = headerToken.substring(7);
        }

        return new TokenVO(headerToken, null,
                null, null);
    }

    /**
     * JWT Get claims
     * @param accessToken 토큰
     * @return JWT Claims
     */
    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    /**
     * Token ID(User Domain pk) 추출
     * @param token access token
     * @return ID
     */
    public Long getId(String token) {
        return parseClaims(token).get(Const.JWT.CLAIM_ID, Long.class);
    }

    /**
     * Token SID(Refresh token id)
     * @param token access token
     * @return ID
     */
    public String getSid(String token) {
        return parseClaims(token).get(Const.JWT.CLAIM_SID, String.class);
    }

    /**
     * Access Token DB check
     * @param token Access Token
     */
    public void checkAccessToken(String token) {
        String sid = getSid(token);
        Token dbToken = tokenRepository.findByRefreshJtiAndRevokedFalse(sid)
                    .orElseThrow(() -> new ApiCustomException(ErrorEnum.USER_LOGIN_04));

        if (dbToken.getRefreshExpiresAt().before(Timestamp.valueOf(LocalDateTime.now()))) {
            throw new ApiCustomException(ErrorEnum.USER_LOGIN_04);
        }
    }

    /**
     * Delete the token data
     */
    @Transactional
    public void deleteToken(String refreshJti) {
        tokenRepository.deleteByRefreshJti(refreshJti);
    }

    /**
     * JWT 검증
     * @param token access token
     */
    public void isTokenValid(String token) throws ExpiredJwtException {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.error("잘못된 JWT 서명입니다. (Invalid JWT Token)", e);
            throw new MalformedJwtException(e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰 입니다. (Expired JWT Token)", e);
            throw new ExpiredJwtException(e.getHeader(), e.getClaims(), e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰입니다.(Unsupported JWT Token)", e);
            throw new UnsupportedJwtException(e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT 토큰이 잘못되었습니다.(JWT Claims string is empty)", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
