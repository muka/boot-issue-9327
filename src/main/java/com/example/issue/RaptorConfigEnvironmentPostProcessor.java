/*
 * Copyright 2017 FBK/CREATE-NET
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.issue;

import java.io.File;
import java.io.IOException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.FileSystemResource;

/**
 *
 * @author Luca Capra <luca.capra@gmail.com>
 */
public class RaptorConfigEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {
    
    final protected String basePath = "/tmp/";
    final protected String appName = "issue";
    private final YamlPropertySourceLoader loader;

    public RaptorConfigEnvironmentPostProcessor() {
        loader = new YamlPropertySourceLoader();
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment env, SpringApplication application) {
        //ensure that the bootstrap file is only loaded in the bootstrap context
        MutablePropertySources sources = env.getPropertySources();
//        if (sources.contains("broker")) {
            //Each document in the multi-document yaml must be loaded separately.
            //Start by loading the no-profile configs...
            loadProfile(appName, env, null);
            //Then loop through the active profiles and load them.
            for (String profile: env.getActiveProfiles()) {
                loadProfile(appName, env, profile);
            }
//        }
    }

    private void loadProfile(String prefix, ConfigurableEnvironment env, String profile) {
        try {
            PropertySource<?> propertySource = loader.load(prefix + (profile != null ? "-" + profile: ""), new FileSystemResource(new File(basePath + prefix + ".yml")), profile);
            //propertySource will be null if the profile isn't represented in the yml, so skip it if this is the case.
            if (propertySource != null) {
                //add PropertySource after the "applicationConfigurationProperties" source to allow the default yml to override these.
                env.getPropertySources().addAfter("applicationConfigurationProperties", propertySource);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getOrder() {
        //must go after ConfigFileApplicationListener
        return Ordered.HIGHEST_PRECEDENCE + 11;
    }

}