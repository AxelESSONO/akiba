package com.akiba.artisan.exception;

import lombok.Getter;

@Getter
public class DuplicateResourceException extends RuntimeException {
    private final String resource;
    private final String field;
    private final Object value;

    public DuplicateResourceException(String resource, String field, Object value) {
        super("%s existe déjà avec %s = %s".formatted(resource, field, value));
        this.resource = resource;
        this.field = field;
        this.value = value;
    }
}
