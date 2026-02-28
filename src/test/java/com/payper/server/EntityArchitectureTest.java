package com.payper.server;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import jakarta.persistence.Entity;
import jakarta.persistence.MappedSuperclass;

/** Entity 아키텍처 규칙 검증 테스트 */
@AnalyzeClasses(
        packages = "com.payper.server",
        importOptions = {ImportOption.DoNotIncludeTests.class} // test 패키지의 테스트 클래스들은 제외
        )
class EntityArchitectureTest {

    private static final String CONTROLLER_PKG = "..controller..";
    private static final String SERVICE_PKG = "..service..";
    private static final String ENTITY_PKG = "..entity..";

    /** @Entity 어노테이션이 선언되어야 한다. */
    @ArchTest
    static ArchRule entityClassMustHaveEntityAnnotation = classes()
            .that()
            .resideInAPackage(ENTITY_PKG)
            .and()
            .areNotEnums()
            .and()
            .areNotAnnotatedWith(MappedSuperclass.class)
            .and()
            .areTopLevelClasses()
            .should()
            .beAnnotatedWith(Entity.class);

    /** Service 또는 Controller 계층을 의존하면 안 된다. */
    @ArchTest
    static ArchRule entityMustNotDependOnServiceOrController = noClasses()
            .that()
            .resideInAPackage(ENTITY_PKG)
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(SERVICE_PKG, CONTROLLER_PKG);
}
