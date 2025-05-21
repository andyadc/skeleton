package com.andyadc.summer.aop.after;

import com.andyadc.summer.annotation.Bean;
import com.andyadc.summer.annotation.ComponentScan;
import com.andyadc.summer.annotation.Configuration;
import com.andyadc.summer.aop.AroundProxyBeanPostProcessor;

@Configuration
@ComponentScan
public class AfterApplication {

    @Bean
    AroundProxyBeanPostProcessor createAroundProxyBeanPostProcessor() {
        return new AroundProxyBeanPostProcessor();
    }

}
