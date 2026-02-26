CREATE TABLE IF NOT EXISTS reservation_reports (
    id UUID PRIMARY KEY,
    reservation_id UUID NOT NULL UNIQUE,
    room_id VARCHAR(255) NOT NULL,
    start_at TIMESTAMP NOT NULL,
    end_at TIMESTAMP NOT NULL,
    title VARCHAR(255) NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    report_created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS processed_events (
    id UUID PRIMARY KEY,
    event_id UUID NOT NULL UNIQUE,
    event_type VARCHAR(255) NOT NULL,
    processed_at TIMESTAMP NOT NULL,
    notes VARCHAR(500)
);

CREATE INDEX idx_reservation_reports_reservation_id ON reservation_reports(reservation_id);
CREATE INDEX idx_reservation_reports_room_id ON reservation_reports(room_id);
CREATE INDEX idx_processed_events_event_id ON processed_events(event_id);
CREATE INDEX idx_processed_events_event_type ON processed_events(event_type);

