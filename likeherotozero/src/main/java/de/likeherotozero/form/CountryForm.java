package de.likeherotozero.form;

import jakarta.validation.constraints.NotBlank;

public class CountryForm {

    private Long id;

    @NotBlank(message = "Ländername ist erforderlich")
    private String name;

    private String code;

    private String continent;

    public CountryForm() {}

    // Getter und Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getContinent() { return continent; }
    public void setContinent(String continent) { this.continent = continent; }
}
