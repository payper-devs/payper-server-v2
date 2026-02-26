package com.payper.server;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.Entity;
import jakarta.persistence.MappedSuperclass;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/**
 * 전체 아키텍처 규칙 검증 테스트
 *
 * <h2>현재 알려진 위반 목록 (기존 코드 - 리팩토링 대상)</h2>
 * <ul>
 *   <li>[규칙 2]  AuthService는 Repository를 직접 주입하지 않고 UserService에 위임 → AuthService 위반</li>
 *   <li>[규칙 13] UserRepository, RefreshTokenRepository에 @Repository 미선언</li>
 *   <li>[규칙 16] AuthController가 auth.controller 패키지가 아닌 auth 루트에 위치</li>
 *   <li>[규칙 17] AuthService, UserService가 각 도메인 루트 패키지에 위치 (auth, user)</li>
 *   <li>[규칙 18] RefreshTokenRepository가 auth.jwt 패키지에 위치 (repository 패키지 아님)</li>
 *   <li>[규칙 19] AuthApi가 auth 루트 패키지에 위치 (controller 패키지 아님)</li>
 * </ul>
 *
 * <h2>제외 대상</h2>
 * <ul>
 *   <li>com.payper.server.domain.test: 개발/테스트용 임시 컨트롤러이므로 계층 규칙 적용 제외</li>
 * </ul>
 */
@AnalyzeClasses(
        packages = "com.payper.server",
        importOptions = {ImportOption.DoNotIncludeTests.class}
)
class GlobalArchitectureTest {

    private static final String CONTROLLER_PKG = "..controller..";
    private static final String SERVICE_PKG = "..service..";
    private static final String REPOSITORY_PKG = "..repository..";
    private static final String ENTITY_PKG = "..entity..";

    /** 개발/테스트용 임시 컨트롤러 패키지 - 계층 규칙 적용 제외 */
    private static final String TEST_UTIL_PKG = "com.payper.server.domain.test..";


    // ==========================================================================
    // 의존성 방향 규칙
    // ==========================================================================

    /**
     * 규칙 1 & 4: Controller는 Repository에 의존하면 안 된다.
     *
     * <p>Controller는 비즈니스 로직을 Service에 위임해야 하며, 데이터 접근 계층인
     * Repository에 직접 접근해서는 안 된다.
     */
    @ArchTest
    static ArchRule rule1_4_controllerMustNotDependOnRepository =
            noClasses()
                    .that().resideInAPackage(CONTROLLER_PKG)
                    .should().dependOnClassesThat().resideInAPackage(REPOSITORY_PKG)
                    .as("[규칙 1, 4] Controller는 Repository에 직접 의존하면 안 된다");

    /**
     * 규칙 2: Controller는 반드시 Service에 의존해야 한다.
     *
     * <p>도메인 로직이 없는 개발용 컨트롤러(domain.test)는 제외한다.
     */
    @ArchTest
    static ArchRule rule2_controllerMustDependOnService =
            classes()
                    .that().areAnnotatedWith(RestController.class)
                    .and().resideOutsideOfPackage(TEST_UTIL_PKG)
                    .should(new ArchCondition<JavaClass>("[규칙 2] Controller는 반드시 @Service 클래스에 의존해야 한다") {
                        @Override
                        public void check(JavaClass javaClass, ConditionEvents events) {
                            boolean hasServiceDependency = javaClass.getDirectDependenciesFromSelf()
                                    .stream()
                                    .anyMatch(dep -> dep.getTargetClass().isAnnotatedWith(Service.class));

                            if (!hasServiceDependency) {
                                events.add(SimpleConditionEvent.violated(
                                        javaClass,
                                        javaClass.getName() + " 는 @Service 클래스에 의존하지 않습니다."
                                ));
                            }
                        }
                    });

