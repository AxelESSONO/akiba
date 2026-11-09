package com.akiba.geo.service;

import com.akiba.geo.dto.request.RegionRequest;
import com.akiba.geo.dto.response.RegionResponse;

import java.util.List;

public interface RegionService {
    RegionResponse create(RegionRequest request);
    RegionResponse getById(Long id);
    List<RegionResponse> getByCountry(Long countryId);
    RegionResponse update(Long id, RegionRequest request);
    void delete(Long id);

}
