package com.akiba.geo.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "countries")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Country extends BaseEntity{

    @Column(nullable = false, unique = true, length = 2)
    private String isoCode; // "GA", "FR", "CI"

    @Column(nullable = false, length = 100)
    private String name;

    @OneToMany(
        mappedBy = "country",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<Region> regions = new ArrayList<>();

    public Country(String isoCode, String name) {
        this.isoCode = isoCode;
        this.name = name;
    }

    public void addRegion(Region region) {
        regions.add(region);
        region.setCountry(this);
    }

    public void removeRegion(Region region) {
        regions.remove(region);
        region.setCountry(null);
    }

}