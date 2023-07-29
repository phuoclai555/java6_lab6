package com.nhom1.java6.Controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {
    @RequestMapping("/home/index")
    public String index(Model model, Authentication auth){
        if(auth != null){
            System.out.println(auth.getPrincipal());
        }
        model.addAttribute("message","This is home page");
        return "home/index";
    }

    @RequestMapping("/home/about")
    public String about(Model model){
        model.addAttribute("message","This is introduction page");
        return "home/about";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping("/home/admin")
    public String admin(){

        return "home/admin";
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @RequestMapping("/home/user")
    public String user(){

        return "home/user";
    }

    // dang nhap moi duoc vao
    @PreAuthorize("isAuthenticated()")
    @RequestMapping("/home/auth")
    public String auth(){

        return "home/auth";
    }

    @RequestMapping("/home/thymeleaf1")
    public String thy1(){

        return "home/thymeleaf1";
    }

    @RequestMapping("/home/thymeleaf2")
    public String thy2(){

        return "home/thymeleaf2";
    }
}
