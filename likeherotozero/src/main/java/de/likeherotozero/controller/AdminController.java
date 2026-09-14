package de.likeherotozero.controller;

import de.likeherotozero.entity.ScientistUser;
import de.likeherotozero.entity.UserRole;
import de.likeherotozero.service.Co2EmissionService;
import de.likeherotozero.service.ScientistUserService;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ScientistUserService scientistUserService;
    private final Co2EmissionService co2EmissionService;

    public AdminController(ScientistUserService scientistUserService,
                           Co2EmissionService co2EmissionService) {
        this.scientistUserService = scientistUserService;
        this.co2EmissionService = co2EmissionService;
    }

    // ── Dashboard ────────────────────────────────────────────────────────────
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("scientists", scientistUserService.findAll());
        model.addAttribute("totalEmissions", co2EmissionService.findAll().size());
        return "AdminDashboard";
    }

    // ── Neuen Wissenschaftler anlegen ────────────────────────────────────────
    @GetMapping("/scientists/new")
    public String newScientist(Model model) {
        model.addAttribute("scientist", new ScientistUser());
        model.addAttribute("isNew", true);
        return "AdminScientistForm";
    }

    // ── Bestehenden Wissenschaftler bearbeiten ───────────────────────────────
    @GetMapping("/scientists/edit/{id}")
    public String editScientist(@PathVariable @NonNull Long id,
                                Model model,
                                RedirectAttributes ra) {
        return scientistUserService.findById(id)
            .map(scientist -> {
                model.addAttribute("scientist", scientist);
                model.addAttribute("isNew", false);
                return "AdminScientistForm";
            })
            .orElseGet(() -> {
                ra.addFlashAttribute("errorMessage", "Wissenschaftler nicht gefunden.");
                return "redirect:/admin/dashboard";
            });
    }

    // ── Speichern (neu + bearbeiten) ─────────────────────────────────────────
    @SuppressWarnings("null")
    @PostMapping("/scientists/save")
    public String saveScientist(
            @ModelAttribute("scientist") ScientistUser scientist,
            @RequestParam(value = "rawPassword", defaultValue = "") String rawPassword,
            @RequestParam(value = "isNew", defaultValue = "false") boolean isNew,
            RedirectAttributes ra) {

        // Pflichtfelder prüfen
        if (scientist.getUsername() == null || scientist.getUsername().isBlank()) {
            ra.addFlashAttribute("errorMessage", "Benutzername darf nicht leer sein.");
            ra.addFlashAttribute("isNew", isNew);
            return "redirect:" + (isNew ? "/admin/scientists/new"
                                        : "/admin/scientists/edit/" + scientist.getId());
        }

        if (isNew && (rawPassword == null || rawPassword.isBlank())) {
            ra.addFlashAttribute("errorMessage", "Passwort ist für neue Wissenschaftler Pflicht.");
            ra.addFlashAttribute("isNew", true);
            return "redirect:/admin/scientists/new";
        }

        // Rolle sicherstellen
        if (scientist.getRole() == null) {
            scientist.setRole(UserRole.SCIENTIST);
        }


        // Bei Bearbeitung ohne neues Passwort: altes Passwort übernehmen
        if (!isNew && (rawPassword == null || rawPassword.isBlank())) {
            scientistUserService.findById(scientist.getId()).ifPresent(existing ->
                scientist.setPasswordHash(existing.getPasswordHash())
            );
        }

        scientistUserService.saveWithRawPassword(scientist,
                (rawPassword != null && !rawPassword.isBlank()) ? rawPassword : null);

        ra.addFlashAttribute("successMessage",
            isNew ? "Wissenschaftler erfolgreich angelegt."
                  : "Wissenschaftler erfolgreich aktualisiert.");
        return "redirect:/admin/dashboard";
    }

    // ── Löschen ──────────────────────────────────────────────────────────────
    @PostMapping("/scientists/delete/{id}")
    public String deleteScientist(@PathVariable @NonNull Long id, RedirectAttributes ra) {
        scientistUserService.deleteById(id);
        ra.addFlashAttribute("successMessage", "Wissenschaftler erfolgreich gelöscht.");
        return "redirect:/admin/dashboard";
    }
}
