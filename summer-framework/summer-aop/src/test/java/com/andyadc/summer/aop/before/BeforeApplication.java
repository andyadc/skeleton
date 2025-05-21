package com.andyadc.summer.aop.before;

import com.andyadc.summer.annotation.Bean;
import com.andyadc.summer.annotation.ComponentScan;
import com.andyadc.summer.annotation.Configuration;
import com.andyadc.summer.aop.AroundProxyBeanPostProcessor;

@Configuration
@ComponentScan
public class BeforeApplication {

    @Bean
    AroundProxyBeanPostProcessor createAroundProxyBeanPostProcessor() {
        return new AroundProxyBeanPostProcessor();
    }

}
