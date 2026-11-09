CREATE TABLE countries(
    id          BIGSERIAL PRIMARY KEY,
    iso_code    VARCHAR(2)   NOT NULL UNIQUE,
    name        VARCHAR(100) NOT NULL,
    created_at  TIMESTAMPTZ  NOT NULL,
    updated_at  TIMESTAMPTZ  NOT NULL
);

CREATE TABLE regions(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    country_id BIGINT       NOT NULL REFERENCES countries(id),
    created_at TIMESTAMPTZ  NOT NULL,
    updated_at TIMESTAMPTZ  NOT NULL,
    CONSTRAINT uk_region_country_name UNIQUE (country_id, name)
);

CREATE TABLE cities(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    postal_code VARCHAR(10),
    region_id   BIGINT       NOT NULL REFERENCES regions(id),
    created_at  TIMESTAMPTZ  NOT NULL,
    updated_at  TIMESTAMPTZ  NOT NULL
);

CREATE INDEX idx_regions_country_id ON regions(country_id);
CREATE INDEX idx_cities_region_id   ON cities(region_id);
CREATE INDEX idx_cities_name        ON cities(name);