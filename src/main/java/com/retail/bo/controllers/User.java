package com.retail.bo.controllers;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class User {

    @GetMapping("login")
    public ModelAndView login() {
        if (SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser")) {
            return new ModelAndView("login");
        } else {
            return new ModelAndView("stock/open-stock");
        }
    }
}
