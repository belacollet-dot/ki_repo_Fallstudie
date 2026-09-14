package de.likeherotozero.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CountryForm {

    private Long id;

    @NotBlank(message = "Der Ländername darf nicht leer sein.")
    @Size(max = 100, message = "Der Ländername darf maximal 100 Zeichen lang sein.")
    private String name;

    @NotBlank(message = "Der ISO-Code darf nicht leer sein.")
    @Size(min = 2, max = 10, message = "Der ISO-Code muss zwischen 2 und 10 Zeichen lang sein.")
    private String isoCode;

    public CountryForm() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
