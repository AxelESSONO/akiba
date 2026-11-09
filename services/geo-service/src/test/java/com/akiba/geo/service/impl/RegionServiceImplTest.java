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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RegionServiceImplTest {

    @Mock
    private RegionRepository regionRepository;
    @Mock
    private CountryRepository countryRepository;
    @Mock
    private CityRepository cityRepository;
    private final RegionMapper regionMapper = Mappers.getMapper(RegionMapper.class);
    private RegionServiceImpl regionService;

    @BeforeEach
    void setUp() {
        regionService = new RegionServiceImpl(regionRepository, countryRepository, cityRepository, regionMapper);
    }

    // ---------- create ----------

    @Test
    @DisplayName("create : enregistre la région et la rattache à son pays")
    void create_shouldSaveRegionAndLinkItToCountry() {
        // Given
        Country france = country(1L, "FR", "France");
        given(countryRepository.findById(1L)).willReturn(Optional.of(france));
        given(regionRepository.existsByCountryIdAndNameIgnoreCase(1L, "Bretagne")).willReturn(false);
        given(regionRepository.save(any(Region.class))).willAnswer(invocation -> {
            Region saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", 10L);
            return saved;
        });

        // When
        RegionResponse response = regionService.create(new RegionRequest("Bretagne", 1L));

        // Then
        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.name()).isEqualTo("Bretagne");
        assertThat(response.countryId()).isEqualTo(1L);
        assertThat(response.countryName()).isEqualTo("France");
        assertThat(france.getRegions()).extracting(Region::getName).containsExactly("Bretagne");
    }

    @Test
    @DisplayName("create : refuse de créer une région dans un pays inexistant")
    void create_shouldThrowNotFound_whenCountryDoesNotExist() {
        // Given
        given(countryRepository.findById(99L)).willReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> regionService.create(new RegionRequest("Bretagne", 99L)))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("99");

        verify(regionRepository, never()).save(any());
    }

    @Test
    @DisplayName("create : refuse un nom de région déjà utilisé dans ce pays")
    void create_shouldThrowDuplicate_whenNameAlreadyExistsInCountry() {
        // Given
        Country france = country(1L, "FR", "France");
        given(countryRepository.findById(1L)).willReturn(Optional.of(france));
        given(regionRepository.existsByCountryIdAndNameIgnoreCase(1L, "Bretagne")).willReturn(true);

        // When / Then
        assertThatThrownBy(() -> regionService.create(new RegionRequest("Bretagne", 1L)))
            .isInstanceOf(DuplicateResourceException.class)
            .hasMessageContaining("Bretagne");

        verify(regionRepository, never()).save(any());
        assertThat(france.getRegions()).isEmpty();
    }

    // ---------- update ----------

    @Test
    @DisplayName("update : refuse qu'une région change de pays")
    void update_shouldThrowBusinessRule_whenCountryChanges() {
        // Given
        Country france = country(1L, "FR", "France");
        Region bretagne = region(10L, "Bretagne", france);
        given(regionRepository.findWithCountryById(10L)).willReturn(Optional.of(bretagne));

        // When / Then
        assertThatThrownBy(() -> regionService.update(10L, new RegionRequest("Bretagne", 2L)))
            .isInstanceOf(BusinessRuleException.class);

        assertThat(bretagne.getCountry()).isSameAs(france);
    }

    // ---------- delete ----------

    @Test
    @DisplayName("delete : refuse de supprimer une région qui contient des villes")
    void delete_shouldThrowBusinessRule_whenRegionHasCities() {
        // Given
        Country france = country(1L, "FR", "France");
        Region bretagne = region(10L, "Bretagne", france);
        given(regionRepository.findById(10L)).willReturn(Optional.of(bretagne));
        given(cityRepository.existsByRegionId(10L)).willReturn(true);

        // When / Then
        assertThatThrownBy(() -> regionService.delete(10L))
            .isInstanceOf(BusinessRuleException.class);

        assertThat(france.getRegions()).contains(bretagne);
    }

    @Test
    @DisplayName("delete : retire la région de son pays quand elle est vide")
    void delete_shouldRemoveRegionFromCountry_whenRegionIsEmpty() {
        // Given
        Country france = country(1L, "FR", "France");
        Region bretagne = region(10L, "Bretagne", france);
        given(regionRepository.findById(10L)).willReturn(Optional.of(bretagne));
        given(cityRepository.existsByRegionId(10L)).willReturn(false);

        // When
        regionService.delete(10L);

        // Then
        assertThat(france.getRegions()).doesNotContain(bretagne);
        assertThat(bretagne.getCountry()).isNull();
    }

    // ---------- outils de test ----------

    private static Country country(Long id, String isoCode, String name) {
        Country country = new Country(isoCode, name);
        ReflectionTestUtils.setField(country, "id", id);
        return country;
    }

    private static Region region(Long id, String name, Country country) {
        Region region = new Region(name);
        ReflectionTestUtils.setField(region, "id", id);
        country.addRegion(region);
        return region;
    }

}