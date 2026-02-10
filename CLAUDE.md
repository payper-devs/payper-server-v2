# Payper Server v2

## Policy
- 이미 작성되어 있는 기존 코드(사용자 또는 외부에서 작성된 것으로 보이는 코드)는 사용자의 허가나 직접적인 명령이 있기 전까지 수정하지 않는다.
- 기존 코드의 수정이 필요하다고 판단되는 경우, 반드시 사용자에게 먼저 확인을 받는다.

## Tech Stack
- **Framework**: Spring Boot 4.0.1 (Java 21)
- **ORM**: Spring Data JPA (Hibernate) + MySQL
- **Auth**: JWT (JJWT 0.12.6) + Kakao OAuth
- **Security**: Spring Security (Stateless, Bearer Token)
- **Build**: Gradle 9.2.1
- **Dev Tools**: Lombok

## Build & Run
```bash
./gradlew build           # 빌드
./gradlew bootRun         # 로컬 실행 (MySQL localhost:3306/payper_v2 필요)
./gradlew test            # 테스트 실행
```

## Project Structure
```
com.payper.server
├── auth/                  # 인증 (OAuth, JWT 발급/재발급/로그아웃)
│   ├── jwt/               #   JWT 엔티티, 유틸리티, 리포지토리
│   └── util/              #   OAuth 유틸리티 (Kakao)
├── comment/               # 댓글 도메인 (CRUD, 대댓글, 커서 페이지네이션)
├── post/                  # 게시글 도메인 (CRUD, 오프셋 페이지네이션)
├── merchant/              # 가맹점 도메인
├── favorite/              # 즐겨찾기 도메인
├── user/                  # 사용자 도메인
├── security/              # Spring Security 설정, JWT 필터
├── global/                # 공통 (BaseTimeEntity, ApiResponse, ErrorCode, 예외처리)
└── domain/test/           # 테스트 컨트롤러
```

## Package Convention (Feature-based)
```
[feature]/
├── controller/            # REST 엔드포인트
├── service/               # 비즈니스 로직 (@Service @Transactional)
├── repository/            # JPA Repository
├── entity/                # JPA 엔티티
└── dto/
    ├── request/           # 요청 DTO (@Valid)
    └── response/          # 응답 DTO
```

## Key Patterns

### API Response
- 모든 응답은 `ApiResponse<T>` 래퍼 사용 (`global/response/ApiResponse.java`)
- 에러는 `ErrorCode` enum 기반 (`global/response/ErrorCode.java`)
- 예외는 `ApiException` 또는 `AuthException` 사용

### Entity
- `BaseTimeEntity` 상속으로 `createdAt`, `updatedAt` 자동 관리
- 엔티티 생성은 `static create()` 팩토리 메서드 사용
- 모든 연관관계는 `FetchType.LAZY` + 필요 시 `join fetch` JPQL

### Soft Delete
- Post, Comment에 적용: `isDeleted` + `deletedAt` 필드
- 게시글 삭제 시 댓글도 소프트 딜리트 (cascade)
- 삭제된 댓글은 자식이 있으면 "[삭제된 댓글입니다]"로 표시

### Pagination
- **게시글**: 오프셋 기반 (`Page<T>`, page/size 파라미터)
- **댓글**: 커서 기반 (`Slice<T>`, cursorId/size 파라미터)

### Auth Flow
1. 클라이언트 → `/auth/login` (oauthToken)
2. Kakao OAuth 검증 → User 조회/생성
3. Access Token (헤더) + Refresh Token (HttpOnly 쿠키) 발급
4. 인증 필요 API: `Authorization: Bearer <accessToken>`
5. 토큰 만료 시: `/auth/reissue` (쿠키의 refresh token 사용)

### Security
- 공개 API: GET `/api/v1/posts/**`, GET `/api/v1/comments/*/replies`, `/auth/**`
- 인증 필요: POST/PUT/DELETE `/api/v1/posts/**`, `/api/v1/comments/**`
- 관리자: `/admin/**`

### Validation
- 입력: Jakarta Bean Validation (`@NotBlank`, `@Size` 등)
- 비즈니스: 서비스 레이어에서 권한/상태 검증
- 에러 응답: `FieldErrorDto` 리스트 반환

### Transaction
- 서비스 클래스에 `@Transactional` 기본 적용
- 독립 트랜잭션 필요 시 `REQUIRES_NEW` 사용 (댓글 일괄 삭제, 리프레시 토큰 삭제)

## Main Entities
| Entity | 설명 | 특이사항 |
|--------|------|----------|
| User | 사용자 | UUID userIdentifier, soft deactivate (active 필드) |
| Post | 게시글 | soft delete, PostType(BENEFIT/QUESTION/ETC) |
| Comment | 댓글 | soft delete, 자기참조(대댓글), parentComment |
| Merchant | 가맹점 | Category와 연관 |
| Category | 카테고리 | 자기참조(계층 구조) |
| PostLike/PostBookmark/PostReport | 게시글 부가 | UK(post_id, user_id) |
| CommentLike | 댓글 좋아요 | UK(comment_id, user_id) |
| Favorite | 즐겨찾기 | UK(user_id, merchant_id) |
| RefreshTokenEntity | 리프레시 토큰 | 해시 저장, replay attack 방지 |

## Profiles
- **default (dev)**: localhost MySQL, ddl-auto: update, show-sql: true
- **prod**: 환경변수(DB_URL, DB_USERNAME, DB_PASSWORD), show-sql: false
- **test**: H2 인메모리, ddl-auto: create-drop
