package com.akiba.geo.service.impl;

import com.akiba.geo.dto.request.CountryRequest;
import com.akiba.geo.dto.response.CountryResponse;
import com.akiba.geo.exception.BusinessRuleException;
import com.akiba.geo.exception.DuplicateResourceException;
import com.akiba.geo.exception.ResourceNotFoundException;
import com.akiba.geo.mapper.CountryMapper;
import com.akiba.geo.model.Country;
import com.akiba.geo.repository.CountryRepository;
import com.akiba.geo.repository.RegionRepository;
import com.akiba.geo.service.CountryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CountryServiceImpl implements CountryService {

    private static final String RESOURCE = "Pays";

    private final CountryRepository countryRepository;
    private final RegionRepository regionRepository;
    private final CountryMapper countryMapper;

    @Override
    @Transactional
    public CountryResponse create(CountryRequest request) {
        if (countryRepository.existsByIsoCode(request.isoCode())) {
            throw new DuplicateResourceException(RESOURCE, "isoCode", request.isoCode());
        }
        Country country = countryRepository.save(new Country(request.isoCode(), request.name()));
        log.info("Pays créé : id={}, isoCode={}", country.getId(), country.getIsoCode());
        return countryMapper.toResponse(country);
    }

    @Override
    public CountryResponse getById(Long id) {
        return countryMapper.toResponse(findOrThrow(id));
    }

    @Override
    public CountryResponse getByIsoCode(String isoCode) {
        return countryRepository.findByIsoCode(isoCode.toUpperCase())
            .map(countryMapper::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException(RESOURCE, isoCode));
    }

    @Override
    public List<CountryResponse> getAll() {
        return countryMapper.toResponseList(countryRepository.findAll(Sort.by("name")));
    }

    @Override
    @Transactional
    public CountryResponse update(Long id, CountryRequest request) {
        Country country = findOrThrow(id);

        boolean isoCodeChanged = !country.getIsoCode().equals(request.isoCode());
        if (isoCodeChanged && countryRepository.existsByIsoCode(request.isoCode())) {
            throw new DuplicateResourceException(RESOURCE, "isoCode", request.isoCode());
        }

        countryMapper.updateEntity(request, country);
        log.info("Pays mis à jour : id={}", id);
        return countryMapper.toResponse(country);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Country country = findOrThrow(id);
        if (regionRepository.existsByCountryId(id)) {
            throw new BusinessRuleException("Impossible de supprimer un pays qui contient des régions");
        }
        countryRepository.delete(country);
        log.info("Pays supprimé : id={}", id);
    }

    private Country findOrThrow(Long id) {
        return countryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(RESOURCE, id));
    }
}
