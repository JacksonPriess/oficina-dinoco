CREATE TABLE usuario (
     id BIGSERIAL PRIMARY KEY,
     login VARCHAR(100) NOT NULL UNIQUE,
     senha VARCHAR(255) NOT NULL,
     ativo BOOLEAN NOT NULL DEFAULT TRUE,
     precisa_trocar_senha BOOLEAN NOT NULL DEFAULT TRUE,
     perfil VARCHAR(50) NOT NULL
);

-- Inserindo o usuário 'admin' com a senha '123456' criptografada em BCrypt
INSERT INTO usuario (login, senha, precisa_trocar_senha, perfil)
VALUES ('admin', '$2a$10$Y50UaMFOxteibQEYLrwuHeehHYfcoafCopUazP12.rqB41bsolF5.', FALSE, 'ADMIN');