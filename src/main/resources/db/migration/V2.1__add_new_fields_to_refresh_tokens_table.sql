ALTER TABLE refresh_tokens
  ADD COLUMN device_id    VARCHAR(100),
  ADD COLUMN ip_address   VARCHAR(45),
  ADD COLUMN user_agent   VARCHAR(255),
  ADD COLUMN created_at   TIMESTAMP WITH TIME ZONE DEFAULT NOW();
