package de.likeherotozero.controller;

import de.likeherotozero.entity.Country;
import de.likeherotozero.form.CountryEditForm;
import de.likeherotozero.service.Co2EmissionService;
import de.likeherotozero.service.CountryService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/scientist/dashboard")
public class ScientistDashboardController {

    private final CountryService countryService;
    private final Co2EmissionService co2EmissionService;

    public ScientistDashboardController(CountryService countryService,
                                        Co2EmissionService co2EmissionService) {
        this.countryService = countryService;
        this.co2EmissionService = co2EmissionService;
    }

    @GetMapping
    public String showDashboard(Authentication authentication,
                                @RequestParam(value = "editId", required = false) Long editId,
                                @RequestParam(value = "success", required = false) String success,
                                Model model) {

        model.addAttribute("username", authentication.getName());
        model.addAttribute("countryRows", countryService.findDashboardRows());
        model.addAttribute("success", success);
        model.addAttribute("editMode", editId != null);

        if (editId != null) {
            Country selectedCountry = countryService.findById(editId);
            model.addAttribute("countryEditForm",
                    countryService.createEditForm(editId, co2EmissionService.findByCountry(selectedCountry)));
            model.addAttribute("scrollToEdit", true);
        }

        return "ScientistDashboard";
    }

    @SuppressWarnings("null")
    @PostMapping("/save")
    public String saveCountry(Authentication authentication,
                              @Valid @ModelAttribute("countryEditForm") CountryEditForm countryEditForm,
                              BindingResult bindingResult,
                              Model model) {

        @SuppressWarnings("null")
        Country country = countryService.findById(countryEditForm.getCountryId());

        if (bindingResult.hasErrors()) {
            model.addAttribute("username", authentication.getName());
            model.addAttribute("countryRows", countryService.findDashboardRows());
            model.addAttribute("editMode", true);
            model.addAttribute("scrollToEdit", true);
            return "ScientistDashboard";
        }

        try {
            countryService.updateCountryBaseData(countryEditForm.getCountryId(),
                    countryEditForm.getCountry(),
                    countryEditForm.getIsoCode());

            co2EmissionService.updateExistingRecords(country, countryEditForm.getExistingRecords());
        } catch (IllegalArgumentException ex) {
            bindingResult.addError(new FieldError(
                    "countryEditForm",
                    "existingRecords",
                    ex.getMessage()
            ));

            model.addAttribute("username", authentication.getName());
            model.addAttribute("countryRows", countryService.findDashboardRows());
            model.addAttribute("editMode", true);
            model.addAttribute("scrollToEdit", true);
            return "ScientistDashboard";
        }

        return "redirect:/scientist/dashboard?editId=" + countryEditForm.getCountryId() + "&success=saved#edit-section";
    }

    @SuppressWarnings("null")
    @PostMapping("/add-record")
    public String addRecord(Authentication authentication,
                            @Valid @ModelAttribute("countryEditForm") CountryEditForm countryEditForm,
                            BindingResult bindingResult,
                            Model model) {

        Country country = countryService.findById(countryEditForm.getCountryId());

        if (countryEditForm.getNewRecord() == null) {
            bindingResult.reject("newRecord", "Neuer Datensatz fehlt.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("username", authentication.getName());
            model.addAttribute("countryRows", countryService.findDashboardRows());
            model.addAttribute("editMode", true);
            model.addAttribute("scrollToEdit", true);
            return "ScientistDashboard";
        }

        try {
            countryService.updateCountryBaseData(countryEditForm.getCountryId(),
                    countryEditForm.getCountry(),
                    countryEditForm.getIsoCode());

            co2EmissionService.addNewRecord(country,
                    countryEditForm.getNewRecord(),
                    authentication.getName());
        } catch (IllegalArgumentException ex) {
            bindingResult.addError(new FieldError(
                    "countryEditForm",
                    "newRecord.reportingYear",
                    ex.getMessage()
            ));

            model.addAttribute("username", authentication.getName());
            model.addAttribute("countryRows", countryService.findDashboardRows());
            model.addAttribute("editMode", true);
            model.addAttribute("scrollToEdit", true);
            return "ScientistDashboard";
        }

        return "redirect:/scientist/dashboard?editId=" + countryEditForm.getCountryId() + "&success=record-added#edit-section";
    }

    @GetMapping("/cancel")
    public String cancelEdit() {
        return "redirect:/scientist/dashboard";
    }
}
