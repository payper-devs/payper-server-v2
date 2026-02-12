package com.payper.server.global.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * springdoc-openapi가 Querydsl을 감지하여 QuerydslPredicateOperationCustomizer 빈을
 * 자동 생성하는 것을 방지한다.
 * Spring Boot 4 + springdoc 2.8.x 호환 문제로 해당 빈 생성이 실패하므로 제외 처리.
 */
@Configuration
public class SpringDocQuerydslExcludeConfig {

    @Bean
    static BeanDefinitionRegistryPostProcessor removeQuerydslPredicateCustomizer() {
        return new BeanDefinitionRegistryPostProcessor() {
            @Override
            public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
                String beanName = "queryDslQuerydslPredicateOperationCustomizer";
                if (registry.containsBeanDefinition(beanName)) {
                    registry.removeBeanDefinition(beanName);
                }
            }

            @Override
            public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
                // no-op
            }
        };
    }
}
