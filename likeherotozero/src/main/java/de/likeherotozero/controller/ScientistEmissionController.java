package de.likeherotozero.controller;

import de.likeherotozero.form.CountryDashboardForm;
import de.likeherotozero.form.CreateEmissionForm;
import de.likeherotozero.service.Co2EmissionService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/scientist/emissions")
public class ScientistEmissionController {

    private final Co2EmissionService co2EmissionService;

    public ScientistEmissionController(Co2EmissionService co2EmissionService) {
        this.co2EmissionService = co2EmissionService;
    }

    @GetMapping("/new")
    public String showCreateForm(CreateEmissionForm createEmissionForm) {
        return "ScientistEmissionForm";
    }

    @PostMapping
    public String createEmission(@Valid CountryDashboardForm createEmissionForm,
                                 BindingResult bindingResult,
                                 Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return "ScientistEmissionForm";
        }

        co2EmissionService.createEmission(createEmissionForm, authentication.getName());
        return "redirect:/scientist/dashboard";
    }
}
