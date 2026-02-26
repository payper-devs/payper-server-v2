package com.payper.server;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.Architectures;
import org.springframework.beans.factory.annotation.Autowired;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

/**
 * 공통 아키텍처 규칙 검증 테스트
 *
 * <p>특정 계층에 국한되지 않는 전역 규칙을 검증한다.
 * <ul>
 *   <li>계층 간 순방향 의존 흐름</li>
 *   <li>global 패키지 독립성</li>
 *   <li>DI 방식 (생성자 주입, final 필드)</li>
 * </ul>
 */
@AnalyzeClasses(
        packages = "com.payper.server",
        importOptions = {ImportOption.DoNotIncludeTests.class} // TODO: test 패키지의 테스트 클래스들은 제외
)
class GlobalArchitectureTest {

    private static final String CONTROLLER_PKG = "..controller..";
    private static final String SERVICE_PKG = "..service..";
    private static final String REPOSITORY_PKG = "..repository..";

    private static final String CONTROLLER_LAYER = "Controller";
    private static final String SERVICE_LAYER = "Service";
    private static final String REPOSITORY_LAYER = "Repository";

    /**
     * 계층 간 의존 흐름은 Controller → Service → Repository 순방향이어야 한다.
     */
    @ArchTest
    static Architectures.LayeredArchitecture layeredArchitectureMustBeRespected =
            layeredArchitecture()
                    .consideringOnlyDependenciesInLayers()
                    .layer(CONTROLLER_LAYER).definedBy(CONTROLLER_PKG)
                    .layer(SERVICE_LAYER).definedBy(SERVICE_PKG)
                    .layer(REPOSITORY_LAYER).definedBy(REPOSITORY_PKG)
                    .whereLayer(CONTROLLER_LAYER).mayNotBeAccessedByAnyLayer()
                    .whereLayer(SERVICE_LAYER).mayOnlyBeAccessedByLayers(CONTROLLER_LAYER)
                    .whereLayer(REPOSITORY_LAYER).mayOnlyBeAccessedByLayers(SERVICE_LAYER);

    /**
     * global 패키지는 다른 도메인 패키지에 의존하면 안 된다.
     */
    @ArchTest
    static ArchRule globalPackageMustNotDependOnDomainPackages =
            noClasses()
                    .that().resideInAPackage("com.payper.server.global..")
                    // .should().dependOnClassesThat().resideInAPackage(".domain..") TODO: domain 디렉토리 안으로 이동 후 적용
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "com.payper.server.auth..",
                            "com.payper.server.user..",
                            "com.payper.server.post..",
                            "com.payper.server.comment..",
                            "com.payper.server.merchant..",
                            "com.payper.server.favorite.."
                    );

    /**
     * @Autowired 필드 주입은 사용하면 안 된다.
     * <br>
     * 생성자 주입 또는 명시적 생성자만 허용한다.
     */
    @ArchTest
    static ArchRule noFieldInjectionWithAutowired =
            noFields()
                    .should().beAnnotatedWith(Autowired.class);

    /**
     * Service, Controller의 non-static 필드는 final이어야 한다.
     */
    @ArchTest
    static final ArchRule componentsMustHaveOnlyFinalFields =
            fields()
                    .that()
                    .areDeclaredInClassesThat()
                    .resideInAnyPackage(SERVICE_PKG, CONTROLLER_PKG)
                    .and().areNotStatic()
                    .should().beFinal();
}
