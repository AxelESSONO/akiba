package com.akiba.geo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record RegionRequest(
    @NotBlank
    @Size(max = 100)
    String name,

    @NotNull
    @Positive
    Long countryId
) {
}
