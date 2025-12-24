CREATE TABLE IF NOT EXISTS users
(
  id               SERIAL PRIMARY KEY,
  username         VARCHAR(50)  NOT NULL UNIQUE,
  password         VARCHAR(255) NOT NULL,
  email            VARCHAR(100) NOT NULL UNIQUE,
  first_name       VARCHAR(100),
  last_name        VARCHAR(100),
  date_of_birth    DATE,
  phone_number     VARCHAR(20),
  address_street   VARCHAR(200),
  address_city     VARCHAR(100),
  address_state    VARCHAR(100),
  address_country  VARCHAR(100),
  address_zip_code VARCHAR(20),
  role             VARCHAR(50)  NOT NULL DEFAULT 'USER',
  enabled          BOOLEAN      NOT NULL DEFAULT TRUE,
  last_login_at    TIMESTAMP,
  created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE OR REPLACE FUNCTION update_updated_at_column()
  RETURNS TRIGGER AS
$$
BEGIN
  NEW.updated_at = CURRENT_TIMESTAMP;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_users_updated_at
  BEFORE UPDATE
  ON users
  FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();
