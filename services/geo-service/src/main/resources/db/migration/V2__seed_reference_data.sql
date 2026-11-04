INSERT INTO countries (iso_code, name, created_at, updated_at)
VALUES ('FR', 'France', NOW(), NOW());

INSERT INTO regions (name, country_id, created_at, updated_at)
SELECT r.name, c.id, NOW(), NOW()
FROM countries c,
     (VALUES ('Île-de-France'), ('Auvergne-Rhône-Alpes'), ('Provence-Alpes-Côte d''Azur')) AS r(name)
WHERE c.iso_code = 'FR';

INSERT INTO cities (name, postal_code, region_id, created_at, updated_at)
SELECT v.name, v.postal_code, r.id, NOW(), NOW()
FROM regions r
    JOIN (VALUES
        ('Paris', '75001', 'Île-de-France'),
        ('Versailles', '78000', 'Île-de-France'),
        ('Provins', '77160', 'Île-de-France'),
        ('Lyon', '69001', 'Auvergne-Rhône-Alpes'),
        ('Grenoble', '38000', 'Auvergne-Rhône-Alpes'),
        ('Clermont-Ferrand', '63000', 'Auvergne-Rhône-Alpes'),
        ('Marseille', '13001', 'Provence-Alpes-Côte d''Azur'),
        ('Nice', '06000', 'Provence-Alpes-Côte d''Azur'),
        ('Toulon', '83000', 'Provence-Alpes-Côte d''Azur')
) AS v(name, postal_code, region_name) ON v.region_name = r.name;