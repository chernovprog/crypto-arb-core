-- Migration to drop refresh_tokens table after moving to Redis
DROP TABLE IF EXISTS refresh_tokens CASCADE;