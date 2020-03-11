package com.retail.bo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class Rest {

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}
