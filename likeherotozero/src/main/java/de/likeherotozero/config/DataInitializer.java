package de.likeherotozero.config;

import de.likeherotozero.entity.Co2EmissionRecord;
import de.likeherotozero.entity.Country;
import de.likeherotozero.entity.ScientistUser;
import de.likeherotozero.entity.UserRole;
import de.likeherotozero.repository.Co2EmissionRepository;
import de.likeherotozero.repository.CountryRepository;
import de.likeherotozero.repository.ScientistUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(
            CountryRepository countryRepository,
            Co2EmissionRepository co2EmissionRepository,
            ScientistUserRepository scientistUserRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {

            ScientistUser scientist = scientistUserRepository.findByUsername("wissenschaftler1")
                    .orElseGet(() -> {
                        ScientistUser user = new ScientistUser();
                        user.setUsername("wissenschaftler1");
                        user.setPasswordHash(passwordEncoder.encode("test1234"));
                        user.setEmail("wissenschaftler1@example.com");
                        user.setRole(UserRole.SCIENTIST);
                        user.setEnabled(true);
                        return scientistUserRepository.save(user);
                    });

            if (co2EmissionRepository.count() > 0) {
                return;
            }

            ClassPathResource resource = new ClassPathResource("co2-emissions.csv");

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

                String line;
                boolean firstLine = true;

                while ((line = reader.readLine()) != null) {
                    if (firstLine) {
                        firstLine = false;
                        continue;
                    }

                    String[] parts = line.split(",", -1);

                    if (parts.length < 4) {
                        continue;
                    }

                    String countryName = parts[0].trim();
                    String countryCode = parts[1].trim();
                    String yearText = parts[2].trim();
                    String valueText = parts[3].trim();

                    if (countryName.isEmpty() || countryCode.isEmpty() || yearText.isEmpty() || valueText.isEmpty()) {
                        continue;
                    }

                    if (!countryCode.matches("[A-Z]{3}")) {
                        continue;
                    }

                    Integer year;
                    BigDecimal emissionValue;

                    try {
                        year = Integer.valueOf(yearText);
                        emissionValue = new BigDecimal(valueText);
                    } catch (Exception e) {
                        continue;
                    }

                    Optional<Country> existingCountry = countryRepository.findByIsoCodeIgnoreCase(countryCode);

                    Country country = existingCountry.orElseGet(() -> {
                        Country newCountry = new Country();
                        newCountry.setIsoCode(countryCode);
                        newCountry.setName(countryName);
                        return countryRepository.save(newCountry);
                    });

                    boolean recordExists = co2EmissionRepository
                            .existsByCountry_IsoCodeAndReportingYear(countryCode, year);

                    if (recordExists) {
                        continue;
                    }

                    Co2EmissionRecord record = new Co2EmissionRecord();
                    record.setCountry(country);
                    record.setCreatedByUser(scientist);
                    record.setReportingYear(year);
                    record.setEmissionValue(emissionValue);
                    record.setUnit("kt");
                    record.setSource("World Bank Open Data - EN.ATM.CO2E.KT");
                    record.setComment("Initialimport aus co2-emissions.csv");

                    co2EmissionRepository.save(record);
                }
            }
        };
    }
}
