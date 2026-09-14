package de.likeherotozero.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "country")
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Ländername ist erforderlich")
    @Column(unique = true, nullable = false)
    private String name;

    private String code;

    private String continent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "edited_by_id")
    private ScientistUser editedBy;

    // Standard-Konstruktor
    public Country() {}

    // Getter und Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getContinent() { return continent; }
    public void setContinent(String continent) { this.continent = continent; }

    public ScientistUser getEditedBy() { return editedBy; }
    public void setEditedBy(ScientistUser editedBy) { this.editedBy = editedBy; }
}
