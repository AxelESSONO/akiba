package com.akiba.artisan.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "trades")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Trade extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String code; // "PLUMBER", "ELECTRICIAN"…

    @Column(nullable = false, length = 100)
    private String name; // "Plombier", "Électricien"…

    @Column(length = 500)
    private String description;

    public Trade(String code, String name) {
        this.code = code;
        this.name = name;
    }

}
