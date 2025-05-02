package com.andyadc.scan.init;

import com.andyadc.summer.annotation.Component;
import com.andyadc.summer.annotation.Value;
import jakarta.annotation.PostConstruct;

@Component
public class AnnotationInitBean {

    public String appName;

    @Value("${app.title}")
    String appTitle;

    @Value("${app.version}")
    String appVersion;

    @PostConstruct
    void init() {
        this.appName = this.appTitle + " / " + this.appVersion;
    }
}
