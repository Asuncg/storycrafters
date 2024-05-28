package es.asun.StoryCrafters.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/support")
public class SupportController {

    @GetMapping(value= {"/faq"})
    public String viewFaq(Model model) {
        model.addAttribute("content", "views/support/faq");
        return "index";
    }
}
