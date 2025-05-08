package com.andyadc.imported;

import com.andyadc.summer.annotation.Bean;
import com.andyadc.summer.annotation.Configuration;

import java.time.ZonedDateTime;

@Configuration
public class ZonedDateConfiguration {

    @Bean
    ZonedDateTime startZonedDateTime() {
        return ZonedDateTime.now();
    }
}
