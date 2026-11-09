package com.akiba.geo.dto.response;

public record RegionResponse(
    Long id,
    String name,
    Long countryId,
    String countryName
) {
}
