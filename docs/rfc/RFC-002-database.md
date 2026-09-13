# RFC-002 — Escolha do PostgreSQL no Amazon RDS

## Status

**Aceito**

## Contexto

A aplicação Oficina Dinoco trabalha com informações fortemente relacionais, incluindo clientes, veículos, ordens de serviço, funcionários, produtos, serviços e os itens vinculados às ordens de serviço.

Esses dados possuem relacionamentos bem definidos, regras de integridade e necessidade de consistência durante operações de criação, atualização e consulta.

## Problema

Era necessário escolher uma tecnologia de persistência capaz de:

- manter integridade entre entidades relacionadas;
- suportar relacionamentos entre clientes, veículos e ordens de serviço;
- garantir consistência transacional;
- permitir consultas relacionais;
- integrar-se facilmente com Spring Data JPA;
- permitir evolução controlada do esquema;
- operar de forma segura dentro da infraestrutura AWS.

Também era necessário decidir entre executar o banco manualmente ou utilizar um serviço gerenciado.

## Alternativas consideradas

### PostgreSQL no Amazon RDS

O PostgreSQL oferece modelo relacional, suporte a transações ACID (Atomicity, Consistency, Isolation, Durability), constraints, chaves estrangeiras e ampla compatibilidade com aplicações Java e Spring.

O Amazon RDS reduz a necessidade de administração direta da infraestrutura do banco e permite manter a instância protegida dentro da rede da aplicação.

### MySQL no Amazon RDS

Também atenderia aos principais requisitos relacionais da aplicação.

Entretanto, o PostgreSQL foi preferido pela robustez dos recursos relacionais, experiência já existente no projeto e boa integração com o stack adotado.

### Banco NoSQL

Uma alternativa seria utilizar um banco NoSQL, como Amazon DynamoDB.

Apesar de oferecer alta escalabilidade e baixa latência, o modelo de dados da aplicação possui múltiplos relacionamentos e regras de integridade que seriam menos naturais nesse tipo de persistência.

A adoção de NoSQL também exigiria remodelar as consultas e relacionamentos da aplicação.

### PostgreSQL executado em container ou EC2

Seria possível executar PostgreSQL em container ou diretamente em uma instância EC2.

Essa alternativa exigiria maior responsabilidade operacional, incluindo instalação, disponibilidade, atualização e administração da instância.

## Decisão proposta

Utilizar **PostgreSQL 16 executado no Amazon RDS** como banco de dados principal da solução.

O banco é utilizado pela aplicação Spring Boot e pela Lambda de autenticação para consultar e persistir informações de negócio.

A evolução do esquema é controlada por migrations com Flyway.

## Impactos

### Positivos

- modelo adequado para dados relacionais;
- suporte a transações ACID;
- integridade referencial por meio de chaves estrangeiras;
- compatibilidade com Spring Data JPA e Hibernate;
- suporte a migrations com Flyway;
- menor esforço operacional devido ao uso do RDS;
- isolamento de rede por meio da VPC e Security Groups.

### Negativos

- dependência de um banco relacional central;
- escalabilidade horizontal é menos simples do que em algumas soluções NoSQL;
- custo adicional em comparação com um banco executado localmente;
- necessidade de administrar migrations e evolução do modelo.

## Riscos

- criação de relacionamentos inadequados ou excessivamente acoplados;
- consultas mal otimizadas;
- indisponibilidade da aplicação caso o banco se torne indisponível;
- impacto em performance caso o modelo ou índices não sejam ajustados.

Esses riscos podem ser mitigados com modelagem adequada, constraints, índices, monitoramento e revisão das queries.

## Conclusão

O PostgreSQL no Amazon RDS foi escolhido por se adequar ao modelo de dados relacional da Oficina Dinoco e oferecer consistência, integridade e facilidade de integração com a aplicação Java.

A utilização do RDS reduz o esforço de administração da infraestrutura do banco e mantém a persistência integrada à arquitetura de nuvem da solução.
