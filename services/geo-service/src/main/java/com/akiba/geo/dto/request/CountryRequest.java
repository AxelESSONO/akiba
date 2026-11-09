package com.akiba.geo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CountryRequest(

    @NotBlank
    @Pattern(
        regexp = "^[A-Z]{2}$",
        message = "Le code ISO doit contenir 2 lettres majuscules"
    )
    String isoCode,

    @NotBlank
    @Size(max = 100)
    String name
) {
}
