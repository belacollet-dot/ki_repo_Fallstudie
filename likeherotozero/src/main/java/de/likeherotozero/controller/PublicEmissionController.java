package de.likeherotozero.controller;

import de.likeherotozero.entity.Co2EmissionRecord;
import de.likeherotozero.service.Co2EmissionService;
import de.likeherotozero.service.CountryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class PublicEmissionController {

    private final CountryService countryService;
    private final Co2EmissionService emissionService;

    public PublicEmissionController(CountryService countryService, Co2EmissionService emissionService) {
        this.countryService = countryService;
        this.emissionService = emissionService;
    }

    @GetMapping("/emissions")
    public String searchEmissions(Model model,
                                  @RequestParam(required = false) Long countryId,
                                  @RequestParam(required = false) Integer year,
                                  @RequestParam(required = false) Double min,
                                  @RequestParam(required = false) Double max,
                                  @RequestParam(defaultValue = "year") String sortBy,
                                  @RequestParam(defaultValue = "false") boolean ascending) {
        
        List<Co2EmissionRecord> emissions = emissionService.filterEmissions(countryId, year, min, max);
        emissions = sortEmissions(emissions, sortBy, ascending);

        model.addAttribute("emissions", emissions);
        model.addAttribute("countries", countryService.getAllCountries());
        model.addAttribute("selectedCountryId", countryId);
        model.addAttribute("selectedYear", year);
        model.addAttribute("selectedMin", min);
        model.addAttribute("selectedMax", max);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("ascending", ascending);

        return "EmissionsSearch";
    }

    private List<Co2EmissionRecord> sortEmissions(List<Co2EmissionRecord> emissions, String sortBy, boolean ascending) {
        @SuppressWarnings("null")
        Comparator<Co2EmissionRecord> comparator = switch (sortBy) {
            case "co2Value" -> Comparator.comparing(Co2EmissionRecord::getCo2Value);
            case "country" -> Comparator.comparing(e -> e.getCountry().getName(), String.CASE_INSENSITIVE_ORDER);
            default -> Comparator.comparing(Co2EmissionRecord::getYear);
        };

        if (!ascending) {
            comparator = comparator.reversed();
        }

        return emissions.stream().sorted(comparator).collect(Collectors.toList());
    }
}