    /**
     * 규칙 3: Service는 반드시 Repository에 의존해야 한다.
     *
     * <p>Service 클래스는 데이터 접근을 위해 반드시 하나 이상의 Repository를 직접 주입받아야 한다.
     *
     * <p><b>알려진 위반:</b> AuthService는 UserService에 위임하는 구조라 직접 Repository 의존이 없다.
     */
    @ArchTest
    static ArchRule rule3_serviceMustDependOnRepository =
            classes()
                    .that().areAnnotatedWith(Service.class)
                    .should(new ArchCondition<JavaClass>("[규칙 3] Service는 반드시 Repository에 의존해야 한다") {
                        @Override
                        public void check(JavaClass javaClass, ConditionEvents events) {
                            boolean hasRepoDependency = javaClass.getDirectDependenciesFromSelf()
                                    .stream()
                                    .anyMatch(dep ->
                                            dep.getTargetClass().isAnnotatedWith(Repository.class)
                                            || dep.getTargetClass().getPackageName().contains(".repository")
                                    );

                            if (!hasRepoDependency) {
                                events.add(SimpleConditionEvent.violated(
                                        javaClass,
                                        javaClass.getName() + " 는 Repository 클래스에 의존하지 않습니다."
                                ));
                            }
                        }
                    });

    /**
     * 규칙 5: Service는 Controller를 참조하면 안 된다.
     *
     * <p>하위 계층(Service)이 상위 계층(Controller)을 알아서는 안 된다.
     */
    @ArchTest
    static ArchRule rule5_serviceMustNotDependOnController =
            noClasses()
                    .that().areAnnotatedWith(Service.class)
                    .should().dependOnClassesThat().areAnnotatedWith(RestController.class)
                    .as("[규칙 5] Service는 Controller를 참조하면 안 된다");

    /**
     * 규칙 6: Repository는 다른 계층(Service, Controller)을 참조하면 안 된다.
     *
     * <p>데이터 접근 계층인 Repository는 비즈니스/프레젠테이션 계층에 의존해서는 안 된다.
     */
    @ArchTest
    static ArchRule rule6_repositoryMustNotDependOnOtherLayers =
            noClasses()
                    .that().resideInAPackage(REPOSITORY_PKG)
                    .should().dependOnClassesThat().resideInAnyPackage(SERVICE_PKG, CONTROLLER_PKG)
                    .as("[규칙 6] Repository는 Service/Controller 계층을 참조하면 안 된다");


    // ==========================================================================
    // 인터페이스 구현 규칙
    // ==========================================================================

    /**
     * 규칙 7: Controller는 Swagger Api 인터페이스를 구현해야 한다.
     *
     * <p>API 문서화를 강제하여 모든 Controller가 명세(Api 인터페이스)를 가지도록 한다.
     * 개발용 테스트 컨트롤러(domain.test)는 제외한다.
     */
    @ArchTest
    static ArchRule rule7_controllerMustImplementApiInterface =
            classes()
                    .that().areAnnotatedWith(RestController.class)
                    .and().resideOutsideOfPackage(TEST_UTIL_PKG)
                    .should(new ArchCondition<JavaClass>("[규칙 7] Controller는 이름이 'Api'로 끝나는 인터페이스를 구현해야 한다") {
                        @Override
                        public void check(JavaClass javaClass, ConditionEvents events) {
                            boolean implementsApi = javaClass.getInterfaces()
                                    .stream()
                                    .anyMatch(iface -> iface.toErasure().getSimpleName().endsWith("Api"));

                            if (!implementsApi) {
                                events.add(SimpleConditionEvent.violated(
                                        javaClass,
                                        javaClass.getName() + " 는 'Api'로 끝나는 인터페이스를 구현하지 않습니다."
                                ));
                            }
                        }
                    });


    // ==========================================================================
    // 명명 규칙
    // ==========================================================================

    /**
     * 규칙 8: @RestController 클래스의 이름은 "Controller"로 끝나야 한다.
     */
    @ArchTest
    static ArchRule rule8_controllerClassNameMustEndWithController =
            classes()
                    .that().areAnnotatedWith(RestController.class)
                    .should().haveSimpleNameEndingWith("Controller")
                    .as("[규칙 8] @RestController 클래스의 이름은 'Controller'로 끝나야 한다");

