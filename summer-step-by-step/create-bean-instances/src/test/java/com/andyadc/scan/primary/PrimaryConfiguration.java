package com.andyadc.scan.primary;

import com.andyadc.summer.annotation.Bean;
import com.andyadc.summer.annotation.Configuration;
import com.andyadc.summer.annotation.Primary;

@Configuration
public class PrimaryConfiguration {

    @Primary
    @Bean
    DogBean samoyed() {
        return new DogBean("Samoyed");
    }

    @Bean
    DogBean labrador() {
        return new DogBean("Labrador");
    }
}
