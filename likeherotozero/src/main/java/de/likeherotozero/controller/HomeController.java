package de.likeherotozero.controller;

import de.likeherotozero.entity.Co2EmissionRecord;
import de.likeherotozero.entity.Country;
import de.likeherotozero.service.Co2EmissionService;
import de.likeherotozero.service.CountryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
public class HomeController {

    private final CountryService countryService;
    private final Co2EmissionService co2EmissionService;

    public HomeController(CountryService countryService, Co2EmissionService co2EmissionService) {
        this.countryService = countryService;
        this.co2EmissionService = co2EmissionService;
    }

    @GetMapping("/")
    public String showHomePage(
            @RequestParam(name = "selectedCountries", required = false) List<Long> selectedCountries,
            Model model) {

        if (selectedCountries == null) {
            selectedCountries = Collections.emptyList();
        }

        List<Country> countries = new ArrayList<>(countryService.findAllCountries());
        List<Country> selectedCountryEntities = selectedCountries.isEmpty()
                ? Collections.emptyList()
                : countryService.findAllByIds(selectedCountries);

        Set<Long> selectedCountryIdSet = new HashSet<>(selectedCountries);

        countries.sort(
                Comparator
                        .comparing((Country country) -> !selectedCountryIdSet.contains(country.getId()))
                        .thenComparing(country -> country.getName(), String.CASE_INSENSITIVE_ORDER)
        );

        Set<Integer> yearSet = new TreeSet<>();
        Map<Long, Map<Integer, BigDecimal>> countryYearValues = new HashMap<>();

        for (Country country : selectedCountryEntities) {
            List<Co2EmissionRecord> records = co2EmissionService.findByCountry(country);
            Map<Integer, BigDecimal> valuesByYear = new HashMap<>();

            for (Co2EmissionRecord record : records) {
                yearSet.add(record.getReportingYear());
                valuesByYear.put(record.getReportingYear(), record.getEmissionValue());
            }

            countryYearValues.put(country.getId(), valuesByYear);
        }

        List<Integer> chartLabels = new ArrayList<>(yearSet);
        List<Map<String, Object>> chartDatasets = new ArrayList<>();

        String[] colors = {
                "#0f766e", "#2563eb", "#dc2626", "#7c3aed", "#ea580c",
                "#16a34a", "#db2777", "#0891b2", "#4f46e5", "#a16207"
        };

        int colorIndex = 0;

        for (Country country : selectedCountryEntities) {
            Map<Integer, BigDecimal> valuesByYear = countryYearValues.get(country.getId());
            List<BigDecimal> data = new ArrayList<>();

            for (Integer year : chartLabels) {
                data.add(valuesByYear.getOrDefault(year, null));
            }

            Map<String, Object> dataset = new HashMap<>();
            dataset.put("label", country.getName());
            dataset.put("data", data);
            dataset.put("borderColor", colors[colorIndex % colors.length]);
            dataset.put("backgroundColor", colors[colorIndex % colors.length]);
            dataset.put("fill", false);
            dataset.put("tension", 0.25);
            dataset.put("spanGaps", false);

            chartDatasets.add(dataset);
            colorIndex++;
        }

        model.addAttribute("countries", countries);
        model.addAttribute("selectedCountries", selectedCountries);
        model.addAttribute("selectedCountryCount", selectedCountries.size());
        model.addAttribute("chartLabels", chartLabels);
        model.addAttribute("chartDatasets", chartDatasets);

        return "home";
    }
}