    /**
     * 규칙 9: @Service 클래스의 이름은 "Service"로 끝나야 한다.
     */
    @ArchTest
    static ArchRule rule9_serviceClassNameMustEndWithService =
            classes()
                    .that().areAnnotatedWith(Service.class)
                    .should().haveSimpleNameEndingWith("Service")
                    .as("[규칙 9] @Service 클래스의 이름은 'Service'로 끝나야 한다");

    /**
     * 규칙 10: repository 패키지 내 클래스/인터페이스의 이름은 "Repository"로 끝나야 한다.
     */
    @ArchTest
    static ArchRule rule10_repositoryClassNameMustEndWithRepository =
            classes()
                    .that().resideInAPackage(REPOSITORY_PKG)
                    .should().haveSimpleNameEndingWith("Repository")
                    .as("[규칙 10] repository 패키지의 클래스는 이름이 'Repository'로 끝나야 한다");


    // ==========================================================================
    // 어노테이션 규칙
    // ==========================================================================

    /**
     * 규칙 11: controller 패키지의 Controller 클래스는 @RestController 어노테이션이 있어야 한다.
     */
    @ArchTest
    static ArchRule rule11_controllerMustBeAnnotatedWithRestController =
            classes()
                    .that().resideInAPackage(CONTROLLER_PKG)
                    .and().haveSimpleNameEndingWith("Controller")
                    .should().beAnnotatedWith(RestController.class)
                    .as("[규칙 11] controller 패키지의 Controller 클래스는 @RestController 어노테이션이 있어야 한다");

    /**
     * 규칙 12: service 패키지의 Service 클래스는 @Service 어노테이션이 있어야 한다.
     */
    @ArchTest
    static ArchRule rule12_serviceMustBeAnnotatedWithService =
            classes()
                    .that().resideInAPackage(SERVICE_PKG)
                    .and().haveSimpleNameEndingWith("Service")
                    .should().beAnnotatedWith(Service.class)
                    .as("[규칙 12] service 패키지의 Service 클래스는 @Service 어노테이션이 있어야 한다");

    /**
     * 규칙 13: repository 패키지의 인터페이스는 @Repository 어노테이션이 있어야 한다.
     *
     * <p>Spring Data JPA가 자동으로 빈을 등록하더라도, 명시적 선언을 통해 의도를 드러낸다.
     *
     * <p><b>알려진 위반:</b> UserRepository, RefreshTokenRepository에 @Repository 미선언.
     */
    @ArchTest
    static ArchRule rule13_repositoryMustBeAnnotatedWithRepository =
            classes()
                    .that().resideInAPackage(REPOSITORY_PKG)
                    .and().areInterfaces()
                    .should().beAnnotatedWith(Repository.class)
                    .as("[규칙 13] repository 패키지의 인터페이스는 @Repository 어노테이션이 있어야 한다");

    /**
     * 규칙 14: "Api"로 끝나는 인터페이스는 @Tag 어노테이션이 있어야 한다.
     *
     * <p>Swagger 문서에서 API 그룹을 명확히 하기 위해 @Tag 선언을 강제한다.
     */
    @ArchTest
    static ArchRule rule14_apiInterfaceMustHaveTagAnnotation =
            classes()
                    .that().haveSimpleNameEndingWith("Api")
                    .and().areInterfaces()
                    .should().beAnnotatedWith(Tag.class)
                    .as("[규칙 14] 'Api'로 끝나는 인터페이스는 @Tag 어노테이션이 있어야 한다");

    /**
     * 규칙 15: entity 패키지의 enum이 아닌 클래스는 @Entity 어노테이션이 있어야 한다.
     *
     * <p>@MappedSuperclass가 붙은 공통 기반 클래스(BaseTimeEntity 등)는 제외한다.
     */
    @ArchTest
    static ArchRule rule15_entityClassMustHaveEntityAnnotation =
            classes()
                    .that().resideInAPackage(ENTITY_PKG)
                    .and().areNotEnums()
                    .and().areNotAnnotatedWith(MappedSuperclass.class)
                    .should().beAnnotatedWith(Entity.class)
                    .as("[규칙 15] entity 패키지의 클래스(enum · @MappedSuperclass 제외)는 @Entity 어노테이션이 있어야 한다");


