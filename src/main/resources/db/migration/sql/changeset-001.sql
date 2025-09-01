CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS cards (
    id BIGSERIAL PRIMARY KEY,
    number VARCHAR(19) UNIQUE NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE                           ,
    expiry_date DATE NOT NULL,
    balance NUMERIC(10,2) NOT NULL,
    status_card VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    role_name VARCHAR(19) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS user_roles(
    role_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, user_id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
    ON UPDATE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON UPDATE CASCADE
)

