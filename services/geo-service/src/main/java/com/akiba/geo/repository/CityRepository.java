package com.akiba.geo.repository;

import com.akiba.geo.model.City;
import com.akiba.geo.model.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CityRepository extends JpaRepository<City, Long> {

    Page<City> findByRegionId(Long regionId, Pageable pageable);

    Page<City> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("""
        SELECT c FROM City c
        JOIN FETCH c.region r
        JOIN FETCH r.country
        ORDER BY c.name
        """)
    List<City> findAllWithRegionAndCountry();

    @EntityGraph(attributePaths = {"region", "region.country"})
    Page<City> findWithRegionByNameContainingIgnoreCase(String name, Pageable pageable);

}
