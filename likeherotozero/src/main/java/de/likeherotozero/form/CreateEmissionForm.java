package de.likeherotozero.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CreateEmissionForm {

    @NotNull(message = "Land ist erforderlich")
    private Long countryId;

    @NotNull(message = "Jahr ist erforderlich")
    @Min(value = 1900, message = "Jahr muss nach 1900 sein")
    @Max(value = 2100, message = "Jahr muss vor 2100 sein")
    private Integer year;

    @NotNull(message = "CO₂-Wert ist erforderlich")
    @Min(value = 0, message = "CO₂-Wert muss positiv sein")
    @Max(value = 1000, message = "CO₂-Wert darf maximal 1000 sein")
    private Double co2Value;

    private String source;

    public CreateEmissionForm() {}

    // Getter und Setter
    public Long getCountryId() { return countryId; }
    public void setCountryId(Long countryId) { this.countryId = countryId; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public Double getCo2Value() { return co2Value; }
    public void setCo2Value(Double co2Value) { this.co2Value = co2Value; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}