    // ==========================================================================
    // 패키지 배치 규칙
    // ==========================================================================

    /**
     * 규칙 16: @RestController 클래스는 controller 패키지에만 있어야 한다.
     *
     * <p>개발용 컨트롤러(domain.test)는 제외한다.
     *
     * <p><b>알려진 위반:</b> AuthController가 auth 루트 패키지에 위치.
     */
    @ArchTest
    static ArchRule rule16_controllerMustResideInControllerPackage =
            classes()
                    .that().areAnnotatedWith(RestController.class)
                    .and().resideOutsideOfPackage(TEST_UTIL_PKG)
                    .should().resideInAPackage(CONTROLLER_PKG)
                    .as("[규칙 16] @RestController 클래스는 controller 패키지에 위치해야 한다");

    /**
     * 규칙 17: @Service 클래스는 service 패키지에만 있어야 한다.
     *
     * <p><b>알려진 위반:</b> AuthService(auth 패키지), UserService(user 패키지)가 service 하위 패키지가 아닌 곳에 위치.
     */
    @ArchTest
    static ArchRule rule17_serviceMustResideInServicePackage =
            classes()
                    .that().areAnnotatedWith(Service.class)
                    .should().resideInAPackage(SERVICE_PKG)
                    .as("[규칙 17] @Service 클래스는 service 패키지에 위치해야 한다");

    /**
     * 규칙 18: @Repository 인터페이스는 repository 패키지에만 있어야 한다.
     *
     * <p><b>알려진 위반:</b> RefreshTokenRepository가 auth.jwt 패키지에 위치.
     */
    @ArchTest
    static ArchRule rule18_repositoryMustResideInRepositoryPackage =
            classes()
                    .that().areAnnotatedWith(Repository.class)
                    .should().resideInAPackage(REPOSITORY_PKG)
                    .as("[규칙 18] @Repository 인터페이스는 repository 패키지에 위치해야 한다");

    /**
     * 규칙 19: "Api"로 끝나는 인터페이스는 controller 패키지에 있어야 한다.
     *
     * <p><b>알려진 위반:</b> AuthApi가 auth 루트 패키지에 위치.
     */
    @ArchTest
    static ArchRule rule19_apiInterfaceMustResideInControllerPackage =
            classes()
                    .that().haveSimpleNameEndingWith("Api")
                    .and().areInterfaces()
                    .should().resideInAPackage(CONTROLLER_PKG)
                    .as("[규칙 19] 'Api'로 끝나는 인터페이스는 controller 패키지에 위치해야 한다");


    // ==========================================================================
    // 순환 참조 방지
    // ==========================================================================

    /**
     * 규칙 20: 도메인 패키지 간 순환 참조는 없어야 한다.
     *
     * <p>최상위 도메인 슬라이스(auth, post, comment, merchant, user 등)끼리
     * 순환 의존이 생기면 안 된다.
     */
    @ArchTest
    static ArchRule rule20_noCyclicDependencies =
            slices()
                    .matching("com.payper.server.(*)..")
                    .should().beFreeOfCycles()
                    .as("[규칙 20] 도메인 패키지 간 순환 참조가 없어야 한다");


    // ==========================================================================
    // 타입 규칙
    // ==========================================================================

    /**
     * 규칙 21: "Repository"로 끝나는 클래스/인터페이스는 반드시 인터페이스여야 한다.
     *
     * <p>구현체가 아닌 인터페이스 기반으로 Repository를 정의하여 Spring Data JPA와의
     * 일관성을 유지하고 테스트 확장성을 확보한다.
     */
    @ArchTest
    static ArchRule rule21_repositoryMustBeInterface =
            classes()
                    .that().haveSimpleNameEndingWith("Repository")
                    .should().beInterfaces()
                    .as("[규칙 21] Repository는 인터페이스여야 한다");


    // ==========================================================================
    // 계층 방향성 규칙
    // ==========================================================================

