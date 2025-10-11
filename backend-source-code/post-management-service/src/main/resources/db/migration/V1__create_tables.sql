-- ==============================================
-- Create UUID-OSSP extension if not exists
-- ==============================================
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ==============================================
-- CREATE POST_MANAGEMENT SCHEMA IF NOT EXISTS
-- ==============================================
CREATE SCHEMA IF NOT EXISTS POST_MANAGEMENT;

-- ==============================================
-- CREATE FOLLOW_MANAGEMENT SCHEMA IF NOT EXISTS
-- ==============================================
CREATE SCHEMA IF NOT EXISTS FOLLOW_MANAGEMENT;

-- ==============================================
-- CREATE POST TABLE
-- ==============================================
CREATE TABLE IF NOT EXISTS POST_MANAGEMENT.POST(
    ID VARCHAR(255) PRIMARY KEY,
    USER_ID VARCHAR(255) NOT NULL,
    HAS_MEDIA BOOLEAN NOT NULL,
    CONTENT TEXT,
    CREATED_AT TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UPDATED_AT TIMESTAMP
);

-- ==============================================
-- CREATE REACT TABLE
-- ==============================================
CREATE TABLE IF NOT EXISTS POST_MANAGEMENT.REACT(
    ID VARCHAR(255) PRIMARY KEY,
    USER_ID VARCHAR(255) NOT NULL,
    POST_ID VARCHAR(255) NOT NULL,
    TYPE VARCHAR(255) NOT NULL,
    CONSTRAINT FK_REACT_POST
      FOREIGN KEY (POST_ID) REFERENCES POST_MANAGEMENT.POST(ID)
);

-- ==============================================
-- CREATE COMMENT TABLE
-- ==============================================
CREATE TABLE IF NOT EXISTS POST_MANAGEMENT.COMMENT(
    ID VARCHAR(255) PRIMARY KEY,
    USER_ID VARCHAR(255) NOT NULL,
    POST_ID VARCHAR(255) NOT NULL,
    HAS_MEDIA BOOLEAN NOT NULL,
    CONTENT TEXT,
    PARENT_COMMENT_ID VARCHAR(255),
    CREATED_AT TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UPDATED_AT TIMESTAMP,
    CONSTRAINT FK_COMMENT_POST
        FOREIGN KEY (POST_ID) REFERENCES POST_MANAGEMENT.POST(ID),
    CONSTRAINT FK_COMMENT_COMMENT
        FOREIGN KEY (PARENT_COMMENT_ID) REFERENCES POST_MANAGEMENT.COMMENT(ID)
);

-- ==============================================
-- CREATE FOLLOW TABLE
-- ==============================================
CREATE TABLE IF NOT EXISTS FOLLOW_MANAGEMENT.FOLLOWING(
    USER_ID VARCHAR(255) NOT NULL UNIQUE,
    FOLLOWED_USER_IDs VARCHAR(255)[] NOT NULL
);