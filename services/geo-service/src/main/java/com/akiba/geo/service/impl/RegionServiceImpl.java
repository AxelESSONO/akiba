package com.akiba.geo.service.impl;


import com.akiba.geo.dto.request.RegionRequest;
import com.akiba.geo.dto.response.RegionResponse;
import com.akiba.geo.exception.BusinessRuleException;
import com.akiba.geo.exception.DuplicateResourceException;
import com.akiba.geo.exception.ResourceNotFoundException;
import com.akiba.geo.mapper.RegionMapper;
import com.akiba.geo.model.Country;
import com.akiba.geo.model.Region;
import com.akiba.geo.repository.CityRepository;
import com.akiba.geo.repository.CountryRepository;
import com.akiba.geo.repository.RegionRepository;
import com.akiba.geo.service.RegionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RegionServiceImpl implements RegionService {

    private static final String RESOURCE = "Région";

    private final RegionRepository regionRepository;
    private final CountryRepository countryRepository;
    private final CityRepository cityRepository;
    private final RegionMapper regionMapper;

    @Override
    @Transactional
    public RegionResponse create(RegionRequest request) {
        Country country = countryRepository.findById(request.countryId())
            .orElseThrow(() -> new ResourceNotFoundException("Pays", request.countryId()));

        if (regionRepository.existsByCountryIdAndNameIgnoreCase(country.getId(), request.name())) {
            throw new DuplicateResourceException(RESOURCE, "name", request.name());
        }

        Region region = new Region(request.name());
        country.addRegion(region);
        Region saved = regionRepository.save(region);

        log.info("Région créée : id={}, pays={}", saved.getId(), country.getIsoCode());
        return regionMapper.toResponse(saved);
    }

    @Override
    public RegionResponse getById(Long id) {
        return regionMapper.toResponse(findWithCountryOrThrow(id));
    }

    @Override
    public List<RegionResponse> getByCountry(Long countryId) {
        if (!countryRepository.existsById(countryId)) {
            throw new ResourceNotFoundException("Pays", countryId);
        }
        return regionMapper.toResponseList(regionRepository.findByCountryIdOrderByNameAsc(countryId));
    }

    @Override
    @Transactional
    public RegionResponse update(Long id, RegionRequest request) {
        Region region = findWithCountryOrThrow(id);

        if (!region.getCountry().getId().equals(request.countryId())) {
            throw new BusinessRuleException("Une région ne peut pas changer de pays");
        }

        boolean nameChanged = !region.getName().equalsIgnoreCase(request.name());
        if (nameChanged && regionRepository.existsByCountryIdAndNameIgnoreCase(request.countryId(), request.name())) {
            throw new DuplicateResourceException(RESOURCE, "name", request.name());
        }

        regionMapper.updateEntity(request, region);
        log.info("Région mise à jour : id={}", id);
        return regionMapper.toResponse(region);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Region region = regionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(RESOURCE, id));

        if (cityRepository.existsByRegionId(id)) {
            throw new BusinessRuleException("Impossible de supprimer une région qui contient des villes");
        }

        region.getCountry().removeRegion(region);
        log.info("Région supprimée : id={}", id);
    }

    private Region findWithCountryOrThrow(Long id) {
        return regionRepository.findWithCountryById(id)
            .orElseThrow(() -> new ResourceNotFoundException(RESOURCE, id));
    }

}
