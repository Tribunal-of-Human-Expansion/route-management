-- Segments (the graph nodes/edges)
CREATE TABLE IF NOT EXISTS segments (
    segment_id        VARCHAR(64) PRIMARY KEY,
    origin_node       VARCHAR(128) NOT NULL,
    destination_node  VARCHAR(128) NOT NULL,
    authoritative_region VARCHAR(64) NOT NULL,
    description       TEXT,
    active            BOOLEAN NOT NULL DEFAULT TRUE,
    created_at        TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Capacity policies (versioned, time-windowed)
CREATE TABLE IF NOT EXISTS segment_capacity_policies (
    id                BIGSERIAL PRIMARY KEY,
    segment_id        VARCHAR(64) NOT NULL REFERENCES segments(segment_id),
    map_version       VARCHAR(32) NOT NULL,
    vehicle_type      VARCHAR(32) NOT NULL DEFAULT 'ANY',
    max_vehicles      INT NOT NULL,
    time_window_start TIME,        -- NULL = all day
    time_window_end   TIME,
    valid_from        DATE NOT NULL,
    valid_to          DATE,        -- NULL = indefinite
    created_at        TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Temporary closures
CREATE TABLE IF NOT EXISTS temporary_closures (
    id                BIGSERIAL PRIMARY KEY,
    segment_id        VARCHAR(64) NOT NULL REFERENCES segments(segment_id),
    reason            TEXT,
    closure_start     TIMESTAMP NOT NULL,
    closure_end       TIMESTAMP,   -- NULL = indefinite
    map_version       VARCHAR(32) NOT NULL,
    created_at        TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Map versions (audit trail)
CREATE TABLE IF NOT EXISTS map_versions (
    version           VARCHAR(32) PRIMARY KEY,
    description       TEXT,
    is_current        BOOLEAN NOT NULL DEFAULT FALSE,
    created_at        TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Seed current map version if not exists
INSERT INTO map_versions (version, description, is_current)
SELECT 'v2025.04', 'Initial version', TRUE
WHERE NOT EXISTS (SELECT 1 FROM map_versions WHERE version = 'v2025.04');
