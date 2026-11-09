package com.akiba.geo.controller;

import com.akiba.geo.dto.request.CountryRequest;
import com.akiba.geo.dto.response.CountryResponse;
import com.akiba.geo.dto.response.RegionResponse;
import com.akiba.geo.service.CountryService;
import com.akiba.geo.service.RegionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/countries")
@RequiredArgsConstructor
public class CountryController {

    private final CountryService countryService;
    private final RegionService regionService;

    @PostMapping
    public ResponseEntity<CountryResponse> create(@Valid @RequestBody CountryRequest request){

        CountryResponse created = countryService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(created.id())
            .toUri();
        return ResponseEntity.created(location).body(created);

    }

    @GetMapping
    public List<CountryResponse> getAll() {
        return countryService.getAll();
    }

    @GetMapping("/{id}")
    public CountryResponse getById(@PathVariable Long id) {
        return countryService.getById(id);
    }

    @GetMapping("/iso/{isoCode}")
    public CountryResponse getByIsoCode(@PathVariable String isoCode) {
        return countryService.getByIsoCode(isoCode);
    }

    @GetMapping("/{countryId}/regions")
    public List<RegionResponse> getRegions(@PathVariable Long countryId) {
        return regionService.getByCountry(countryId);
    }

    @PutMapping("/{id}")
    public CountryResponse update(@PathVariable Long id, @Valid @RequestBody CountryRequest request) {
        return countryService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id)              {
        countryService.delete(id);
    }

}
