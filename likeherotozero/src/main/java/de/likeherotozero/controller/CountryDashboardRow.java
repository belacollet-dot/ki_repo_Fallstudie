package de.likeherotozero.controller;

import java.time.LocalDateTime;

public class CountryDashboardRow {

    private final Long countryId;
    private final String country;
    private final String isoCode;
    private final LocalDateTime createdAt;
    private final String createdByUser;

    public CountryDashboardRow(Long countryId,
                               String country,
                               String isoCode,
                               LocalDateTime createdAt,
                               String createdByUser) {
        this.countryId = countryId;
        this.country = country;
        this.isoCode = isoCode;
        this.createdAt = createdAt;
        this.createdByUser = createdByUser;
    }

    public Long getCountryId() {
        return countryId;
    }

    public String getCountry() {
        return country;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getCreatedByUser() {
        return createdByUser;
    }
}