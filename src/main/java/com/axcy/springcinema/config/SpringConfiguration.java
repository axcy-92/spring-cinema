package com.axcy.springcinema.config;

import com.axcy.springcinema.config.core.CoreConfiguration;
import com.axcy.springcinema.config.web.WebConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Aleksei_Cherniavskii
 */
@Configuration
@Import({
        CoreConfiguration.class,
        WebConfiguration.class
})
public class SpringConfiguration {}