package com.andyadc.scan.init;

public class SpecifyInitBean {

    public String appName;

    String appTitle;

    String appVersion;

    SpecifyInitBean(String appTitle, String appVersion) {
        this.appTitle = appTitle;
        this.appVersion = appVersion;
    }

    void init() {
        this.appName = this.appTitle + " / " + this.appVersion;
    }
}
