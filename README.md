# Spring security + JWT 테스트 토이프로젝트
Spring boot 4.0.x, JAVA21<br/>

## 기능
1. refresh token을 활용한 멀티로그인, 강제로그아웃, 토큰 재갱신
- 강제로그아웃은 토큰 테이블에 컬럼 값을 `TRUE`로 변경 시 해당 access token, refresh token이 모두 사용 불가되는 구성.
2. spring security 커스텀 활용
- CORS, Method 접근권한 설정
3. 간단 API 구현
4. Custom APIExcpetion
5. Controller Exception 처리
6. Swagger UI 간단 설정

## API
회원가입. 로그인. 로그아웃. 토큰 재발급

## 프로젝트 설명
- 후추 -
