package com.akiba.geo.controller;

import com.akiba.geo.dto.response.CityResponse;
import com.akiba.geo.service.CityService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CityController.class)
class CityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CityService cityService;

    private static final CityResponse LYON = new CityResponse(
        1L,
        "Lyon",
        "69001",
        2L,
        "Auvergne-Rhône-Alpes",
        "FR");

    @Test
    @DisplayName("GET /cities/{id} : renvoie la ville avec sa région et son pays")
    void getById_shouldReturnCityWithRegionAndCountry() throws Exception {
        given(cityService.getById(1L)).willReturn(LYON);

        mockMvc.perform(get("/api/v1/cities/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Lyon"))
            .andExpect(jsonPath("$.regionName").value("Auvergne-Rhône-Alpes"))
            .andExpect(jsonPath("$.countryIsoCode").value("FR"));
    }

    @Test
    @DisplayName("GET /cities?name=a : renvoie 400 sans appeler le service")
    void search_shouldReturn400_whenNameIsTooShort() throws Exception {
        mockMvc.perform(get("/api/v1/cities").param("name", "a"))
            .andExpect(status().isBadRequest());

        verifyNoInteractions(cityService);
    }

    @Test
    @DisplayName("GET /cities?name=ly : renvoie une page et applique la pagination par défaut")
    void search_shouldReturnPageWithDefaultPagination() throws Exception {
        // Given
        given(cityService.search(eq("ly"), any(Pageable.class)))
            .willReturn(new PageImpl<>(List.of(LYON), PageRequest.of(0, 20), 1));

        // When / Then : le format JSON de la page
        mockMvc.perform(get("/api/v1/cities").param("name", "ly"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].name").value("Lyon"))
            .andExpect(jsonPath("$.page.totalElements").value(1))
            .andExpect(jsonPath("$.page.size").value(20));

        // Then : le Pageable réellement transmis au service
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(cityService).search(eq("ly"), captor.capture());

        Pageable pageable = captor.getValue();
        assertThat(pageable.getPageNumber()).isZero();
        assertThat(pageable.getPageSize()).isEqualTo(20);
        assertThat(pageable.getSort().getOrderFor("name")).isNotNull();
        assertThat(pageable.getSort().getOrderFor("name").getDirection()).isEqualTo(Sort.Direction.ASC);
    }

}