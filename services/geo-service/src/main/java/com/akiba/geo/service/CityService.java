package com.akiba.geo.service;

import com.akiba.geo.dto.request.CityRequest;
import com.akiba.geo.dto.response.CityResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CityService {

    CityResponse create(CityRequest request);

    CityResponse getById(Long id);

    Page<CityResponse> getByRegion(Long regionId, Pageable pageable);

    Page<CityResponse> search(String name, Pageable pageable);

    CityResponse update(Long id, CityRequest request);

    void delete(Long id);

}
