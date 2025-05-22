CREATE TABLE IF NOT EXISTS usuarios (
                                        id SERIAL PRIMARY KEY,
                                        username VARCHAR(50) UNIQUE NOT NULL,
                                        email VARCHAR(100) UNIQUE NOT NULL,
                                        password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS roles (
                                     id SERIAL PRIMARY KEY,
                                     role VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS usuario_roles (
                                             usuario_id INTEGER NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
                                             role_id INTEGER NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
                                             PRIMARY KEY (usuario_id, role_id)
);

INSERT INTO usuarios (id, username, email, password) VALUES
                                                         (1, 'rubi', 'rubi@gmail.com', 'rubi'),
                                                         (2, 'igna', 'igna@gmail.com', 'igna'),
                                                         (3, 'oska', 'oska@gmail.com', 'oska'),
                                                         (4, 'inma', 'inma@gmail.com', 'inma');

INSERT INTO roles (id, role) VALUES
                                 (1, 'ADMIN'),
                                 (2, 'MODERATOR'),
                                 (3, 'USER');

INSERT INTO usuario_roles (usuario_id, role_id) VALUES
                                                    (1, 1),
                                                    (2, 2),
                                                    (3, 3),
                                                    (4, 3);
