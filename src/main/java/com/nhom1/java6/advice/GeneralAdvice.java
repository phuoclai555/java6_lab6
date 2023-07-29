package com.nhom1.java6.advice;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.context.annotation.RequestScope;

@ControllerAdvice
public class GeneralAdvice {
    @ModelAttribute
    public void getRequest(Model model, HttpServletRequest req) {
        model.addAttribute("request",req);
    }
}
