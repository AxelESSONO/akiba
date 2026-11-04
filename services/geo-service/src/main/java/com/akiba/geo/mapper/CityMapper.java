package com.akiba.geo.mapper;

import com.akiba.geo.config.GeoMapperConfig;
import com.akiba.geo.dto.request.CityRequest;
import com.akiba.geo.dto.response.CityResponse;
import com.akiba.geo.model.City;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(config = GeoMapperConfig.class)
public interface CityMapper {

    @Mapping(target = "regionId", source = "region.id")
    @Mapping(target = "regionName", source = "region.name")
    @Mapping(target = "countryIsoCode", source = "region.country.isoCode")
    CityResponse toResponse(City city);

    List<CityResponse> toResponseList(List<City> cities);

    @Mapping(target = "region", ignore = true)
    void updateEntity(CityRequest request, @MappingTarget City city);

}
