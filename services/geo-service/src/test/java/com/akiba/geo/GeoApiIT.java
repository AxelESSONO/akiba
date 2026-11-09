package com.akiba.geo;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@Transactional
public class GeoApiIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Scénario complet : créer un pays, une région et une ville, puis relire la ville")
    void fullScenario_shouldCreateAndReadCity() throws Exception {
        Long countryId = createAndGetId("/api/v1/countries", """
                {"isoCode": "BE", "name": "Belgique"}
                """);

        Long regionId = createAndGetId("/api/v1/regions", """
                {"name": "Wallonie", "countryId": %d}
                """.formatted(countryId));

        Long cityId = createAndGetId("/api/v1/cities", """
                {"name": "Namur", "postalCode": "5000", "regionId": %d}
                """.formatted(regionId));

        mockMvc.perform(get("/api/v1/cities/{id}", cityId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Namur"))
            .andExpect(jsonPath("$.regionName").value("Wallonie"))
            .andExpect(jsonPath("$.countryIsoCode").value("BE"));
    }

    @Test
    @DisplayName("POST /countries : renvoie 409 pour un code ISO déjà en base")
    void createCountry_shouldReturn409_whenIsoCodeExists() throws Exception {
        mockMvc.perform(post("/api/v1/countries")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                                {"isoCode": "FR", "name": "France"}
                                """))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.type").value(containsString("duplicate-resource")));
    }

    @Test
    @DisplayName("DELETE /countries/{id} : renvoie 409 pour un pays qui contient des régions")
    void deleteCountry_shouldReturn409_whenCountryHasRegions() throws Exception {
        String body = mockMvc.perform(get("/api/v1/countries/iso/FR"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        Integer franceId = JsonPath.read(body, "$.id");

        mockMvc.perform(delete("/api/v1/countries/{id}", franceId))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.type").value(containsString("business-rule-violation")));
    }

    private Long createAndGetId(String url, String json) throws Exception {
        String body = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(body, "$.id")).longValue();
    }

}
