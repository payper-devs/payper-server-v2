package com.payper.server;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import io.swagger.v3.oas.annotations.tags.Tag;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

/**
 * Swagger Api 인터페이스 아키텍처 규칙 검증 테스트
 */
@AnalyzeClasses(packages = "com.payper.server")
class SwaggerArchitectureTest {

    private static final String CONTROLLER_PKG = "..controller..";

    /**
     * Swagger API 문서는 인터페이스여야 한다.
     */
    @ArchTest
    static ArchRule apiMustBeInterface =
            classes()
                    .that().haveSimpleNameEndingWith("Api")
                    .should().beInterfaces();

    /**
     * @Tag 어노테이션이 선언되어야 한다.
     */
    @ArchTest
    static ArchRule apiInterfaceMustHaveTagAnnotation =
            classes()
                    .that().haveSimpleNameEndingWith("Api")
                    .should().beAnnotatedWith(Tag.class);

    /**
     * controller 패키지에 있어야 한다.
     */
    @ArchTest
    static ArchRule apiInterfaceMustResideInControllerPackage =
            classes()
                    .that().haveSimpleNameEndingWith("Api")
                    .should().resideInAPackage(CONTROLLER_PKG);
}
