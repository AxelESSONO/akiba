package com.akiba.geo.controller;

import com.akiba.geo.dto.response.CountryResponse;
import com.akiba.geo.exception.ResourceNotFoundException;
import com.akiba.geo.service.CountryService;
import com.akiba.geo.service.RegionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CountryController.class)
class CountryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CountryService countryService;

    @MockitoBean
    private RegionService regionService;

    @Test
    @DisplayName("POST /countries : renvoie 201 avec l'en-tête Location")
    void create_shouldReturn201WithLocation() throws Exception {
        given(countryService.create(any())).willReturn(new CountryResponse(2L, "BE", "Belgique"));

        mockMvc.perform(post("/api/v1/countries")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {"isoCode": "BE", "name": "Belgique"}
                          """))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", endsWith("/api/v1/countries/2")))
            .andExpect(jsonPath("$.id").value(2))
            .andExpect(jsonPath("$.isoCode").value("BE"));
    }

    @Test
    @DisplayName("POST /countries : renvoie 400 avec le détail des champs invalides")
    void create_shouldReturn400_whenRequestIsInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/countries")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {"isoCode": "fra", "name": ""}
                         """))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.type").value(containsString("validation-error")))
            .andExpect(jsonPath("$.errors.isoCode").exists())
            .andExpect(jsonPath("$.errors.name").exists());

        verifyNoInteractions(countryService);
    }

    @Test
    @DisplayName("GET /countries/{id} : renvoie 404 au format ProblemDetail")
    void getById_shouldReturn404_whenCountryDoesNotExist() throws Exception {
        given(countryService.getById(999L)).willThrow(new ResourceNotFoundException("Pays", 999L));

        mockMvc.perform(get("/api/v1/countries/999"))
            .andExpect(status().isNotFound())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.type").value(containsString("resource-not-found")))
            .andExpect(jsonPath("$.instance").value("/api/v1/countries/999"))
            .andExpect(jsonPath("$.resource").value("Pays"));
    }

    @Test
    @DisplayName("DELETE /countries/{id} : renvoie 204")
    void delete_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/v1/countries/1"))
            .andExpect(status().isNoContent());

        verify(countryService).delete(1L);
    }

}