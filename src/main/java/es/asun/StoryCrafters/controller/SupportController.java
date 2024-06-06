package es.asun.StoryCrafters.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador para manejar las solicitudes relacionadas con el soporte al usuario.
 */
@Controller
@RequestMapping("/support")
public class SupportController {

    /**
     * Muestra la página de Preguntas Frecuentes (FAQ).
     *
     * @param model el modelo para la vista
     * @return el nombre de la vista que muestra las FAQs
     */
    @GetMapping(value = {"/faq"})
    public String viewFaq(Model model) {
        model.addAttribute("content", "views/support/faq");
        model.addAttribute("currentPage", "ayuda");
        return "index";
    }
}
