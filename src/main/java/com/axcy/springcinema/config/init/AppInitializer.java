package com.axcy.springcinema.config.init;

import com.axcy.springcinema.config.SpringConfiguration;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletRegistration;

/**
 * @author Aleksei_Cherniavskii
 */
public class AppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[] { SpringConfiguration.class };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return null;
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] { "/" };
    }

    @Override
    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
        int maxUploadSizeInMb = 1048576; // 10mb
        MultipartConfigElement multipartConfigElement = new MultipartConfigElement(null,
                                                                                   maxUploadSizeInMb,
                                                                                   maxUploadSizeInMb * 2,
                                                                                   maxUploadSizeInMb / 2);
        registration.setMultipartConfig(multipartConfigElement);
    }
}