package com.akiba.geo.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "regions",
    uniqueConstraints = @UniqueConstraint(columnNames = {"country_id", "name"}))
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Region extends BaseEntity{

    @Column(nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @OneToMany(
        mappedBy = "region",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<City> cities = new ArrayList<>();

    public Region(String name) {
        this.name = name;
    }

    public void addCity(City city) {
        cities.add(city);
        city.setRegion(this);
    }

    public void removeCity(City city) {
        cities.remove(city);
        city.setRegion(null);
    }

}
