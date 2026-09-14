package de.likeherotozero.config;

import de.likeherotozero.entity.Co2EmissionRecord;
import de.likeherotozero.entity.Country;
import de.likeherotozero.entity.UserRole;
import de.likeherotozero.repository.Co2EmissionRepository;
import de.likeherotozero.repository.CountryRepository;
import de.likeherotozero.service.Co2EmissionService;
import de.likeherotozero.service.CountryService;
import de.likeherotozero.service.ScientistUserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(ScientistUserService userService,
                                      CountryService countryService,
                                      Co2EmissionService emissionService,
                                      CountryRepository countryRepository,
                                      Co2EmissionRepository emissionRepository) {
        return args -> {
            // Nur ausführen, wenn noch keine Daten existieren
            if (userService.getAllUsers().isEmpty()) {
                // Admin erstellen
                userService.createUser("Admin", "admin@likeherotozero.de", "admin123", UserRole.ADMIN);

                // Scientist erstellen
                userService.createUser("Scientist", "scientist@likeherotozero.de", "scientist123", UserRole.SCIENTIST);

                // Länder erstellen
                Country germany = new Country();
                germany.setName("Deutschland");
                germany.setCode("DE");
                germany.setContinent("Europa");
                countryService.saveCountry(germany);

                Country france = new Country();
                france.setName("Frankreich");
                france.setCode("FR");
                france.setContinent("Europa");
                countryService.saveCountry(france);

                Country italy = new Country();
                italy.setName("Italien");
                italy.setCode("IT");
                italy.setContinent("Europa");
                countryService.saveCountry(italy);

                // CSV-Daten importieren
                importCsvData(countryRepository, emissionRepository);
            }
        };
    }

    private void importCsvData(CountryRepository countryRepository, Co2EmissionRepository emissionRepository) throws Exception {
        ClassPathResource resource = new ClassPathResource("co2-emissions.csv");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line;
            boolean skipHeader = true;

            while ((line = reader.readLine()) != null) {
                if (skipHeader) {
                    skipHeader = false;
                    continue;
                }

                String[] parts = line.split(";");
                if (parts.length >= 3) {
                    try {
                        String countryName = parts[0].trim();
                        Integer year = Integer.parseInt(parts[1].trim());
                        Double co2Value = Double.parseDouble(parts[2].trim());

                        Country country = countryRepository.findByName(countryName).orElse(null);
                        if (country != null) {
                            Co2EmissionRecord record = new Co2EmissionRecord();
                            record.setCountry(country);
                            record.setYear(year);
                            record.setCo2Value(co2Value);
                            emissionRepository.save(record);
                        }
                    } catch (NumberFormatException e) {
                        // Ungültige Zeile überspringen
                    }
                }
            }
        }
    }
}
