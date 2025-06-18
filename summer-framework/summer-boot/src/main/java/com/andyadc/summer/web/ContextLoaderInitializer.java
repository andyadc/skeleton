package com.andyadc.summer.web;

import com.andyadc.summer.context.AnnotationConfigApplicationContext;
import com.andyadc.summer.context.ApplicationContext;
import com.andyadc.summer.io.PropertyResolver;
import com.andyadc.summer.web.utils.WebUtils;
import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class ContextLoaderInitializer implements ServletContainerInitializer {

    final Logger logger = LoggerFactory.getLogger(getClass());

    final Class<?> configClass;
    final PropertyResolver propertyResolver;

    public ContextLoaderInitializer(Class<?> configClass, PropertyResolver propertyResolver) {
        this.configClass = configClass;
        this.propertyResolver = propertyResolver;
    }

    @Override
    public void onStartup(Set<Class<?>> set, ServletContext ctx) throws ServletException {
        logger.info("Servlet container start. ServletContext = {}", ctx);

        String encoding = propertyResolver.getProperty("${summer.web.character-encoding:UTF-8}");
        ctx.setRequestCharacterEncoding(encoding);
        ctx.setResponseCharacterEncoding(encoding);

        WebMvcConfiguration.setServletContext(ctx);
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(this.configClass, this.propertyResolver);
        logger.info("Application context created: {}", applicationContext);

        // register filters:
        WebUtils.registerFilters(ctx);
        // register DispatcherServlet:
        WebUtils.registerDispatcherServlet(ctx, this.propertyResolver);
    }

}
