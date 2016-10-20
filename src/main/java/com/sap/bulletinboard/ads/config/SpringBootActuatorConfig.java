package com.sap.bulletinboard.ads.config;

import org.springframework.boot.actuate.autoconfigure.EndpointAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@EnableConfigurationProperties
@Configuration
@EnableAutoConfiguration
@Import(EndpointAutoConfiguration.class)
public class SpringBootActuatorConfig {

}
