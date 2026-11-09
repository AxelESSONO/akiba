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
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CountryServiceImplTest {

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private RegionRepository regionRepository;
    private final CountryMapper countryMapper = Mappers.getMapper(CountryMapper.class);
    private CountryServiceImpl countryService;

    @BeforeEach
    void setUp(){
        countryService = new CountryServiceImpl(countryRepository, regionRepository, countryMapper);
    }

    @Test
    @DisplayName("create : enregistre le pays quand le code ISO est libre")
    void create_shouldSaveCountry_whenIsoCodeIsFree() {
        // Given
        var request = new CountryRequest("BE", "Belgique");
        given(countryRepository.existsByIsoCode("BE")).willReturn(false);
        given(countryRepository.save(any(Country.class))).willAnswer(invocation -> {
            Country saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", 2L);
            return saved;
        });

        // When
        CountryResponse response = countryService.create(request);

        // Then
        assertThat(response.id()).isEqualTo(2L);
        assertThat(response.isoCode()).isEqualTo("BE");
        assertThat(response.name()).isEqualTo("Belgique");
    }

    @Test
    @DisplayName("create : refuse un code ISO déjà utilisé, sans rien enregistrer")
    void create_shouldThrowDuplicate_whenIsoCodeExists() {
        // Given
        var request = new CountryRequest("FR", "France");
        given(countryRepository.existsByIsoCode("FR")).willReturn(true);

        // When / Then
        assertThatThrownBy(() -> countryService.create(request))
            .isInstanceOf(DuplicateResourceException.class)
            .hasMessageContaining("FR");

        verify(countryRepository, never()).save(any());
    }

    @Test
    @DisplayName("getById : lève une exception quand le pays n'existe pas")
    void getById_shouldThrowNotFound_whenCountryDoesNotExist() {
        given(countryRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> countryService.getById(999L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("999");
    }

    @Test
    @DisplayName("update : modifie l'entité sans appeler save (dirty checking)")
    void update_shouldModifyEntityWithoutSave() {
        // Given
        Country france = country(1L, "FR", "France");
        given(countryRepository.findById(1L)).willReturn(Optional.of(france));

        // When
        CountryResponse response = countryService.update(1L, new CountryRequest("FR", "République française"));

        // Then
        assertThat(response.name()).isEqualTo("République française");
        assertThat(france.getName()).isEqualTo("République française");
        verify(countryRepository, never()).save(any());
    }

    @Test
    @DisplayName("delete : refuse de supprimer un pays qui contient des régions")
    void delete_shouldThrowBusinessRule_whenCountryHasRegions() {
        // Given
        given(countryRepository.findById(1L)).willReturn(Optional.of(country(1L, "FR", "France")));
        given(regionRepository.existsByCountryId(1L)).willReturn(true);

        // When / Then
        assertThatThrownBy(() -> countryService.delete(1L))
            .isInstanceOf(BusinessRuleException.class);

        verify(countryRepository, never()).delete(any());
    }

    private static Country country(Long id, String isoCode, String name) {
        Country country = new Country(isoCode, name);
        ReflectionTestUtils.setField(country, "id", id);
        return country;
    }

}