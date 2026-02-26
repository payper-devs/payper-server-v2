package com.payper.server;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

/**
 * DTO 아키텍처 규칙 검증 테스트
 */
@AnalyzeClasses(packages = "com.payper.server")
class DtoArchitectureTest {

    private static final String DTO_PKG = "..dto..";

    /**
     * dto 패키지에 위치해야 한다.
     */
    @ArchTest
    static ArchRule dtoMustResideInDtoPackage =
            classes()
                    .that().haveSimpleNameEndingWith("Request")
                    .or().haveSimpleNameEndingWith("Response")
                    .and().resideOutsideOfPackage("com.payper.server.global..") // TODO: domain 이동 후 변경
                    .should().resideInAPackage(DTO_PKG);
}
