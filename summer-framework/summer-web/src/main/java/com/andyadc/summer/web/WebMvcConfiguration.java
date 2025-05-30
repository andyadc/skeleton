package com.andyadc.summer.web;

import com.andyadc.summer.annotation.Configuration;
import jakarta.servlet.ServletContext;

@Configuration
public class WebMvcConfiguration {

    private static ServletContext servletContext = null;

    /**
     * Set by web listener.
     */
    static void setServletContext(ServletContext ctx) {
        servletContext = ctx;
    }

}
