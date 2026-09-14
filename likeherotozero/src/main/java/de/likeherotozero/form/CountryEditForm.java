package de.likeherotozero.form;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

public class CountryEditForm {

    @NotNull
    private Long countryId;

    @NotBlank(message = "Country darf nicht leer sein.")
    @Size(max = 100, message = "Country darf maximal 100 Zeichen lang sein.")
    private String country;

    @NotBlank(message = "ISO-Code darf nicht leer sein.")
    @Size(min = 2, max = 10, message = "ISO-Code muss zwischen 2 und 10 Zeichen lang sein.")
    private String isoCode;

    @Valid
    private List<EmissionRecordRowForm> existingRecords = new ArrayList<>();

    @Valid
    private EmissionRecordRowForm newRecord = new EmissionRecordRowForm();

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    public List<EmissionRecordRowForm> getExistingRecords() {
        return existingRecords;
    }

    public void setExistingRecords(List<EmissionRecordRowForm> existingRecords) {
        this.existingRecords = existingRecords;
    }

    public EmissionRecordRowForm getNewRecord() {
        return newRecord;
    }

    public void setNewRecord(EmissionRecordRowForm newRecord) {
        this.newRecord = newRecord;
    }
}
