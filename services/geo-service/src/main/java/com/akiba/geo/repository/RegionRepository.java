package com.akiba.geo.repository;

import com.akiba.geo.model.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegionRepository extends JpaRepository<Region, Long> {

    List<Region> findByCountryIdOrderByNameAsc(Long countryId);
    boolean existsByCountryIdAndNameIgnoreCase(Long countryId, String name);

}
