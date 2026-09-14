package de.likeherotozero.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "co2emissionrecord")
public class Co2EmissionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "emissionvalue")
    private Double emissionValue;

    @Column(name = "reportingyear")
    private Integer reportingYear;

    @NotNull(message = "Land ist erforderlich")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "countryid", nullable = false)
    private Country country;

    @Column(name = "createdat")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdbyuserid")
    private ScientistUser createdByUser;

    private String unit;

    @Column(length = 1000)
    private String comment;

    private String source;

    @NotNull(message = "CO₂-Wert ist erforderlich")
    @Min(value = 0, message = "CO₂-Wert muss positiv sein")
    @Max(value = 1000000, message = "CO₂-Wert ist zu groß")
    @Column(name = "co2value")
    private Double co2Value;

    @NotNull(message = "Jahr ist erforderlich")
    @Min(value = 1900, message = "Jahr muss nach 1900 sein")
    @Max(value = 2100, message = "Jahr muss vor 2100 sein")
    private Integer year;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scientistid")
    private ScientistUser scientist;

    public Co2EmissionRecord() {}

    // Getter und Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getEmissionValue() { return emissionValue; }
    public void setEmissionValue(Double emissionValue) { this.emissionValue = emissionValue; }

    public Integer getReportingYear() { return reportingYear; }
    public void setReportingYear(Integer reportingYear) { this.reportingYear = reportingYear; }

    public Country getCountry() { return country; }
    public void setCountry(Country country) { this.country = country; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public ScientistUser getCreatedByUser() { return createdByUser; }
    public void setCreatedByUser(ScientistUser createdByUser) { this.createdByUser = createdByUser; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public Double getCo2Value() { return co2Value; }
    public void setCo2Value(Double co2Value) { this.co2Value = co2Value; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public ScientistUser getScientist() { return scientist; }
    public void setScientist(ScientistUser scientist) { this.scientist = scientist; }
}
