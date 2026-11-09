package com.akiba.geo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CityRequest(
    @NotBlank
    @Size(max = 100)
    String name,

    @Size(max = 10)
    String postalCode,

    @NotNull
    @Positive
    Long regionId
) {
}
