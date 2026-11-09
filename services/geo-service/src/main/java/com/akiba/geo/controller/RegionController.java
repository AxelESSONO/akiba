package com.akiba.geo.controller;

import com.akiba.geo.dto.request.RegionRequest;
import com.akiba.geo.dto.response.CityResponse;
import com.akiba.geo.dto.response.RegionResponse;
import com.akiba.geo.service.CityService;
import com.akiba.geo.service.RegionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/regions")
@RequiredArgsConstructor
public class RegionController {

    private final RegionService regionService;
    private final CityService cityService;

    @PostMapping
    public ResponseEntity<RegionResponse> create(@Valid @RequestBody RegionRequest request) {
        RegionResponse created = regionService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(created.id())
            .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    public RegionResponse getById(@PathVariable Long id) {
        return regionService.getById(id);
    }

    @GetMapping("/{regionId}/cities")
    public Page<CityResponse> getCities(
        @PathVariable Long regionId,
        @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return cityService.getByRegion(regionId, pageable);
    }

    @PutMapping("/{id}")
    public RegionResponse update(
        @PathVariable Long id,
        @Valid @RequestBody RegionRequest request) {
        return regionService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        regionService.delete(id);
    }

}