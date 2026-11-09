CREATE TABLE trades (
    id  BIGSERIAL PRIMARY KEY,
    code VARCHAR(50)  NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    created_at  TIMESTAMPTZ  NOT NULL,
    updated_at  TIMESTAMPTZ  NOT NULL
);

CREATE TABLE artisans (
    id          BIGSERIAL PRIMARY KEY,
    first_name  VARCHAR(100) NOT NULL,
    last_name   VARCHAR(100) NOT NULL,
    email       VARCHAR(255) NOT NULL UNIQUE,
    phone       VARCHAR(20),
    city_id     BIGINT       NOT NULL,
    status      VARCHAR(30)  NOT NULL,
    created_at  TIMESTAMPTZ  NOT NULL,
    updated_at  TIMESTAMPTZ  NOT NULL,
    CONSTRAINT chk_artisans_status CHECK (status IN ('PENDING_VALIDATION', 'ACTIVE', 'SUSPENDED'))
);

CREATE INDEX idx_artisans_city_id ON artisans(city_id);

CREATE TABLE artisan_trades (
    artisan_id  BIGINT NOT NULL REFERENCES artisans(id) ON DELETE CASCADE,
    trade_id    BIGINT NOT NULL REFERENCES trades(id),
    PRIMARY KEY (artisan_id, trade_id)
);

CREATE INDEX idx_artisan_trades_trade_id ON artisan_trades(trade_id);

CREATE TABLE artisan_profiles (
    artisan_id          BIGINT PRIMARY KEY REFERENCES artisans(id) ON DELETE CASCADE,
    bio                 VARCHAR(2000),
    years_of_experience INTEGER CHECK (years_of_experience >= 0),
    siret               VARCHAR(14) UNIQUE,
    created_at          TIMESTAMPTZ NOT NULL,
    updated_at          TIMESTAMPTZ NOT NULL
);