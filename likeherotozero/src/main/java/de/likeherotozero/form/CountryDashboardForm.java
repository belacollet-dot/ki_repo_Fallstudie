package de.likeherotozero.form;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class CountryDashboardForm {

    @NotNull
    private Long countryId;

    @NotBlank(message = "Der Ländername darf nicht leer sein.")
    @Size(max = 100, message = "Der Ländername darf maximal 100 Zeichen lang sein.")
    private String country;

    @NotBlank(message = "Der ISO-Code darf nicht leer sein.")
    @Size(min = 2, max = 10, message = "Der ISO-Code muss zwischen 2 und 10 Zeichen lang sein.")
    private String isoCode;

    @NotNull(message = "Das Berichtsjahr ist erforderlich.")
    @Min(value = 1900, message = "Das Berichtsjahr muss zwischen 1900 und 2100 liegen.")
    @Max(value = 2100, message = "Das Berichtsjahr muss zwischen 1900 und 2100 liegen.")
    private Integer reportingYear;

    @NotNull(message = "Der Emissionswert ist erforderlich.")
    @DecimalMin(value = "0.0", inclusive = true, message = "Der Emissionswert darf nicht negativ sein.")
    private BigDecimal emissionValue;

    public CountryDashboardForm() {
    }

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

    public Integer getReportingYear() {
        return reportingYear;
    }

    public void setReportingYear(Integer reportingYear) {
        this.reportingYear = reportingYear;
    }

    public BigDecimal getEmissionValue() {
        return emissionValue;
    }

    public void setEmissionValue(BigDecimal emissionValue) {
        this.emissionValue = emissionValue;
    }
}
