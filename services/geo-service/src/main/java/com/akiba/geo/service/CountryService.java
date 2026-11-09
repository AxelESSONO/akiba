package com.akiba.geo.service;

import com.akiba.geo.dto.request.CountryRequest;
import com.akiba.geo.dto.response.CountryResponse;

import java.util.List;

public interface CountryService {

    CountryResponse create(CountryRequest request);

    CountryResponse getById(Long id);

    CountryResponse getByIsoCode(String isoCode);

    List<CountryResponse> getAll();

    CountryResponse update(Long id, CountryRequest request);

    void delete(Long id);

}
