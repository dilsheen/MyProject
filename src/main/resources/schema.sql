CREATE TABLE IF NOT EXISTS events (
    event_id TEXT PRIMARY KEY,
    type TEXT NOT NULL,
    occurred_at TIMESTAMPTZ NOT NULL,
    value NUMERIC
);
