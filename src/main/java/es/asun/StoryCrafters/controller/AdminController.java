package es.asun.StoryCrafters.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import es.asun.StoryCrafters.security.SecurityConfig;

@Controller
public class AdminController {
    @GetMapping(value = {"/admin"})
    public String admin() {
        return "views/admin";
    }

    @PostMapping(value = {"/newuser"})
    public String newuser(HttpServletRequest request,
                          @RequestParam(name = "user", defaultValue = "") String usuario,
                          @RequestParam(name = "pass", defaultValue = "") String password,
                          CsrfToken csrfToken
    ) {
        String csrfTokenValue = csrfToken.getToken();
        if (usuario.trim().equals("") || password.trim().equals("")) {
            return "redirect:/admin?errorcreated&_csrf="+csrfTokenValue;
        } else {
            @SuppressWarnings("deprecation")
            UserDetails user1 = User.withDefaultPasswordEncoder()
                    .username(usuario)
                    .password(password)
                    .build();


            try {
                request.logout();
                request.login(usuario, password);
            } catch (ServletException e) {
                throw new RuntimeException(e);
            }
            return "redirect:/index";
        }
    }
}
