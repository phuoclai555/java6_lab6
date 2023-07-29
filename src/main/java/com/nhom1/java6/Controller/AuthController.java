package com.nhom1.java6.Controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {
    @RequestMapping("/login/form")
    public String login() {
        return "home/login";
    }

    @RequestMapping("/login/success")
    public String su() {
        return "forward:/auth/login/form";
    }

    @RequestMapping("/logoff")
    public String s() {
        System.out.println(SecurityContextHolder.getContext().getAuthentication());
        return "forward:/auth/login/form";
    }

    @RequestMapping("/login/error")
    public String e(Model model){
        model.addAttribute("message","Sai password hoac username");
        return "forward:/auth/login/form";
    }

    @RequestMapping("access/denied")
    public String err(Model model){
        model.addAttribute("message","Khong du quyen try cap");
        return "home/login";
    }
}
