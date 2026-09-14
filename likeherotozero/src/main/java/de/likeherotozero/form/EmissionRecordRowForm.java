package de.likeherotozero.form;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class EmissionRecordRowForm {

    private Long recordId;

    private Long countryId;

    @NotBlank(message = "Country darf nicht leer sein.")
    @Size(max = 100, message = "Country darf maximal 100 Zeichen lang sein.")
    private String country;

    @NotNull(message = "Reporting Year ist erforderlich.")
    @Min(value = 1900, message = "Reporting Year muss zwischen 1900 und 2100 liegen.")
    @Max(value = 2100, message = "Reporting Year muss zwischen 1900 und 2100 liegen.")
    private Integer reportingYear;

    @NotNull(message = "Emission Value ist erforderlich.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Emission Value muss größer als 0 sein.")
    private BigDecimal emissionValue;

    @NotBlank(message = "Source darf nicht leer sein.")
    @Size(max = 255, message = "Source darf maximal 255 Zeichen lang sein.")
    private String source;

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
