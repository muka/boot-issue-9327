/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.issue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

/**
 *
 * @author Luca Capra <lcapra@fbk.eu>
 */
@SpringBootApplication(
        scanBasePackages = {"com.example.issue"},
        exclude = {ArtemisAutoConfiguration.class}
)
@EnableConfigurationProperties
@Profile("default")
@EnableAutoConfiguration
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class Application  {
    
    static final String basepath = "/tmp/";
    static String appName = "issue";
    
    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = new SpringApplicationBuilder()
                .sources(Application.class)
                .web(false)
                .run(args);
        
        RaptorConfig config = ctx.getBean(RaptorConfig.class);
        
        System.out.println("com.example.issue.Application.main()" + config.getBroker().getArtemis());
        if(config.getBroker().getArtemis() == null)
            throw new RuntimeException("`artemis` should not be null");
        
    }

    static public String[] buildArgs(Class clazz, String[] args) {

        String[] parts = clazz.getCanonicalName().split("\\.");
        appName = parts[parts.length - 2];
        String name = "--spring.config.name=" + appName;

        String[] args2 = new String[args.length + 1];
        System.arraycopy(args, 0, args2, 0, args.length);
        args2[args2.length - 1] = name;

        return args2;
    }
    
}
