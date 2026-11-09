package com.akiba.artisan.model;

import com.akiba.artisan.exception.BusinessRuleException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "artisans")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Artisan  extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(nullable = false)
    private Long cityId;

    @Setter(AccessLevel.NONE)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ArtisanStatus status;

    @Setter(AccessLevel.NONE)
    @ManyToMany
    @JoinTable(
        name = "artisan_trades",
        joinColumns = @JoinColumn(name = "artisan_id"),
        inverseJoinColumns = @JoinColumn(name = "trade_id")
    )
    private Set<Trade> trades = new HashSet<>();

    public Artisan(String firstName, String lastName, String email, Long cityId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.cityId = cityId;
        this.status = ArtisanStatus.PENDING_VALIDATION;
    }

    public void addTrade(Trade trade) {
        trades.add(trade);
    }

    public void removeTrade(Trade trade) {
        trades.remove(trade);
    }

    public void activate() {
        if (trades.isEmpty()) {
            throw new BusinessRuleException("Un artisan doit exercer au moins un métier pour être activé");
        }
        this.status = ArtisanStatus.ACTIVE;
    }

    public void suspend() {
        this.status = ArtisanStatus.SUSPENDED;
    }

}
