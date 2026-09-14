package de.likeherotozero.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "country")
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotBlank
    @Size(min = 2, max = 10)
    @Column(name = "iso_code", nullable = false, unique = true, length = 10)
    private String isoCode;

    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Co2EmissionRecord> emissionRecords = new ArrayList<>();

    public Country() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    public List<Co2EmissionRecord> getEmissionRecords() {
        return emissionRecords;
    }

    public void setEmissionRecords(List<Co2EmissionRecord> emissionRecords) {
        this.emissionRecords = emissionRecords;
    }
}
