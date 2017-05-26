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
    static final String appName = "issue";
    
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


    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();

        propertySourcesPlaceholderConfigurer.setIgnoreResourceNotFound(false);
        
        List<Resource> resources = new ArrayList<String>(Arrays.asList("raptor.yml", appName + ".yml"))
                .stream()
                .filter(f -> new File(basepath + f).exists())
                .map(f -> new FileSystemResource(basepath + f))
                .collect(Collectors.toList());

        if (resources.isEmpty()) {
            throw new RuntimeException("Cannot find a loadable property file in: " + basepath);
        }

        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(resources.toArray(new Resource[]{}));

        propertySourcesPlaceholderConfigurer.setProperties(yaml.getObject());
        return propertySourcesPlaceholderConfigurer;
    }
    
}
