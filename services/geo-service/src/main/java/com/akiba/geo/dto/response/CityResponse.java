package com.akiba.geo.dto.response;

public record CityResponse(
    Long id,
    String name,
    String postalCode,
    Long regionId,
    String regionName,
    String countryIsoCode
) {
}
