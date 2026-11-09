package com.akiba.geo.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cities")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class City extends BaseEntity{

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 10)
    private String postalCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    public City(String name, String postalCode) {
        this.name = name;
        this.postalCode = postalCode;
    }

}
