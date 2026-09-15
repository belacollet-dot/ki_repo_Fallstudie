package de.likeherotozero.controller;

import de.likeherotozero.entity.Co2EmissionRecord;
import de.likeherotozero.entity.Country;
import de.likeherotozero.service.Co2EmissionService;
import de.likeherotozero.service.CountryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private final CountryService countryService;
    private final Co2EmissionService emissionService;
    private final ObjectMapper objectMapper;

    public HomeController(CountryService countryService,
                          Co2EmissionService emissionService,
                          ObjectMapper objectMapper) {
        this.countryService = countryService;
        this.emissionService = emissionService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/")
    public String home(Model model,
                       @RequestParam(required = false) List<Long> selectedCountries,
                       @RequestParam(required = false) String search,
                       @RequestParam(defaultValue = "name") String sortBy,
                       @RequestParam(defaultValue = "true") boolean ascending) {

        // Alle Länder laden (nur für die Checkbox-Liste, KEINE Emissionen pro Land)
        List<Country> countries = countryService.searchCountries(search);

        // Sortierung
        @SuppressWarnings("null")
        Comparator<Country> comparator = switch (sortBy) {
            case "isoCode" -> Comparator.comparing(
                    c -> c.getIsoCode() != null ? c.getIsoCode() : "",
                    String.CASE_INSENSITIVE_ORDER);
            case "continent" -> Comparator.comparing(
                    c -> c.getContinent() != null ? c.getContinent() : "",
                    String.CASE_INSENSITIVE_ORDER);
            default -> Comparator.comparing(Country::getName, String.CASE_INSENSITIVE_ORDER);
        };

        if (!ascending) {
            comparator = comparator.reversed();
        }

        countries = countries.stream().sorted(comparator).collect(Collectors.toList());

        // Auswahl
        List<Long> selected = selectedCountries != null ? selectedCountries : List.of();

        // Chart-Daten nur für ausgewählte Länder aufbauen
        Object chartData = null;
        if (!selected.isEmpty()) {
            chartData = buildChartData(selected);
        }

        model.addAttribute("countries", countries);
        model.addAttribute("selectedCountries", selected);
        model.addAttribute("selectedCountryCount", selected.size());
        model.addAttribute("chartData", chartData);
        model.addAttribute("searchQuery", search);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("ascending", ascending);

        return "home";
    }

    @SuppressWarnings("null")
    private Object buildChartData(List<Long> selectedIds) {
        // Alle Jahre sammeln
        Set<Integer> yearsSet = new TreeSet<>();
        Map<Long, Map<Integer, Double>> valuesByCountry = new LinkedHashMap<>();

        for (Long id : selectedIds) {
            List<Co2EmissionRecord> records = emissionService.getEmissionsByCountry(id);
            Map<Integer, Double> valueByYear = new HashMap<>();

            for (Co2EmissionRecord r : records) {
                yearsSet.add(r.getYear());
                // co2value zuerst, fallback auf emissionvalue
                double val = r.getCo2Value() != null ? r.getCo2Value()
                           : r.getEmissionValue() != null ? r.getEmissionValue()
                           : 0.0;
                valueByYear.put(r.getYear(), val);
            }

            valuesByCountry.put(id, valueByYear);
        }

        List<Integer> years = new ArrayList<>(yearsSet);

        List<Map<String, Object>> datasets = new ArrayList<>();
        for (Long id : selectedIds) {
            Country country = countryService.getCountryById(id);
            Map<Integer, Double> valueByYear = valuesByCountry.get(id);

            List<Object> data = years.stream()
                .map(y -> (Object) valueByYear.getOrDefault(y, null))
                .collect(Collectors.toList());

            Map<String, Object> ds = new LinkedHashMap<>();
            ds.put("label", country.getName());
            ds.put("data", data);
            datasets.add(ds);
        }

        Map<String, Object> chartData = new LinkedHashMap<>();
        chartData.put("years", years);
        chartData.put("datasets", datasets);

        try {
            return objectMapper.valueToTree(chartData);
        } catch (Exception e) {
            return null;
        }
    }
}
