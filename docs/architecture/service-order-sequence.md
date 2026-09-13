# Diagrama de Sequência — Abertura de Ordem de Serviço

## Objetivo

Este diagrama representa o fluxo de abertura de uma Ordem de Serviço na aplicação, considerando as duas formas disponíveis de criação:

- abertura simples, sem itens de produtos ou serviços;
- abertura completa, já contendo itens de serviços e produtos.

O diagrama também evidencia a participação da API Gateway, da aplicação Spring Boot, da camada de domínio, do banco PostgreSQL e da observabilidade com New Relic.

## Fluxo de abertura da Ordem de Serviço

```mermaid
sequenceDiagram
    autonumber

    actor Atendente
    participant APIGW as AWS API Gateway
    participant API as Spring Boot API
    participant Domain as Domínio / Caso de Uso
    participant DB as RDS PostgreSQL
    participant NR as New Relic

    Atendente->>APIGW: Requisição para abertura da OS
    APIGW->>API: Encaminha requisição
    API->>Domain: Solicita criação da Ordem de Serviço

    Domain->>DB: Valida cliente e veículo
    DB-->>Domain: Dados encontrados

    alt Abertura simples
        Domain->>DB: Persiste Ordem de Serviço
        DB-->>Domain: OS criada
    else Abertura com itens
        Domain->>DB: Persiste Ordem de Serviço
        Domain->>DB: Persiste itens de serviços
        Domain->>DB: Persiste itens de produtos
        DB-->>Domain: OS e itens criados
    end

    Domain-->>API: Ordem de Serviço criada
    API->>NR: Registra logs, traces e métricas
    API-->>APIGW: HTTP 201
    APIGW-->>Atendente: Ordem de Serviço criada
```

## Descrição

O processo de abertura da Ordem de Serviço é iniciado pelo atendente por meio da API pública exposta pelo Amazon API Gateway. A requisição é encaminhada para a aplicação Spring Boot, que direciona a operação para a camada de domínio responsável pelo caso de uso de criação da OS.

Antes da persistência, a aplicação valida as informações necessárias, como cliente e veículo. A partir daí, existem duas variações do fluxo:

- **Abertura simples:** a Ordem de Serviço é criada inicialmente sem itens de produtos ou serviços.
- **Abertura com itens:** a Ordem de Serviço é criada juntamente com os itens de serviços e produtos informados na requisição.

Em ambos os casos, os dados são persistidos no PostgreSQL executado no Amazon RDS. Durante o processamento, a aplicação gera logs estruturados, traces e métricas que são enviados ao New Relic para fins de monitoramento e observabilidade.

Após a criação bem-sucedida, a aplicação retorna uma resposta HTTP de sucesso ao atendente por meio do API Gateway.
