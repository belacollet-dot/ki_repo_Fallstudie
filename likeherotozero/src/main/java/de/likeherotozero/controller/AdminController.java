package de.likeherotozero.controller;

import de.likeherotozero.entity.Co2EmissionRecord;
import de.likeherotozero.entity.Country;
import de.likeherotozero.entity.ScientistUser;
import de.likeherotozero.entity.UserRole;
import de.likeherotozero.form.CountryForm;
import de.likeherotozero.service.Co2EmissionService;
import de.likeherotozero.service.CountryService;
import de.likeherotozero.service.ScientistUserService;
import jakarta.validation.Valid;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ScientistUserService userService;
    private final CountryService countryService;
    private final Co2EmissionService emissionService;

    public AdminController(ScientistUserService userService,
                           CountryService countryService,
                           Co2EmissionService emissionService) {
        this.userService = userService;
        this.countryService = countryService;
        this.emissionService = emissionService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<ScientistUser> users = userService.getAllUsers();
        List<Country> countries = countryService.getAllCountries();
        List<Co2EmissionRecord> emissions = emissionService.getAllEmissions();

        model.addAttribute("users", users);
        model.addAttribute("countries", countries);
        model.addAttribute("emissions", emissions);
        model.addAttribute("userCount", users.size());
        model.addAttribute("countryCount", countries.size());
        model.addAttribute("emissionCount", emissions.size());

        return "AdminDashboard";
    }

    // ===== Wissenschaftler verwalten =====

    @GetMapping("/scientist/new")
    public String newScientistForm(Model model) {
        model.addAttribute("scientist", new ScientistUser());
        model.addAttribute("roles", UserRole.values());
        return "AdminScientistForm";
    }

    @PostMapping("/scientist/new")
    public String createScientist(@ModelAttribute("scientist") ScientistUser user,
                                  @RequestParam String password,
                                  Model model) {
        if (userService.emailExists(user.getEmail())) {
            model.addAttribute("error", "E-Mail existiert bereits");
            model.addAttribute("roles", UserRole.values());
            return "AdminScientistForm";
        }

        userService.createUser(user.getName(), user.getEmail(), password, user.getRole());
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/scientist/edit/{id}")
    public String editScientistForm(@PathVariable @NonNull Long id, Model model) {
        ScientistUser user = userService.getUserById(id);
        model.addAttribute("scientist", user);
        model.addAttribute("roles", UserRole.values());
        return "AdminScientistForm";
    }

    @PostMapping("/scientist/edit/{id}")
    public String updateScientist(@PathVariable @NonNull Long id,
                                  @ModelAttribute("scientist") ScientistUser user,
                                  @RequestParam(required = false) String password) {
        userService.updateUser(id, user.getName(), user.getEmail(), user.getRole());

        if (password != null && !password.trim().isEmpty()) {
            userService.updateUserPassword(id, password);
        }

        return "redirect:/admin/dashboard";
    }

    @PostMapping("/scientist/delete/{id}")
    public String deleteScientist(@PathVariable @NonNull Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/dashboard";
    }

    // ===== Länder verwalten =====

    @GetMapping("/countries")
    public String listCountries(Model model) {
        model.addAttribute("countries", countryService.getAllCountries());
        return "AdminCountries";
    }

    @GetMapping("/country/new")
    public String newCountryForm(Model model) {
        model.addAttribute("countryForm", new CountryForm());
        return "AdminCountryForm";
    }

    @PostMapping("/country/new")
    public String createCountry(@Valid @ModelAttribute("countryForm") CountryForm form,
                                BindingResult result) {
        if (result.hasErrors()) {
            return "AdminCountryForm";
        }

        Country country = new Country();
        country.setName(form.getName());
        country.setCode(form.getCode());
        country.setContinent(form.getContinent());

        countryService.saveCountry(country);
        return "redirect:/admin/countries";
    }

    @GetMapping("/country/edit/{id}")
    public String editCountryForm(@PathVariable @NonNull Long id, Model model) {
        Country country = countryService.getCountryById(id);
        CountryForm form = new CountryForm();
        form.setId(country.getId());
        form.setName(country.getName());
        form.setCode(country.getCode());
        form.setContinent(country.getContinent());

        model.addAttribute("countryForm", form);
        return "AdminCountryForm";
    }

    @PostMapping("/country/edit/{id}")
    public String updateCountry(@PathVariable @NonNull Long id,
                                @Valid @ModelAttribute("countryForm") CountryForm form,
                                BindingResult result) {
        if (result.hasErrors()) {
            return "AdminCountryForm";
        }

        Country country = countryService.getCountryById(id);
        country.setName(form.getName());
        country.setCode(form.getCode());
        country.setContinent(form.getContinent());

        countryService.saveCountry(country);
        return "redirect:/admin/countries";
    }

    @PostMapping("/country/delete/{id}")
    public String deleteCountry(@PathVariable @NonNull Long id) {
        countryService.deleteCountry(id);
        return "redirect:/admin/countries";
    }

    // ===== Emissionsrekorde verwalten =====

    @GetMapping("/emissions")
    public String listEmissions(Model model,
                                @RequestParam(required = false) Long countryId,
                                @RequestParam(required = false) Integer year) {
        List<Co2EmissionRecord> emissions = emissionService.filterEmissions(countryId, year, null, null);
        model.addAttribute("emissions", emissions);
        model.addAttribute("countries", countryService.getAllCountries());
        model.addAttribute("selectedCountryId", countryId);
        model.addAttribute("selectedYear", year);

        return "AdminEmissions";
    }

    @PostMapping("/emission/delete/{id}")
    public String deleteEmission(@PathVariable @NonNull Long id) {
        emissionService.deleteEmission(id);
        return "redirect:/admin/emissions";
    }
}
