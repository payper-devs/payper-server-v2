package com.payper.server;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/** Repository 아키텍처 규칙 검증 테스트 */
@AnalyzeClasses(packages = "com.payper.server")
class RepositoryArchitectureTest {

    private static final String CONTROLLER_PKG = "..controller..";
    private static final String SERVICE_PKG = "..service..";
    private static final String REPOSITORY_PKG = "..repository..";

    /** Service 또는 Controller 계층을 참조하면 안 된다. */
    @ArchTest
    static ArchRule repositoryMustNotDependOnOtherLayers = noClasses()
            .that()
            .resideInAPackage(REPOSITORY_PKG)
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(SERVICE_PKG, CONTROLLER_PKG);

    /** repository 패키지에 있어야 한다. */
    @ArchTest
    static ArchRule repositoryMustResideInRepositoryPackage = classes()
            .that()
            .haveSimpleNameContaining("Repository")
            .and()
            .doNotHaveSimpleName("RepositoryArchitectureTest")
            .should()
            .resideInAPackage(REPOSITORY_PKG);
}
