package com.akiba.geo.service.impl;

import com.akiba.geo.dto.request.CityRequest;
import com.akiba.geo.dto.response.CityResponse;
import com.akiba.geo.exception.BusinessRuleException;
import com.akiba.geo.exception.ResourceNotFoundException;
import com.akiba.geo.mapper.CityMapper;
import com.akiba.geo.model.City;
import com.akiba.geo.model.Region;
import com.akiba.geo.repository.CityRepository;
import com.akiba.geo.repository.RegionRepository;
import com.akiba.geo.service.CityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CityServiceImpl implements CityService {

    private static final String RESOURCE = "Ville";

    private final CityRepository cityRepository;
    private final RegionRepository regionRepository;
    private final CityMapper cityMapper;

    @Override
    @Transactional
    public CityResponse create(CityRequest request) {

        Region region = regionRepository.findWithCountryById(request.regionId())
            .orElseThrow(() -> new ResourceNotFoundException("Région", request.regionId()));

        City city = new City(request.name(), request.postalCode());
        region.addCity(city);
        City saved = cityRepository.save(city);

        log.info("Ville créée : id={}, région={}", saved.getId(), region.getName());
        return cityMapper.toResponse(saved);

    }

    @Override
    public CityResponse getById(Long id) {
        // Quelle méthode du CityRepository charge la région ET le pays ?
        return cityMapper.toResponse(findWithRegionAndCountryOrThrow(id));
    }

    @Override
    public Page<CityResponse> getByRegion(Long regionId, Pageable pageable) {
        // 1. 404 si la région n'existe pas (comme getByCountry dans RegionServiceImpl)
        if (!regionRepository.existsByCountryId(regionId)){
            throw new ResourceNotFoundException("Country", regionId);
        }

        // 2. Charger la page, puis la convertir avec page.map(...)
        return cityRepository.findByRegionId(regionId, pageable)
            .map(cityMapper::toResponse);
    }

    @Override
    public Page<CityResponse> search(String name, Pageable pageable) {
        // Une seule ligne suffit (méthode @EntityGraph de l'étape 2)
        return cityRepository.findByNameContainingIgnoreCase(name, pageable)
            .map(cityMapper::toResponse);
    }

    @Override
    @Transactional
    public CityResponse update(Long id, CityRequest request) {
        // 1. Charger la ville avec région et pays
        City city = findWithRegionAndCountryOrThrow(id);

        // 2. BusinessRuleException si request.regionId() ≠ région actuelle
        if (!city.getRegion().getId().equals(request.regionId())){
            throw new BusinessRuleException("Une ville ne peut pas changer de région");
        }

        // 3. Mettre à jour via le mapper (pas de save : pourquoi ?)
        cityMapper.updateEntity(request, city);

        // 4. Logger, renvoyer le DTO
        log.info("Ville mise à jour : id={}", id);
        return cityMapper.toResponse(city);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        // 1. Charger la ville
        City city = cityRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(RESOURCE, id));

        // 2. La supprimer en passant par son parent (méthode utilitaire de Region)
        city.getRegion().removeCity(city);

        // 3. Logger
        log.info("Ville supprimée : id={}", id);
    }

    // Une méthode privée findWithRegionAndCountryOrThrow(...) t'évitera de dupliquer le code
    private City findWithRegionAndCountryOrThrow(Long id) {
        return cityRepository.findWithRegionAndCountryById(id)
            .orElseThrow(() -> new ResourceNotFoundException(RESOURCE, id));
    }

}
