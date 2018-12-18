package com.sap.bulletinboard.ads;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.apache.cxf.jaxrs.servlet.CXFNonSpringJaxrsServlet;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import com.sap.bulletinboard.ads.config.WebAppContextConfig;
import com.sap.hcp.cf.logging.servlet.filter.RequestLoggingFilter;

public class AppInitializer implements WebApplicationInitializer {
    private static final int LOAD_ON_STARTUP = 1; // initialize when tomcat starts, not when first request comes in
    private static final String MAPPING_URL = "/*";
    public  static final String ODATA_END_POINT = "/odata.svc";

    private void registerOdataServlet(final ServletContext servletContext) {
        ServletRegistration.Dynamic dispatcher=servletContext.addServlet("ODataSpring", new CXFNonSpringJaxrsServlet());
        dispatcher.setLoadOnStartup(LOAD_ON_STARTUP);
        Map<String, String> initParameters = new HashMap<>();
        
        initParameters.put("javax.ws.rs.Application", "org.apache.olingo.odata2.core.rest.app.ODataApplication");
        initParameters.put("org.apache.olingo.odata2.service.factory",
                "com.sap.bulletinboard.ads.context.JPAServiceFactory");
        dispatcher.setInitParameters(initParameters);
        dispatcher.addMapping(AppInitializer.ODATA_END_POINT+"/*");
    }
    
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        WebApplicationContext applicationContext = getApplicationContext();

        // register oData servlet
        registerOdataServlet(servletContext);
        
        // register Spring Web servlet
        ServletRegistration.Dynamic dispatcher = servletContext.addServlet("DispatcherServlet",
                new DispatcherServlet(applicationContext));
        dispatcher.setLoadOnStartup(LOAD_ON_STARTUP);
        
        // map requests that you want the DispatcherServlet to handle, by using a URL mapping
        dispatcher.addMapping(MAPPING_URL);
        
        servletContext.addListener(new ContextLoaderListener(applicationContext));

        // register logging servlet filter which logs HTTP request processing details
        servletContext.addFilter("RequestLoggingFilter", RequestLoggingFilter.class).addMappingForUrlPatterns(null,
                false, "/*");

        // register filter with name "springSecurityFilterChain"
        servletContext
                .addFilter(AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME,
                        new DelegatingFilterProxy(AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME))
                .addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "/*");
    }

    /**
     * Ensure that all required @Configuration, @Controller and @Component classes are registered to the Spring
     * application context.
     */
    private AnnotationConfigWebApplicationContext getApplicationContext() {
        AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
        applicationContext.register(WebAppContextConfig.class);
        applicationContext.getEnvironment().setActiveProfiles("cloud");
        return applicationContext;
    }
}
