package com.akiba.geo.repository;

import com.akiba.geo.TestcontainersConfiguration;
import com.akiba.geo.model.City;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "spring.jpa.properties.hibernate.generate_statistics=true")
@Import(TestcontainersConfiguration.class)
@Transactional
public class CityRepositoryIT {

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    private Statistics statistics;

    @BeforeEach
    void setUp() {
        statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.clear();
    }

    @Test
    @DisplayName("findAll : provoque le problème N+1 en lisant les relations")
    void findAll_shouldTriggerNPlusOne() {
        List<City> cities = cityRepository.findAll();
        cities.forEach(city -> city.getRegion().getCountry().getIsoCode());

        // 1 requête pour les villes + 3 pour les régions + 1 pour le pays
        assertThat(statistics.getPrepareStatementCount()).isEqualTo(5);
    }

    @Test
    @DisplayName("findAllWithRegionAndCountry : charge tout en une seule requête")
    void findAllWithRegionAndCountry_shouldUseSingleQuery() {
        List<City> cities = cityRepository.findAllWithRegionAndCountry();
        cities.forEach(city -> city.getRegion().getCountry().getIsoCode());

        assertThat(cities).hasSize(9);
        assertThat(statistics.getPrepareStatementCount()).isEqualTo(1);
    }

}
