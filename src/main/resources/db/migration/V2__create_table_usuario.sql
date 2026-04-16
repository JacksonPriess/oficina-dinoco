CREATE TABLE usuario (
     id BIGSERIAL PRIMARY KEY,
     login VARCHAR(100) NOT NULL UNIQUE,
     senha VARCHAR(255) NOT NULL,
     ativo BOOLEAN NOT NULL DEFAULT TRUE
);

-- Inserindo o usuário 'admin' com a senha '123456' criptografada em BCrypt
INSERT INTO usuario (login, senha)
VALUES ('admin', '$2a$10$Y50UaMFOxteibQEYLrwuHeehHYfcoafCopUazP12.rqB41bsolF5.');