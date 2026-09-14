package de.likeherotozero.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "co2_emission_record")
public class Co2EmissionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Jahr ist erforderlich")
    @Min(value = 1900, message = "Jahr muss nach 1900 sein")
    @Max(value = 2100, message = "Jahr muss vor 2100 sein")
    private Integer year;

    @NotNull(message = "CO₂-Wert ist erforderlich")
    @Min(value = 0, message = "CO₂-Wert muss positiv sein")
    @Max(value = 1000, message = "CO₂-Wert darf maximal 1000 sein")
    @Column(name = "co2_value", nullable = false)
    private Double co2Value;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scientist_id")
    private ScientistUser scientist;

    // Standard-Konstruktor
    public Co2EmissionRecord() {}

    // Getter und Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public Double getCo2Value() { return co2Value; }
    public void setCo2Value(Double co2Value) { this.co2Value = co2Value; }

    public Country getCountry() { return country; }
    public void setCountry(Country country) { this.country = country; }

    public ScientistUser getScientist() { return scientist; }
    public void setScientist(ScientistUser scientist) { this.scientist = scientist; }
}
