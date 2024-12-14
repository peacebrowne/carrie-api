
-- Enable the uuid-ossp extension for UUID generation
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create the authors table
CREATE TABLE IF NOT EXISTS authors (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name TEXT NOT NULL UNIQUE,
    email TEXT NOT NULL UNIQUE,
    dob DATE,
    gender TEXT,
    password_hash TEXT,
    password_salt TEXT,
    created_at TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP
);

-- Create the articles table
CREATE TABLE IF NOT EXISTS articles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title TEXT NOT NULL,
    authorId UUID NOT NULL,
    content TEXT,
    updated_at TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (authorId) REFERENCES authors(id) ON DELETE CASCADE
);

