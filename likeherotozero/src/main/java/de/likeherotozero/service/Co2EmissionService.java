package de.likeherotozero.service;

import de.likeherotozero.entity.Co2EmissionRecord;
import de.likeherotozero.repository.Co2EmissionRepository;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class Co2EmissionService {

    private final Co2EmissionRepository emissionRepository;

    public Co2EmissionService(Co2EmissionRepository emissionRepository) {
        this.emissionRepository = emissionRepository;
    }

    public List<Co2EmissionRecord> getAllEmissions() {
        return emissionRepository.findAll();
    }

    public List<Co2EmissionRecord> getEmissionsByCountry(Long countryId) {
        return emissionRepository.findByCountryIdOrderByYearDesc(countryId);
    }

    public List<Co2EmissionRecord> getEmissionsByScientist(Long scientistId) {
        return emissionRepository.findByScientistIdOrderByYearDesc(scientistId);
    }

    public Co2EmissionRecord getEmissionById(@NonNull Long id) {
        return emissionRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Emissionsrekord nicht gefunden mit ID: " + id));
    }

    public Co2EmissionRecord saveEmission(@NonNull Co2EmissionRecord record) {
        return emissionRepository.save(record);
    }

    public void deleteEmission(@NonNull Long id) {
        emissionRepository.deleteById(id);
    }

    public List<Co2EmissionRecord> filterEmissions(Long countryId, Integer year, Double min, Double max) {
        List<Co2EmissionRecord> emissions = getAllEmissions();

        return emissions.stream()
            .filter(e -> countryId == null || e.getCountry().getId().equals(countryId))
            .filter(e -> year == null || e.getYear().equals(year))
            .filter(e -> min == null || e.getCo2Value() >= min)
            .filter(e -> max == null || e.getCo2Value() <= max)
            .collect(Collectors.toList());
    }

    public List<Co2EmissionRecord> getEmissionsSorted(String sortBy, boolean ascending) {
        List<Co2EmissionRecord> emissions = getAllEmissions();

        @SuppressWarnings("null")
        Comparator<Co2EmissionRecord> comparator = switch (sortBy) {
            case "year" -> Comparator.comparing(Co2EmissionRecord::getYear);
            case "co2Value" -> Comparator.comparing(Co2EmissionRecord::getCo2Value);
            case "country" -> Comparator.comparing(e -> e.getCountry().getName(), String.CASE_INSENSITIVE_ORDER);
            default -> Comparator.comparing(Co2EmissionRecord::getYear);
        };

        if (!ascending) {
            comparator = comparator.reversed();
        }

        return emissions.stream().sorted(comparator).collect(Collectors.toList());
    }

    @SuppressWarnings("null")
    public double calculateAverage(List<Co2EmissionRecord> records) {
        if (records.isEmpty()) return 0.0;
        return records.stream()
            .mapToDouble(Co2EmissionRecord::getCo2Value)
            .average()
            .orElse(0.0);
    }

    @SuppressWarnings("null")
    public double calculateMin(List<Co2EmissionRecord> records) {
        if (records.isEmpty()) return 0.0;
        return records.stream()
            .mapToDouble(Co2EmissionRecord::getCo2Value)
            .min()
            .orElse(0.0);
    }

    @SuppressWarnings("null")
    public double calculateMax(List<Co2EmissionRecord> records) {
        if (records.isEmpty()) return 0.0;
        return records.stream()
            .mapToDouble(Co2EmissionRecord::getCo2Value)
            .max()
            .orElse(0.0);
    }
}
