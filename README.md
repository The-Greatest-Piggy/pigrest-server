# 🐽 pigrest-server
Pigrest Server based on Spring Boot

## 🗂️ 패키지 구조
```text
pigrest-server/
├─ .github/
│  └─ workflows/                          # Github Actions 워크플로우
│     ├─ deploy-push.yaml                 # main 브랜치 push 시, 배포 파이프라인
│     └─ test-pull-request.yaml           # PR 생성/업데이트 시, 테스트 파이프라인
├─ src/
│  ├─ main/
│  │  ├─ java/
│  │  │  └─ app/
│  │  │     ├─ global/                    # 전역 설정/예외/보안/공통 유틸리티
│  │  │     │  ├─ common
│  │  │     │  ├─ config                            # CORS, Redis 설정
│  │  │     │  ├─ exception                         # Custom 예외 & 전역 예외 핸들러
│  │  │     │  └─ security                          # 스프링 시큐리티 구성, 필터 등
│  │  │     ├─ auth/                      # 인증/인가 관련 패키지
│  │  │     │  ├─ controller                        # API 엔드포인트
│  │  │     │  ├─ domain                            # 도메인 및 저장소
│  │  │     │  ├─ dto/                              # 요청/응답 DTO
│  │  │     │  │  ├─ request
│  │  │     │  │  └─ response
│  │  │     │  └─ service/                          # 서비스 및 토큰/세션 처리
│  │  │     │     ├─ AuthService.java
│  │  │     │     ├─ JwtService.java
│  │  │     │     └─ RedisService.java
│  │  │     ├─ content                    # 도메인: 핀, 보드 관련 패키지
│  │  │     └─ member                     # 도메인: 회원 관련 패키지
│  │  └─ resources/           # 애플리케이션 설정 (application-*.yaml)
│  │     └─ public            # openapi3.yaml 생성 장소
│  └─ test/
│     ├─ java                 # 테스트: Controller Test, Unit Test
│     └─ resources
└─ Dockerfile                 # 컨테이너 이미지 빌드 정의
```

## 🗒️ API 문서
해당 프로젝트는 `Spring REST Docs`를 사용하여 OpenAPI 3.0 스펙을 자동으로 생성하고, `Swagger UI`로 API 문서를 확인할 수 있습니다.

### ✏️ 사용 방법
1) **Controller Test 작성**: OpenAPI 스펙이 생성되려면, 각 Controller에 대한 테스트가 작성되어야 합니다.
2) **테스트 실행**: 테스트를 실행하면 `build/generated-snippets/` 하위에 API 문서 조각들이 생성됩니다.
3) **Swagger UI 접속**: 애플리케이션이 실행된 후, `http://localhost:8080/swagger-ui/index.html`로 접속하면 됩니다.

### ⚠️ 주의 
Swagger UI로 테스트 진행 시 먼저 로그인을 해야 합니다.
이후 인증이 필요한 API에 한하여, Authorization Header에 응답 값으로 받은 access token을 넣어줘야 진행이 가능합니다.
