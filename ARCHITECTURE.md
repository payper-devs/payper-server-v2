# Payper Server Architecture

이 문서는 코드베이스에 적용된 아키텍처 규칙을 정리합니다.
규칙은 ArchUnit 테스트로 자동 검증됩니다 (`./gradlew test`).

---

## 패키지 구조 (최종 버전)

```
com.payper.server
├── {domain}/
│   ├── controller/     ← REST 엔드포인트 + Swagger Api 인터페이스
│   ├── service/        ← 비즈니스 로직
│   ├── repository/     ← JPA Repository
│   ├── entity/         ← JPA 엔티티
│   └── dto/
│       ├── request/
│       └── response/
├── global/             ← 공통 (응답 래퍼, 예외, 에러 코드)
└── security/           ← Spring Security 설정 및 필터
```

---

## 계층 구조 및 의존 방향

```
Controller  →  Service  →  Repository
```

- 의존은 **단방향**. 역방향 참조 금지.

---

## 계층별 규칙

### Controller

| 규칙 | 내용 |
|------|------|
| 패키지 위치 | `controller` 패키지에만 위치해야 한다 |
| 어노테이션 | `@RestController`가 선언되어야 한다 |
| Service 의존 | 반드시 하나 이상의 `@Service` 클래스에 의존해야 한다 |
| Repository 격리 | `repository` 패키지에 직접 의존하면 안 된다 |
| Swagger 명세 | 이름이 `Api`로 끝나는 인터페이스를 구현해야 한다 |

> `domain.test` 패키지의 개발용 컨트롤러는 위 규칙에서 제외된다.

**명명 예시**
```
PostController, CommentController
```

---

### Service

| 규칙 | 내용 |
|------|------|
| 패키지 위치 | `service` 패키지에만 위치해야 한다 |
| 어노테이션 | `service` 패키지 내 구체 클래스는 `@Service`가 선언되어야 한다 |
| Controller 격리 | `controller` 패키지를 참조하면 안 된다 |

> interface가 있을 수 있으므로 이름 규칙은 강제하지 않는다. `PostService`, `PostServiceImpl` 등 자유롭게 사용할 수 있다.

**명명 예시**
```
PostService, CommentServiceImpl
```

---

### Repository

| 규칙 | 내용 |
|------|------|
| 패키지 위치 | `repository` 패키지에만 위치해야 한다 |
| 계층 격리 | `service`, `controller` 패키지를 참조하면 안 된다 |

> 커스텀이 있을 수 있으므로 이름 규칙은 강제하지 않는다. `PostRepository`, `PostRepositoryCustomImpl`, `PostRepositoryCustom` 등 자유롭게 사용할 수 있다.

**명명 예시**
```
PostRepository, PostRepositoryCustomImpl, PostRepositoryCustom
```

---

### Entity

| 규칙 | 내용 |
|------|------|
| 어노테이션 | `entity` 패키지의 enum·`BaseTimeEntity` 이외 클래스는 `@Entity`가 선언되어야 한다 |
| 계층 격리 | `service`, `controller` 패키지를 의존하면 안 된다 |

---

### DTO

| 규칙 | 내용 |
|------|------|
| 패키지 위치 | 이름이 `Request` 또는 `Response`로 끝나는 클래스는 `dto` 패키지에 위치해야 한다 |

> `global` 패키지의 공통 응답 래퍼(`ApiResponse` 등)는 제외된다.

---

### Swagger Api 인터페이스

| 규칙 | 내용 |
|------|------|
| 타입 | 인터페이스여야 한다 |
| 어노테이션 | `@Tag`가 선언되어야 한다 |
| 패키지 위치 | `controller` 패키지에 위치해야 한다 |

**명명 규칙**: `{Domain}Api` 형태로 이름이 `Api`로 끝나야 한다.

```java
// 올바른 예
@Tag(name = "게시글", description = "...")
public interface PostApi { ... }

// PostController는 PostApi를 구현해야 한다
@RestController
public class PostController implements PostApi { ... }
```

---

## 공통 규칙

### 의존성 주입 (DI)

| 규칙 | 내용 |
|------|------|
| 주입 방식 | `@Autowired` 필드 주입 금지. 생성자 주입만 허용 |
| 필드 선언 | `service`, `controller` 계층의 인스턴스 필드는 `final`이어야 한다 |
| Lombok | `service`, `controller` 계층의 구체 클래스는 `@RequiredArgsConstructor`를 사용해야 한다 |

```java
// 올바른 예
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
}

// 잘못된 예
@Autowired
private PostRepository postRepository;
```

### global 패키지 독립성

`global` 패키지는 도메인 패키지에 의존하면 안 된다.

---

## ArchUnit 테스트 파일 목록

| 파일 | 검증 대상 |
|------|----------|
| `GlobalArchitectureTest` | 계층 방향성, global 독립성, DI 방식 |
| `ControllerArchitectureTest` | Controller 계층 전반 |
| `ServiceArchitectureTest` | Service 계층 전반 |
| `RepositoryArchitectureTest` | Repository 계층 전반 |
| `EntityArchitectureTest` | Entity 어노테이션, 계층 격리 |
| `DtoArchitectureTest` | DTO 패키지 위치 |
| `SwaggerArchitectureTest` | Api 인터페이스 타입, 어노테이션, 위치 |
