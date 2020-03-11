package com.retail.bo.components.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class Protocol implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Protocol.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //LOGGER.info("Filter: Protocol -> Initialized ");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        //LOGGER.info("Filter: Protocol -> Invoked ");
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        //LOGGER.info("Filter: Protocol -> Destroyed ");
    }
}
