package com.salt.hed_admin.common.filter;

import com.salt.hed_admin.common.Const;
import com.salt.hed_admin.common.exception.ApiCustomException;
import com.salt.hed_admin.common.exception.ErrorEnum;
import com.salt.hed_admin.common.handler.TokenProvider;
import com.salt.hed_admin.feature.user.service.UserService;
import com.salt.hed_admin.vo.ResultVO;
import com.salt.hed_admin.vo.TokenVO;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final TokenProvider tokenProvider;

    public JwtAuthFilter(UserService userService, TokenProvider tokenProvider) {
        this.userService = userService;
        this.tokenProvider = tokenProvider;
    }

    /**
     * JWT 토큰 검증 필터
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        TokenVO tokenInfo = tokenProvider.getHeaderToken(request.getHeader(Const.JWT.TOKEN_HEADER));

        if (tokenInfo.getAccessToken() == null || tokenInfo.getAccessToken().isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = tokenInfo.getAccessToken();

            /*
                JWT 유효성 검증
             */
            tokenProvider.isTokenValid(token);
            /*
                Access Token check
             */
            if (filterTokenCheck(response, token)) return;

            Long id = tokenProvider.getId(token);
            createSecurityToken(response, userService, id);

        /*
            JWT Exception Controller MalformedJwtException
         */
        } catch (MalformedJwtException e) {
            createResponseError(response, ErrorEnum.USER_FORBIDDEN_01.getHttpStatus(),
                    ErrorEnum.USER_FORBIDDEN_01.getMessage());
            return;
        } catch (ExpiredJwtException e) {
            createResponseError(response, ErrorEnum.USER_FORBIDDEN_02.getHttpStatus(),
                    ErrorEnum.USER_FORBIDDEN_02.getMessage());
            return;
        } catch (UnsupportedJwtException e) {
            createResponseError(response, ErrorEnum.USER_FORBIDDEN_03.getHttpStatus(),
                    ErrorEnum.USER_FORBIDDEN_03.getMessage());
            return;
        } catch (IllegalArgumentException e) {
            createResponseError(response, ErrorEnum.USER_FORBIDDEN_04.getHttpStatus(),
                    ErrorEnum.USER_FORBIDDEN_04.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * AccessToken, RefreshToken check
     * @param response - servlet
     * @param token - AccessToken || RefreshToken
     * @return boolean
     */
    protected boolean filterTokenCheck(HttpServletResponse response, String token) throws IOException {
        try {
            tokenProvider.checkAccessToken(token);
        } catch (ApiCustomException e) {
            createResponseError(response, e.getErrorCode().getHttpStatus(), e.getErrorCode().getMessage());
            return true;
        }
        return false;
    }

    /**
     * Response message custom
     */
    protected void createResponseError(HttpServletResponse response, HttpStatus status, String message)
            throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        ResultVO errorResponse = ResultVO.createError(status.value(), message);
        String body = objectMapper.writeValueAsString(errorResponse);
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(body);
    }

    /**
     * Create UsernamePasswordAuthenticationToken
     * @param response - HttpServletResponse
     * @param userService - Services according to authority
     * @param id - Access token CLAIM_ID
     */
    protected void createSecurityToken(HttpServletResponse response,
                                       UserService userService, Long id) throws IOException {
        UserDetails userDetails = userService.loadUserByUsername(id.toString());

        if (!userDetails.isEnabled()) {
            createResponseError(response, ErrorEnum.USER_LOGIN_00.getHttpStatus(), ErrorEnum.USER_LOGIN_00.getMessage());
            return;
        }

        if (!userDetails.isAccountNonLocked()) {
            createResponseError(response, ErrorEnum.USER_FORBIDDEN_05.getHttpStatus(),
                    ErrorEnum.USER_FORBIDDEN_05.getMessage());
            return;
        }

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null,
                        userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }
}
