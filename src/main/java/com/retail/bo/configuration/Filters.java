package com.retail.bo.configuration;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.retail.bo.components.filters.Protocol;

@Configuration
public class Filters {

    @Bean
    public FilterRegistrationBean<Protocol> filterRegistrationBean() {
        FilterRegistrationBean<Protocol> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new Protocol());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }
}
