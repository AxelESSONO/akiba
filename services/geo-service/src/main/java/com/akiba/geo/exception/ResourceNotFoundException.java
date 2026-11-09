package com.akiba.geo.exception;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException{

    private final String resource;
    private final Object identifier;

    public ResourceNotFoundException(String resource, Object identifier) {
        super("%s introuvable : %s".formatted(resource, identifier));
        this.resource = resource;
        this.identifier = identifier;
    }

}
