-- V6: Add TypeGG integration and Daily Competition tables

ALTER TABLE users
ADD COLUMN typegg_id VARCHAR(50),
ADD COLUMN typegg_username VARCHAR(50);

CREATE TABLE daily_quotes (
    quote_id VARCHAR(50) PRIMARY KEY,
    start_date TIMESTAMP WITH TIME ZONE NOT NULL,
    end_date TIMESTAMP WITH TIME ZONE NOT NULL,
    text TEXT NOT NULL,
    source_title VARCHAR(255),
    difficulty DOUBLE PRECISION
);

CREATE TABLE typegg_scores (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    quote_id VARCHAR(50) NOT NULL,
    wpm DOUBLE PRECISION NOT NULL,
    accuracy DOUBLE PRECISION,
    raw DOUBLE PRECISION,
    consistency DOUBLE PRECISION,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_typegg_score_user FOREIGN KEY (user_id) REFERENCES users(discord_id) ON DELETE CASCADE,
    CONSTRAINT fk_typegg_score_quote FOREIGN KEY (quote_id) REFERENCES daily_quotes(quote_id) ON DELETE CASCADE,

    CONSTRAINT unique_user_daily_quote UNIQUE (user_id, quote_id)
);