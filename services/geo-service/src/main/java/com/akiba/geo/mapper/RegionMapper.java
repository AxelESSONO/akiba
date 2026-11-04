package com.akiba.geo.mapper;

import com.akiba.geo.config.GeoMapperConfig;
import com.akiba.geo.dto.request.RegionRequest;
import com.akiba.geo.dto.response.RegionResponse;
import com.akiba.geo.model.Region;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(config = GeoMapperConfig.class)
public interface RegionMapper {

    @Mapping(target = "countryId", source = "country.id")
    @Mapping(target = "countryName", source = "country.name")
    RegionResponse toResponse(Region region);

    List<RegionResponse> toResponseList(List<Region> regions);

    @Mapping(target = "country", ignore = true)
    @Mapping(target = "cities", ignore = true)
    void updateEntity(RegionRequest request, @MappingTarget Region region);

}
