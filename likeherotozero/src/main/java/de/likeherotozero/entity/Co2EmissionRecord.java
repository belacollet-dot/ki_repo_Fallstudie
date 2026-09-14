package de.likeherotozero.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "co2_emission_record")
public class Co2EmissionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private ScientistUser createdByUser;

    @NotNull
    @Min(1900)
    @Max(2100)
    @Column(name = "reporting_year", nullable = false)
    private Integer reportingYear;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "emission_value", nullable = false, precision = 15, scale = 2)
    private BigDecimal emissionValue;

    @NotBlank
    @Size(max = 20)
    @Column(name = "unit", nullable = false, length = 20)
    private String unit;

    @NotBlank
    @Size(max = 255)
    @Column(name = "source", nullable = false, length = 255)
    private String source;

    @Size(max = 1000)
    @Column(name = "comment", length = 1000)
    private String comment;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Co2EmissionRecord() {
    }

    public Long getId() {
        return id;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public ScientistUser getCreatedByUser() {
        return createdByUser;
    }

    public void setCreatedByUser(ScientistUser createdByUser) {
        this.createdByUser = createdByUser;
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
