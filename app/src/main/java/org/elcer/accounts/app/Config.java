package org.elcer.accounts.app;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class Config {

    @Bean
    @Scope(ConfigurableListableBeanFactory.SCOPE_PROTOTYPE)
    public Logger getLogger(InjectionPoint ip) {
        return LoggerFactory.getLogger(ip.getDeclaredType());
    }
}
