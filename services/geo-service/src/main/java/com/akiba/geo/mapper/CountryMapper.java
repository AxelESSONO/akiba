package com.akiba.geo.mapper;

import com.akiba.geo.config.GeoMapperConfig;
import com.akiba.geo.dto.request.CountryRequest;
import com.akiba.geo.dto.response.CountryResponse;
import com.akiba.geo.model.Country;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(config = GeoMapperConfig.class)
public interface CountryMapper {

    CountryResponse toResponse(Country country);

    List<CountryResponse> toResponseList(List<Country> countries);

    @Mapping(target = "regions", ignore = true)
    void updateEntity(CountryRequest request, @MappingTarget Country country);

}
