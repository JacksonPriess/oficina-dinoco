# Diagrama de Sequência — Autenticação do Cliente

## Objetivo

Este diagrama representa o fluxo de autenticação do cliente por CPF e o uso posterior do JWT nas rotas protegidas da aplicação.

## Fluxo de autenticação

```mermaid
sequenceDiagram
    autonumber

    actor Cliente
    participant APIGW as AWS API Gateway
    participant Lambda as Lambda Auth CPF
    participant Secrets as AWS Secrets Manager
    participant DB as RDS PostgreSQL
    participant API as Spring Boot API
    participant Security as Spring Security / JWT

    Cliente->>APIGW: POST /auth/cliente<br/>{ cpf }

    APIGW->>Lambda: Encaminha requisição

    Lambda->>Secrets: Obtém credenciais e segredo JWT
    Secrets-->>Lambda: Credenciais / JWT Secret

    Lambda->>DB: Busca cliente pelo CPF
    DB-->>Lambda: Dados do cliente

    alt CPF inválido, cliente inexistente ou inativo
        Lambda-->>APIGW: Erro de autenticação
        APIGW-->>Cliente: HTTP 401 / erro
    else Cliente válido
        Lambda->>Lambda: Gera JWT<br/>sub = clienteId<br/>tipo = CLIENTE
        Lambda-->>APIGW: JWT
        APIGW-->>Cliente: HTTP 200 + token
    end

    Note over Cliente,Security: Utilização do JWT em uma rota protegida

    Cliente->>APIGW: Requisição API<br/>Authorization: Bearer JWT
    APIGW->>API: Encaminha via Load Balancer
    API->>Security: Valida assinatura e claims do JWT

    alt Token inválido ou expirado
        Security-->>Cliente: HTTP 401
    else Token válido
        Security->>API: clienteId + ROLE_CLIENTE
        API->>DB: Consulta recurso autorizado ao cliente
        DB-->>API: Dados
        API-->>Cliente: HTTP 200 + resposta
    end
```

## Descrição

O cliente realiza a autenticação através do CPF pelo Amazon API Gateway. A requisição é direcionada a uma AWS Lambda responsável por validar o CPF, consultar o cliente no banco PostgreSQL e, quando autorizado, emitir um JWT contendo a identificação do cliente.

Nas requisições subsequentes, o token é enviado à API Spring Boot, que valida sua assinatura e utiliza a identificação contida no token para realizar a autorização de acesso aos recursos.

No JWT:

- `sub` contém o identificador do cliente.
- `tipo` contém o valor `CLIENTE`.

Dessa forma, a aplicação utiliza a identidade presente no token para validar o acesso aos recursos do cliente, evitando confiar em um `clienteId` informado livremente pela requisição.
