package de.likeherotozero.service;

import de.likeherotozero.controller.CountryDashboardRow;
import de.likeherotozero.entity.Co2EmissionRecord;
import de.likeherotozero.entity.Country;
import de.likeherotozero.form.CountryDashboardForm;
import de.likeherotozero.form.CountryEditForm;
import de.likeherotozero.form.EmissionRecordRowForm;
import de.likeherotozero.repository.Co2EmissionRepository;
import de.likeherotozero.repository.CountryRepository;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class CountryService {

    private final CountryRepository countryRepository;
    private final Co2EmissionRepository co2EmissionRepository;

    public List<Country> findAllCountries() {
    return countryRepository.findAll();
}

    public List<Country> findAllByIds(@NonNull List<Long> ids) {
        return countryRepository.findAllById(ids);
    }

    public CountryService(CountryRepository countryRepository,
                          Co2EmissionRepository co2EmissionRepository) {
        this.countryRepository = countryRepository;
        this.co2EmissionRepository = co2EmissionRepository;
    }

    @SuppressWarnings("null")
    public List<de.likeherotozero.controller.CountryDashboardRow> findDashboardRows() {
        return countryRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Country::getName, String.CASE_INSENSITIVE_ORDER))
                .map(country -> {
                    Optional<Co2EmissionRecord> latestRecord =
                            co2EmissionRepository.findTopByCountryOrderByCreatedAtDesc(country);

                    return new CountryDashboardRow(
                            country.getId(),
                            country.getName(),
                            country.getIsoCode(),
                            latestRecord.map(Co2EmissionRecord::getCreatedAt).orElse(null),
                            latestRecord.map(record -> record.getCreatedByUser().getUsername()).orElse("-")
                    );
                })
                .toList();
    }

    @SuppressWarnings("null")
    public CountryDashboardForm createFormForCountry(Long countryId) {
        Country country = countryRepository.findById(countryId)
                .orElseThrow(() -> new IllegalArgumentException("Land nicht gefunden: " + countryId));

        Optional<Co2EmissionRecord> latestRecord =
                co2EmissionRepository.findTopByCountryOrderByReportingYearDesc(country);

        CountryDashboardForm form = new CountryDashboardForm();
        form.setCountryId(country.getId());
        form.setCountry(country.getName());
        form.setIsoCode(country.getIsoCode());
        form.setReportingYear(latestRecord.map(Co2EmissionRecord::getReportingYear)
                .orElse(LocalDate.now().getYear()));
        form.setEmissionValue(latestRecord.map(Co2EmissionRecord::getEmissionValue).orElse(null));

        return form;
    }

    public void updateCountryBaseData(CountryDashboardForm form) {
        @SuppressWarnings("null")
        Country country = countryRepository.findById(form.getCountryId())
                .orElseThrow(() -> new IllegalArgumentException("Land nicht gefunden: " + form.getCountryId()));

        country.setName(form.getCountry().trim());
        country.setIsoCode(form.getIsoCode().trim().toUpperCase());

        countryRepository.save(country);
    }

    public Country findById(@NonNull Long id) {
    return countryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Land nicht gefunden: " + id));
    }

    public CountryEditForm createEditForm(@NonNull Long countryId, List<Co2EmissionRecord> records) {
    Country country = findById(countryId);

    CountryEditForm form = new CountryEditForm();
    form.setCountryId(country.getId());
    form.setCountry(country.getName());
    form.setIsoCode(country.getIsoCode());

    List<EmissionRecordRowForm> existingRecords = records.stream().map(record -> {
        EmissionRecordRowForm row = new EmissionRecordRowForm();
        row.setRecordId(record.getId());
        row.setCountryId(country.getId());
        row.setCountry(country.getName());
        row.setReportingYear(record.getReportingYear());
        row.setEmissionValue(record.getEmissionValue());
        row.setSource(record.getSource());
        return row;
    }).toList();

    form.setExistingRecords(existingRecords);

    EmissionRecordRowForm newRecord = new EmissionRecordRowForm();
    newRecord.setCountryId(country.getId());
    newRecord.setCountry(country.getName());
    form.setNewRecord(newRecord);

    return form;
    }
    public void updateCountryBaseData(@NonNull Long countryId, String countryName, String isoCode) {
    Country country = findById(countryId);
    country.setName(countryName.trim());
    country.setIsoCode(isoCode.trim().toUpperCase());
    countryRepository.save(country);
}


}
