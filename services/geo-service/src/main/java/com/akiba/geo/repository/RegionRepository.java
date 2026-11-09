package com.akiba.geo.repository;

import com.akiba.geo.model.Region;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RegionRepository extends JpaRepository<Region, Long> {

    boolean existsByCountryIdAndNameIgnoreCase(Long countryId, String name);

    @EntityGraph(attributePaths = "country")
    Optional<Region> findWithCountryById(Long id);

    @EntityGraph(attributePaths = "country")
    List<Region> findByCountryIdOrderByNameAsc(Long countryId); // annotation ajoutée sur la méthode existante

    boolean existsByCountryId(Long countryId);

}
