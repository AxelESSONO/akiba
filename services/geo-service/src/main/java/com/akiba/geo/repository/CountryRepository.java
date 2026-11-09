package com.akiba.geo.repository;

import com.akiba.geo.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, Long> {

    Optional<Country> findByIsoCode(String isoCode);
    boolean existsByIsoCode(String isoCode);

}
