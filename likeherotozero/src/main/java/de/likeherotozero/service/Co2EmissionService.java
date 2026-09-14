package de.likeherotozero.service;

import de.likeherotozero.entity.Co2EmissionRecord;
import de.likeherotozero.entity.Country;
import de.likeherotozero.entity.ScientistUser;
import de.likeherotozero.form.CountryDashboardForm;
import de.likeherotozero.form.EmissionRecordRowForm;
import de.likeherotozero.repository.Co2EmissionRepository;
import de.likeherotozero.repository.CountryRepository;
import de.likeherotozero.repository.ScientistUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Co2EmissionService {

    private final Co2EmissionRepository co2EmissionRepository;
    private final CountryRepository countryRepository;
    private final ScientistUserRepository scientistUserRepository;

    public Co2EmissionService(Co2EmissionRepository co2EmissionRepository,
                              CountryRepository countryRepository,
                              ScientistUserRepository scientistUserRepository) {
        this.co2EmissionRepository = co2EmissionRepository;
        this.countryRepository = countryRepository;
        this.scientistUserRepository = scientistUserRepository;
    }

    public List<Co2EmissionRecord> findByCountry1(Country country) {
        return co2EmissionRepository.findByCountryOrderByReportingYearDesc(country);
    }

    public void createEmission(CountryDashboardForm form, String username) {
        @SuppressWarnings("null")
        Country country = countryRepository.findById(form.getCountryId())
                .orElseThrow(() -> new IllegalArgumentException("Land nicht gefunden: " + form.getCountryId()));

        ScientistUser scientistUser = scientistUserRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Benutzer nicht gefunden: " + username));

        Co2EmissionRecord record = new Co2EmissionRecord();
        record.setCountry(country);
        record.setCreatedByUser(scientistUser);
        record.setReportingYear(form.getReportingYear());
        record.setEmissionValue(form.getEmissionValue());
        record.setUnit("tCO2");
        record.setSource("Dashboard");
        record.setComment("Datensatz über Scientist Dashboard erstellt.");

        co2EmissionRepository.save(record);
    }

    public List<Co2EmissionRecord> findByCountry(Country country) {
    return co2EmissionRepository.findByCountryOrderByReportingYearDesc(country);
}

public void updateExistingRecords(Country country, List<EmissionRecordRowForm> existingRecords) {
    for (EmissionRecordRowForm row : existingRecords) {
        @SuppressWarnings("null")
        Co2EmissionRecord record = co2EmissionRepository.findById(row.getRecordId())
                .orElseThrow(() -> new IllegalArgumentException("Datensatz nicht gefunden: " + row.getRecordId()));

        if (co2EmissionRepository.existsByCountryAndReportingYearAndIdNot(country, row.getReportingYear(), record.getId())) {
            throw new IllegalArgumentException("Für dieses Land existiert bereits ein Datensatz für das Jahr " + row.getReportingYear() + ".");
        }

        record.setReportingYear(row.getReportingYear());
        record.setEmissionValue(row.getEmissionValue());
        record.setSource(row.getSource());
        record.setUnit("kt");

        co2EmissionRepository.save(record);
    }
}

public void addNewRecord(Country country, EmissionRecordRowForm newRecord, String username) {
    if (newRecord.getReportingYear() == null ||
        newRecord.getEmissionValue() == null ||
        newRecord.getSource() == null ||
        newRecord.getSource().isBlank()) {
        throw new IllegalArgumentException("Für einen neuen Datensatz müssen Reporting Year, Emission Value und Source ausgefüllt sein.");
    }

    if (co2EmissionRepository.existsByCountryAndReportingYear(country, newRecord.getReportingYear())) {
        throw new IllegalArgumentException("Für dieses Land existiert bereits ein Datensatz für das Jahr " + newRecord.getReportingYear() + ".");
    }

    ScientistUser scientistUser = scientistUserRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("Benutzer nicht gefunden: " + username));

    Co2EmissionRecord record = new Co2EmissionRecord();
    record.setCountry(country);
    record.setCreatedByUser(scientistUser);
    record.setReportingYear(newRecord.getReportingYear());
    record.setEmissionValue(newRecord.getEmissionValue());
    record.setSource(newRecord.getSource().trim());
    record.setUnit("kt");
    record.setComment("Datensatz über Scientist Dashboard erstellt.");

    co2EmissionRepository.save(record);
}

public List<Co2EmissionRecord> findAll() {
    return co2EmissionRepository.findAll();
}

public List<Co2EmissionRecord> findByCreatedByUser(ScientistUser user) {
    return co2EmissionRepository.findByCreatedByUser(user);
}
}
