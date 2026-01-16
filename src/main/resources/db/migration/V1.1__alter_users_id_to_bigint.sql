-- Flyway Migration: Alter users.id to bigint
ALTER TABLE users ALTER COLUMN id TYPE bigint;