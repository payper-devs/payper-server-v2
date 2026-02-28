package com.payper.server;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.simpleNameEndingWith;
import static com.tngtech.archunit.lang.conditions.ArchConditions.implement;

/** 컨트롤러 아키텍처 규칙 검증 테스트 */
@AnalyzeClasses(
        packages = "com.payper.server",
        importOptions = {ImportOption.DoNotIncludeTests.class} // test 패키지의 테스트 클래스들은 제외
)
class ControllerArchitectureTest {

    private static final String CONTROLLER_PKG = "..controller..";
    private static final String REPOSITORY_PKG = "..repository..";

    /** 개발/테스트용 임시 컨트롤러 패키지 - 계층 규칙 적용 제외 */
    private static final String TEST_UTIL_PKG = "com.payper.server.domain.test..";

    /** @RestController 어노테이션이 선언되어야 한다. */
    @ArchTest
    static ArchRule controllerMustBeAnnotatedWithRestController =
            classes().that().haveSimpleNameEndingWith("Controller").should().beAnnotatedWith(RestController.class);

    /** Repository에 의존하면 안 된다. */
    @ArchTest
    static ArchRule controllerMustNotDependOnRepository = noClasses()
            .that()
            .resideInAPackage(CONTROLLER_PKG)
            .should()
            .dependOnClassesThat()
            .resideInAPackage(REPOSITORY_PKG);

    /** 반드시 Service에 의존해야 한다. */
    @ArchTest
    static ArchRule controllerMustDependOnService = classes()
            .that()
            .areAnnotatedWith(RestController.class)
            .and()
            .resideOutsideOfPackage(TEST_UTIL_PKG)
            .should()
            .dependOnClassesThat()
            .areAnnotatedWith(Service.class);

    /**
     * Controller는 Swagger Api 인터페이스를 구현해야 한다. <br>
     * API 문서화를 강제하여 모든 Controller가 명세서를 가지도록 한다.
     */
    @ArchTest
    static ArchRule controllerMustImplementApiInterface = classes()
            .that()
            .areAnnotatedWith(RestController.class)
            .and()
            .resideOutsideOfPackage(TEST_UTIL_PKG)
            .should(implement(simpleNameEndingWith("Api")));

    /** controller 패키지에만 있어야 한다. */
    @ArchTest
    static ArchRule controllerMustResideInControllerPackage = classes()
            .that()
            .haveSimpleNameEndingWith("Controller")
            .and()
            .resideOutsideOfPackage(TEST_UTIL_PKG)
            .should()
            .resideInAPackage(CONTROLLER_PKG);
}
