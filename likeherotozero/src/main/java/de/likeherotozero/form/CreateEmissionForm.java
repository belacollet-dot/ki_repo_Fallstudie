package de.likeherotozero.form;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateEmissionForm {

    @NotBlank
    private String country;

    @NotNull
    @Min(1900)
    private Integer year;

    @NotNull
    @DecimalMin("0.0")
    private Double co2Value;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Double getCo2Value() {
        return co2Value;
    }

    public void setCo2Value(Double co2Value) {
        this.co2Value = co2Value;
    }
}
