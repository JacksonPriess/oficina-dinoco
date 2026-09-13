# ADR-004 — Uso de JWT Stateless para Autenticação e Autorização

## Status
**Aceito**

## Contexto
A solução precisa autenticar diferentes perfis e identificar o consumidor nas requisições subsequentes.

Para clientes, a autenticação ocorre por CPF através de AWS Lambda. Para funcionários, a autenticação é realizada pela aplicação principal.

Era necessário um mecanismo comum sem sessão compartilhada entre os componentes.

## Decisão
Utilizar **JSON Web Token (JWT)** como mecanismo stateless de autenticação e autorização.

O token é enviado no header:

`Authorization: Bearer <token>`

Para clientes:
- `sub` representa o identificador do cliente;
- a claim `tipo` identifica o perfil como `CLIENTE`.

A aplicação Spring Boot valida assinatura, expiração e claims utilizando Spring Security.

## Consequências

### Positivas
- ausência de sessão compartilhada;
- integração simples entre Lambda e Spring Boot;
- escalabilidade horizontal facilitada;
- transporte da identidade e permissões no token;
- possibilidade de autorização por recurso.

### Negativas
- tokens permanecem válidos até expirar;
- o segredo de assinatura precisa ser protegido;
- alterações nas claims exigem compatibilidade entre emissores e consumidores.

## Observações
A aplicação utiliza a identidade presente no JWT para validar acesso aos recursos, evitando confiar em identificadores de cliente enviados livremente pela requisição.
