package de.likeherotozero.controller;

import de.likeherotozero.entity.Co2EmissionRecord;
import de.likeherotozero.entity.Country;
import de.likeherotozero.entity.ScientistUser;
import de.likeherotozero.form.CreateEmissionForm;
import de.likeherotozero.service.Co2EmissionService;
import de.likeherotozero.service.CountryService;
import de.likeherotozero.service.ScientistUserService;
import jakarta.validation.Valid;

import org.springframework.lang.NonNull;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/scientist")
public class ScientistDashboardController {

    private final ScientistUserService userService;
    private final CountryService countryService;
    private final Co2EmissionService emissionService;

    public ScientistDashboardController(ScientistUserService userService,
                                        CountryService countryService,
                                        Co2EmissionService emissionService) {
        this.userService = userService;
        this.countryService = countryService;
        this.emissionService = emissionService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model,
                            @AuthenticationPrincipal User currentUser,
                            @RequestParam(required = false) String search,
                            @RequestParam(defaultValue = "name") String sortBy,
                            @RequestParam(defaultValue = "true") boolean ascending) {
        ScientistUser scientist = userService.getUserByEmail(currentUser.getUsername());

        List<Country> countries = countryService.searchCountries(search);

        // Länder mit Statistiken
        List<CountryDashboardRow> rows = countries.stream()
            .map(country -> {
                List<Co2EmissionRecord> records = emissionService.getEmissionsByCountry(country.getId());
                ScientistUser editor = country.getEditedBy();
                return new CountryDashboardRow(
                    country.getId(),
                    country.getName(),
                    country.getContinent(),
                    emissionService.calculateAverage(records),
                    emissionService.calculateMin(records),
                    emissionService.calculateMax(records),
                    records.size(),
                    editor != null ? editor.getName() : "Nicht zugewiesen"
                );
            })
            .sorted(getCountryComparator(sortBy, ascending))
            .collect(Collectors.toList());

        model.addAttribute("scientist", scientist);
        model.addAttribute("countries", rows);
        model.addAttribute("searchQuery", search);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("ascending", ascending);

        return "ScientistDashboard";
    }

    @GetMapping("/my-emissions")
    public String myEmissions(Model model, @AuthenticationPrincipal User currentUser) {
        ScientistUser scientist = userService.getUserByEmail(currentUser.getUsername());
        List<Co2EmissionRecord> emissions = emissionService.getEmissionsByScientist(scientist.getId());

        model.addAttribute("scientist", scientist);
        model.addAttribute("emissions", emissions);

        return "ScientistMyEmissions";
    }

    @GetMapping("/emission/new")
    public String newEmissionForm(Model model) {
        model.addAttribute("emissionForm", new CreateEmissionForm());
        model.addAttribute("countries", countryService.getAllCountries());
        return "ScientistEmissionForm";
    }

    @PostMapping("/emission/new")
    public String createEmission(@Valid @ModelAttribute("emissionForm") CreateEmissionForm form,
                                 BindingResult result,
                                 @AuthenticationPrincipal User currentUser) {
        if (result.hasErrors()) {
            model.addAttribute("countries", countryService.getAllCountries());
            return "ScientistEmissionForm";
        }

        ScientistUser scientist = userService.getUserByEmail(currentUser.getUsername());
        Country country = countryService.getCountryById(form.getCountryId());

        Co2EmissionRecord record = new Co2EmissionRecord();
        record.setCountry(country);
        record.setYear(form.getYear());
        record.setCo2Value(form.getCo2Value());
        record.setScientist(scientist);

        emissionService.saveEmission(record);
        return "redirect:/scientist/my-emissions";
    }

    @GetMapping("/emission/edit/{id}")
    public String editEmissionForm(@PathVariable Long id, Model model,
                                   @AuthenticationPrincipal User currentUser) {
        Co2EmissionRecord record = emissionService.getEmissionById(id);
        ScientistUser scientist = userService.getUserByEmail(currentUser.getUsername());

        // Nur eigene Rekorde bearbeiten
        if (!record.getScientist().getId().equals(scientist.getId())) {
            return "redirect:/scientist/my-emissions?error=permission";
        }

        CreateEmissionForm form = new CreateEmissionForm();
        form.setCountryId(record.getCountry().getId());
        form.setYear(record.getYear());
        form.setCo2Value(record.getCo2Value());

        model.addAttribute("emissionForm", form);
        model.addAttribute("countries", countryService.getAllCountries());
        model.addAttribute("recordId", id);

        return "ScientistEmissionForm";
    }

    @PostMapping("/emission/edit/{id}")
    public String updateEmission(@PathVariable Long id,
                                 @Valid @ModelAttribute("emissionForm") CreateEmissionForm form,
                                 BindingResult result,
                                 @AuthenticationPrincipal User currentUser) {
        if (result.hasErrors()) {
            model.addAttribute("countries", countryService.getAllCountries());
            return "ScientistEmissionForm";
        }

        Co2EmissionRecord record = emissionService.getEmissionById(id);
        ScientistUser scientist = userService.getUserByEmail(currentUser.getUsername());

        // Nur eigene Rekorde bearbeiten
        if (!record.getScientist().getId().equals(scientist.getId())) {
            return "redirect:/scientist/my-emissions?error=permission";
        }

        Country country = countryService.getCountryById(form.getCountryId());
        record.setCountry(country);
        record.setYear(form.getYear());
        record.setCo2Value(form.getCo2Value());

        emissionService.saveEmission(record);
        return "redirect:/scientist/my-emissions";
    }

    @PostMapping("/emission/delete/{id}")
    public String deleteEmission(@PathVariable @NonNull Long id,
                                 @AuthenticationPrincipal User currentUser) {
        Co2EmissionRecord record = emissionService.getEmissionById(id);
        ScientistUser scientist = userService.getUserByEmail(currentUser.getUsername());

        // Nur eigene Rekorde löschen
        if (!record.getScientist().getId().equals(scientist.getId())) {
            return "redirect:/scientist/my-emissions?error=permission";
        }

        emissionService.deleteEmission(id);
        return "redirect:/scientist/my-emissions";
    }

    private Comparator<CountryDashboardRow> getCountryComparator(String sortBy, boolean ascending) {
        @SuppressWarnings("null")
        Comparator<CountryDashboardRow> comparator = switch (sortBy) {
            case "avgEmission" -> Comparator.comparingDouble(CountryDashboardRow::avgEmission);
            case "recordCount" -> Comparator.comparingInt(CountryDashboardRow::recordCount);
            default -> Comparator.comparing(CountryDashboardRow::name, String.CASE_INSENSITIVE_ORDER);
        };

        return ascending ? comparator : comparator.reversed();
    }

    // Record-Klasse für Dashboard
    public record CountryDashboardRow(
        Long id,
        String name,
        String continent,
        double avgEmission,
        double minEmission,
        double maxEmission,
        int recordCount,
        String editorName
    ) {}
}
