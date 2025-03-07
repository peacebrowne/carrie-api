
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
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create the articles table
CREATE TABLE IF NOT EXISTS articles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title TEXT NOT NULL,
    authorID UUID NOT NULL,
    description TEXT,
    content TEXT,
    published_at TIMESTAMP,
    status TEXT NOT NULL DEFAULT 'draft',
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (authorID) REFERENCES authors(id) ON DELETE CASCADE,
    CONSTRAINT unique_author_article UNIQUE (authorID, title)
);

-- Create the comments table
CREATE TABLE IF NOT EXISTS comments(
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    articleID UUID,
    authorID UUID NOT NULL,
    parentCommentID UUID,
    content TEXT NOT NULL,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(articleID) REFERENCES articles(id) ON DELETE CASCADE,
    FOREIGN KEY(authorID) REFERENCES authors(id) ON DELETE CASCADE,
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
    articleID UUID,
    commentID UUID,
    likes INTEGER DEFAULT 1,
    dislikes INTEGER,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(authorID) REFERENCES authors(id) ON DELETE CASCADE,
    FOREIGN KEY(articleID) REFERENCES articles(id) ON DELETE CASCADE,
    FOREIGN KEY(commentID) REFERENCES comments(id) ON DELETE CASCADE,
    UNIQUE(authorID,articleID),
    UNIQUE(authorID,commentID)
);

CREATE TABLE IF NOT EXISTS images(
	id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name TEXT,
    type TEXT,
    data BYTEA,
    articleID UUID UNIQUE,
    authorID UUID UNIQUE,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(articleID) REFERENCES articles(id) ON DELETE CASCADE,
    FOREIGN KEY(authorID) REFERENCES authors(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS author_follower (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  follower_author_id UUID NOT NULL,
  followed_author_id UUID NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (follower_author_id) REFERENCES authors(id) ON DELETE CASCADE,
  FOREIGN KEY (followed_author_id) REFERENCES authors(id) ON DELETE CASCADE,
  UNIQUE(follower_author_id, followed_author_id)
);