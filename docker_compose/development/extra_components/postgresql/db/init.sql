CREATE TABLE organization (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR (64) NOT NULL
);
INSERT into organization(name) VALUES
    ('AIA'),
    ('CELLOCK'),
    ('OAG'),
    ('PACE'),
    ('UCY');
​
CREATE TABLE datacheckinjob (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR (64) NOT NULL
);
INSERT into datacheckinjob(name) VALUES
    ('weather_dataset'),
    ('airport_dataset'),
    ('airline_dataset'),
    ('orders_dataset'),
    ('_dataset');
​
CREATE TABLE usageanalyticslogs (
    id BIGSERIAL PRIMARY KEY,
    event_type VARCHAR (60) NOT NULL,
    meta JSON,
    created_on TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX usageanalyticslogs_idx ON usageanalyticslogs (event_type, created_on DESC);
​
​
CREATE TABLE organizationlogs (
    organization_id BIGINT REFERENCES organization (id),
    event_id BIGINT REFERENCES usageanalyticslogs (id),
    PRIMARY KEY (organization_id, event_id)
);
​
CREATE TABLE assetlogs (
    asset_id BIGINT NOT NULL,
    event_id BIGINT REFERENCES usageanalyticslogs (id),
    asset_type CHAR,
    PRIMARY KEY (asset_id, event_id)
);
​
CREATE TABLE preaggregatedstatistics (
    id SERIAL PRIMARY KEY,
    name VARCHAR(60) UNIQUE NOT NULL,
    value BIGINT NOT NULL DEFAULT 0
);
​
CREATE TABLE dataasset (
   id BIGSERIAL PRIMARY KEY,
   name VARCHAR(20) UNIQUE NOT NULL
);
INSERT  into dataasset(name) VALUES ('Test'),('Test1');
​
CREATE TABLE userinteractions (
    organization_id BIGINT REFERENCES organization (id),
    asset_id BIGINT REFERENCES dataasset (id),
    score smallint,
    PRIMARY KEY (organization_id, asset_id)
);

