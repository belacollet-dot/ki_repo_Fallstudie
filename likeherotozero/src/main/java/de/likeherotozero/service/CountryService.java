package de.likeherotozero.service;

import de.likeherotozero.entity.Country;
import de.likeherotozero.entity.ScientistUser;
import de.likeherotozero.repository.CountryRepository;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CountryService {

    private final CountryRepository countryRepository;

    public CountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    public List<Country> getAllCountries() {
        return countryRepository.findAll();
    }

    public List<Country> searchCountries(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllCountries();
        }
        // Suche nach Ländername (case-insensitive)
        return countryRepository.findAllByNameContainingIgnoreCase(query.trim());
    }

    public Country getCountryById(@NonNull Long id) {
        return countryRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Land nicht gefunden mit ID: " + id));
    }

    public Country saveCountry(@NonNull Country country) {
        return countryRepository.save(country);
    }

    public void deleteCountry(@NonNull Long id) {
        countryRepository.deleteById(id);
    }

    public List<Country> getCountriesSorted(String sortBy, boolean ascending) {
        List<Country> countries = getAllCountries();

        @SuppressWarnings("null")
        Comparator<Country> comparator = switch (sortBy) {
            case "name" -> Comparator.comparing(Country::getName, String.CASE_INSENSITIVE_ORDER);
            case "isoCode" -> Comparator.comparing(c -> c.getIsoCode() != null ? c.getIsoCode() : "", String.CASE_INSENSITIVE_ORDER);
            case "continent" -> Comparator.comparing(c -> c.getContinent() != null ? c.getContinent() : "", String.CASE_INSENSITIVE_ORDER);
            default -> Comparator.comparing(Country::getName, String.CASE_INSENSITIVE_ORDER);
        };

        if (!ascending) {
            comparator = comparator.reversed();
        }

        return countries.stream().sorted(comparator).collect(Collectors.toList());
    }

    public void assignEditor(Country country, ScientistUser user) {
        country.setEditedBy(user);
        countryRepository.save(country);
    }
}
