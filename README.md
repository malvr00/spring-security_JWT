# Spring Security + JWT (Access/Refresh + DB 검증) 토이 프로젝트

Spring Boot 4.0.2 / Java 21 기반의 **Stateless JWT 인증/인가** 예제 프로젝트입니다.  
Access Token + Refresh Token을 발급하고, **Refresh 토큰을 DB에 저장(해시)하여 Access 토큰까지 검증**하는 방식으로
멀티 로그인/강제 로그아웃/토큰 재발급 흐름을 구성했습니다.

> 프로젝트 패키지: `com.salt.hed_admin`  
> Swagger UI: `/swagger-ui.html`  
> Health Check: `/actuator/health`

---

## 1) 기술 스택

- Java 21, Spring Boot 4.0.2
- Spring Security (Method Security 활성화)
- Spring Data JPA + MySQL
- JWT
  - `io.jsonwebtoken:jjwt (0.11.5)`
  - `com.auth0:java-jwt (3.13.0)` (의존성 포함)
  - `com.nimbusds:nimbus-jose-jwt (10.0.2)` (의존성 포함)
- Swagger: `springdoc-openapi-starter-webmvc-ui:3.0.1`

---

## 2) 핵심 기능

### (1) Access/Refresh 토큰 발급
- 로그인 성공 시
  - **Access Token**: 사용자 식별/권한 정보 포함
  - **Refresh Token**: `jti`(refresh id)를 발급하고 DB 검증 키로 사용

### (2) DB 기반 토큰 검증 (강제 로그아웃/차단)
- Access Token 내부에 `sid`(= refresh jti)를 넣어둠
- 요청 시 필터에서:
  1) JWT 서명/만료 검증
  2) DB에서 `refreshJti`를 조회해 **revoked 여부, refresh 만료 여부 확인**
- `revoked=true`로 바꾸면 **해당 sid 기반의 Access/Refresh 흐름을 즉시 차단** 가능

### (3) Refresh 토큰 해시 저장
- Refresh Token 원문을 저장하지 않고, `HMAC-SHA256(secret, refreshToken)` 형태로 해시 저장
- 재발급 요청 시 refreshToken을 다시 해시해서 DB의 `refreshHash`와 비교

### (4) Spring Security 커스텀
- `SessionCreationPolicy.STATELESS`
- FormLogin/BasicHttp 비활성화
- CORS 허용 + 응답 헤더 노출
  - `Authorization`, `X-Refresh-Token` 노출
- XSS Protection / CSP 헤더 설정
- `@EnableMethodSecurity(prePostEnabled = true)` + `@PreAuthorize` 사용

### (5) 공통 예외/응답 포맷
- `ApiCustomException` + `ErrorEnum` 기반 예외 처리
- Filter 레벨 JWT 오류 응답도 JSON으로 통일 (`ResultVO`)

---

## 3) 인증/인가 흐름 (요약)

### 로그인
1. `/v1/api/admin/users/login`
2. Access/Refresh 발급
3. 응답 헤더에 내려줌
   - `Authorization: <accessToken>`
   - `X-Refresh-Token: <refreshToken>`
4. 동시에 DB에 token row 저장
   - `refreshJti`
   - `refreshHash`
   - `refreshExpiresAt`
   - `revoked=false`

### 인증이 필요한 요청
1. `JwtAuthFilter`에서 `Authorization` 헤더 추출
2. JWT 유효성 검사
3. DB에서 `refreshJti` 조회 (revoked=false, refreshExpiresAt 검증)
4. 이상 없으면 `SecurityContext`에 인증 정보 세팅

### 토큰 재발급
1. `/v1/api/admin/users/refresh`
2. refreshToken 유효성(서명/만료) 검사
3. DB에서 refreshJti row 조회 + refreshHash 매칭
4. 새 Access Token 발급 후 `Authorization` 헤더로 내려줌

### 로그아웃(강제 차단)
1. `/v1/api/admin/users/logout`
2. sid(refreshJti) 기준으로 DB token row `revoked=true` 업데이트
3. 이후 기존 accessToken도 필터에서 DB 검증 단계에서 막힘

---

## 4) API

Base Path: `/v1/api/admin/users`

| Method | Path | Auth | 설명 |
|---|---|---|---|
| POST | `/signup` | Public | 회원가입 |
| POST | `/login` | Public | 로그인(토큰 발급) |
| POST | `/logout` | 필요 | 로그아웃(토큰 revoked) |
| POST | `/refresh` | 필요 | Access 토큰 재발급 |

> `logout`, `refresh`는 `@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CS')")` 적용