    /**
     * 규칙 22: 계층 간 의존 흐름은 Controller → Service → Repository 순방향이어야 한다.
     *
     * <p>역방향 의존(Repository → Service, Service → Controller 등)은 허용하지 않는다.
     * 각 계층 내부에서의 의존은 허용한다.
     */
    @ArchTest
    static ArchRule rule22_layeredArchitectureMustBeRespected =
            layeredArchitecture()
                    .consideringOnlyDependenciesInLayers()
                    .layer("Controller").definedBy(CONTROLLER_PKG)
                    .layer("Service").definedBy(SERVICE_PKG)
                    .layer("Repository").definedBy(REPOSITORY_PKG)
                    .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
                    .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller")
                    .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service")
                    .as("[규칙 22] 계층 간 의존 흐름은 Controller → Service → Repository 순방향이어야 한다");


    // ==========================================================================
    // 추가 권장 규칙
    // ==========================================================================

    /**
     * [권장 1] Service 클래스에는 클래스 레벨에 @Transactional 어노테이션이 있어야 한다.
     *
     * <p>메서드 단위의 누락을 방지하고, 기본 트랜잭션 전략을 명시적으로 선언하게 한다.
     * 읽기 전용 메서드는 @Transactional(readOnly = true)로 오버라이드하도록 유도한다.
     */
    @ArchTest
    static ArchRule recommended1_serviceMustHaveClassLevelTransactional =
            classes()
                    .that().areAnnotatedWith(Service.class)
                    .should().beAnnotatedWith(Transactional.class)
                    .as("[권장 1] Service 클래스는 클래스 레벨에 @Transactional이 있어야 한다");

    /**
     * [권장 2] Entity 클래스는 Service 또는 Controller 계층을 의존하면 안 된다.
     *
     * <p>도메인 모델의 순수성을 유지하기 위해 Entity가 상위 계층을 알아서는 안 된다.
     */
    @ArchTest
    static ArchRule recommended2_entityMustNotDependOnServiceOrController =
            noClasses()
                    .that().resideInAPackage(ENTITY_PKG)
                    .should().dependOnClassesThat().resideInAnyPackage(SERVICE_PKG, CONTROLLER_PKG)
                    .as("[권장 2] Entity 클래스는 Service/Controller 계층을 의존하면 안 된다");

    /**
     * [권장 3] DTO 클래스(Request/Response)는 entity 패키지에 있으면 안 된다.
     *
     * <p>데이터 전송 객체와 도메인 모델을 명확히 분리하여 계층 간 결합을 낮춘다.
     */
    @ArchTest
    static ArchRule recommended3_dtoMustNotResideInEntityPackage =
            noClasses()
                    .that().haveSimpleNameEndingWith("Request")
                    .or().haveSimpleNameEndingWith("Response")
                    .should().resideInAPackage(ENTITY_PKG)
                    .as("[권장 3] DTO(Request/Response) 클래스는 entity 패키지에 있으면 안 된다");

    /**
     * [권장 4] @Autowired 필드 주입은 사용하면 안 된다.
     *
     * <p>생성자 주입(@RequiredArgsConstructor 또는 명시적 생성자)만 허용하여
     * 불변성 확보 및 테스트 용이성을 높인다.
     */
    @ArchTest
    static ArchRule recommended4_noFieldInjectionWithAutowired =
            noFields()
                    .should().beAnnotatedWith(org.springframework.beans.factory.annotation.Autowired.class)
                    .as("[권장 4] @Autowired 필드 주입 대신 생성자 주입을 사용해야 한다");

    /**
     * [권장 5] global 패키지는 다른 도메인 패키지에 의존하면 안 된다.
     *
     * <p>공통 유틸리티/응답/예외 처리를 담당하는 global 패키지가
     * 특정 도메인에 결합되면 재사용성이 떨어진다.
     */
    @ArchTest
    static ArchRule recommended5_globalPackageMustNotDependOnDomainPackages =
            noClasses()
                    .that().resideInAPackage("com.payper.server.global..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "com.payper.server.post..",
                            "com.payper.server.comment..",
                            "com.payper.server.merchant..",
                            "com.payper.server.favorite..",
                            "com.payper.server.user..",
                            "com.payper.server.auth.."
                    )
                    .as("[권장 5] global 패키지는 특정 도메인 패키지에 의존하면 안 된다");
}
