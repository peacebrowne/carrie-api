
-- Enable the uuid-ossp extension for UUID generation
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create the authors table
CREATE TABLE IF NOT EXISTS authors (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username TEXT NOT NULL UNIQUE,
    email TEXT NOT NULL UNIQUE,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    dob DATE,
    gender TEXT,
    password_hash TEXT,
    password_salt TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create the articles table
CREATE TABLE IF NOT EXISTS articles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title TEXT NOT NULL,
    authorID UUID NOT NULL,
    description TEXT,
    content TEXT,
    published_at TIMESTAMP,
    is_published BOOLEAN DEFAULT false,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (authorID) REFERENCES authors(id) ON DELETE CASCADE
    CONSTRAINT unique_author_article UNIQUE (authorID, title)
);

-- Create the comments table
CREATE TABLE IF NOT EXISTS comments(
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    articleID UUID NOT NULL,
    content TEXT NOT NULL,
    FOREIGN KEY(articleID) REFERENCES articles(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tags (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name TEXT NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS article_tags (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    articleID UUID NOT NULL,
    tagID UUID NOT NULL,
    FOREIGN KEY(articleID) REFERENCES articles(id) ON DELETE CASCADE,
    FOREIGN KEY(tagID) REFERENCES tags(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS claps(
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    authorID UUID NOT NULL,
    articleID UUID NOT NULL,
    count INTEGER DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(authorID) REFERENCES authors(id) ON DELETE CASCADE,
    FOREIGN KEY(articleID) REFERENCES articles(id) ON DELETE CASCADE
)
