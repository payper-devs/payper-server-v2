package com.payper.server;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Service;

/** 서비스 아키텍처 규칙 검증 테스트 */
@AnalyzeClasses(
        packages = "com.payper.server",
        importOptions = {ImportOption.DoNotIncludeTests.class} // test 패키지의 테스트 클래스들은 제외
)
class ServiceArchitectureTest {

    private static final String CONTROLLER_PKG = "..controller..";
    private static final String SERVICE_PKG = "..service..";

    /** @Service 어노테이션이 선언되어야 한다. */
    @ArchTest
    static ArchRule serviceMustBeAnnotatedWithService = classes()
            .that()
            .resideInAPackage(SERVICE_PKG)
            .and()
            .areNotInterfaces()
            .and()
            .areTopLevelClasses()
            .should()
            .beAnnotatedWith(Service.class);

    /** Controller를 참조하면 안 된다. */
    @ArchTest
    static ArchRule serviceMustNotDependOnController = noClasses()
            .that()
            .resideInAPackage(SERVICE_PKG)
            .should()
            .dependOnClassesThat()
            .resideInAPackage(CONTROLLER_PKG);

    /** service 패키지에 있어야 한다. */
    @ArchTest
    static ArchRule serviceMustResideInServicePackage = classes()
            .that()
            .haveSimpleNameContaining("Service")
            .and()
            .resideOutsideOfPackage("..security..")
            .should()
            .resideInAPackage(SERVICE_PKG);
}
