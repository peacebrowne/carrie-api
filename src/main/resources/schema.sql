
-- Enable the uuid-ossp extension for UUID generation
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create the authors table
CREATE TABLE IF NOT EXISTS authors (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username TEXT NOT NULL UNIQUE,
    email TEXT NOT NULL UNIQUE,
    firstName TEXT NOT NULL,
    lastName TEXT,
    dob DATE,
    gender TEXT,
    password_hash TEXT,
    password_salt TEXT,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
);

-- Create the articles table
CREATE TABLE IF NOT EXISTS articles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title TEXT NOT NULL,
    authorID UUID NOT NULL,
    description TEXT,
    content TEXT,
    published_at TIMESTAMP,
    isPublished BOOLEAN DEFAULT false,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (authorID) REFERENCES authors(id) ON DELETE CASCADE
    CONSTRAINT unique_author_article UNIQUE (authorID, title)
);

-- Create the comments table
CREATE TABLE IF NOT EXISTS comments(
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    articleID UUID NOT NULL,
    authorID UUID NOT NULL,
    parentCommentID UUID;
    content TEXT NOT NULL,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(articleID) REFERENCES articles(id) ON DELETE CASCADE
    FOREIGN KEY(authorID) REFERENCES authorID(id) ON DELETE CASCADE
    FOREIGN KEY(parentCommentID) REFERENCES comments(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tags (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name TEXT NOT NULL UNIQUE,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
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
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(authorID) REFERENCES authors(id) ON DELETE CASCADE,
    FOREIGN KEY(articleID) REFERENCES articles(id) ON DELETE CASCADE
)
