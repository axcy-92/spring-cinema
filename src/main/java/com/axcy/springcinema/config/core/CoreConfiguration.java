package com.axcy.springcinema.config.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@Import({AspectConfiguration.class, DataConfiguration.class, AuditoriumConfiguration.class})
@PropertySource("classpath:auditorium1.properties")
@PropertySource("classpath:auditorium2.properties")
@PropertySource("classpath:auditorium3.properties")
@ComponentScan("com.axcy.springcinema.service")
public class CoreConfiguration {
    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer(){
        return new PropertySourcesPlaceholderConfigurer();
    }
}