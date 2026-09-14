package de.likeherotozero.controller;

import de.likeherotozero.entity.Co2EmissionRecord;
import de.likeherotozero.entity.Country;
import de.likeherotozero.service.Co2EmissionService;
import de.likeherotozero.service.CountryService;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private final CountryService countryService;
    private final Co2EmissionService emissionService;

    public HomeController(CountryService countryService, Co2EmissionService emissionService) {
        this.countryService = countryService;
        this.emissionService = emissionService;
    }

    @GetMapping("/")
    public String home(Model model,
                       @RequestParam(required = false) String search,
                       @RequestParam(defaultValue = "name") String sortBy,
                       @RequestParam(defaultValue = "true") boolean ascending) {
        
        List<Country> countries = countryService.searchCountries(search);

        // Länder mit Statistiken für das Dashboard
        List<CountryDashboardRow> rows = countries.stream()
            .map(country -> {
                List<Co2EmissionRecord> records = emissionService.getEmissionsByCountry(country.getId());
                return new CountryDashboardRow(
                    country.getId(),
                    country.getName(),
                    country.getIsoCode(),
                    country.getContinent(),
                    emissionService.calculateAverage(records),
                    emissionService.calculateMin(records),
                    emissionService.calculateMax(records),
                    records.size()
                );
            })
            .sorted(getCountryComparator(sortBy, ascending))
            .collect(Collectors.toList());

        model.addAttribute("countries", rows);
        model.addAttribute("searchQuery", search);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("ascending", ascending);

        return "home";
    }

    @GetMapping("/country/{id}")
    public String countryDetail(@PathVariable @NonNull Long id, Model model,
                                @RequestParam(required = false) Integer year,
                                @RequestParam(defaultValue = "year") String sortBy,
                                @RequestParam(defaultValue = "false") boolean ascending) {
        
        Country country = countryService.getCountryById(id);
        List<Co2EmissionRecord> records = emissionService.getEmissionsByCountry(id);

        // Nach Jahr filtern
        if (year != null) {
            records = records.stream()
                .filter(r -> r.getYear().equals(year))
                .collect(Collectors.toList());
        }

        // Verfügbare Jahre für Dropdown
        @SuppressWarnings("null")
        List<Integer> availableYears = emissionService.getEmissionsByCountry(id).stream()
            .map(Co2EmissionRecord::getYear)
            .distinct()
            .sorted()
            .collect(Collectors.toList());

        model.addAttribute("country", country);
        model.addAttribute("emissions", records);
        model.addAttribute("availableYears", availableYears);
        model.addAttribute("selectedYear", year);

        return "country-detail";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/legal")
    public String legal() {
        return "legal";
    }

    private Comparator<CountryDashboardRow> getCountryComparator(String sortBy, boolean ascending) {
        @SuppressWarnings("null")
        Comparator<CountryDashboardRow> comparator = switch (sortBy) {
            case "avgEmission" -> Comparator.comparingDouble(CountryDashboardRow::avgEmission);
            case "minEmission" -> Comparator.comparingDouble(CountryDashboardRow::minEmission);
            case "maxEmission" -> Comparator.comparingDouble(CountryDashboardRow::maxEmission);
            case "recordCount" -> Comparator.comparingInt(CountryDashboardRow::recordCount);
            case "isoCode" -> Comparator.comparing(r -> r.isoCode() != null ? r.isoCode() : "", String.CASE_INSENSITIVE_ORDER);
            default -> Comparator.comparing(CountryDashboardRow::name, String.CASE_INSENSITIVE_ORDER);
        };

        return ascending ? comparator : comparator.reversed();
    }

    // Record-Klasse für Dashboard-Anzeige
    public record CountryDashboardRow(
        Long id,
        String name,
        String isoCode,
        String continent,
        double avgEmission,
        double minEmission,
        double maxEmission,
        int recordCount
    ) {}
}
