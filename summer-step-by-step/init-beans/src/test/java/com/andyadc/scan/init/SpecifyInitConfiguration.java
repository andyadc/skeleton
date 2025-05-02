package com.andyadc.scan.init;

import com.andyadc.summer.annotation.Bean;
import com.andyadc.summer.annotation.Configuration;
import com.andyadc.summer.annotation.Value;

@Configuration
public class SpecifyInitConfiguration {

    @Bean(initMethod = "init")
    SpecifyInitBean createSpecifyInitBean(@Value("${app.title}") String appTitle, @Value("${app.version}") String appVersion) {
        return new SpecifyInitBean(appTitle, appVersion);
    }
}
