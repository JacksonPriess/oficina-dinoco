# Justificativa da Escolha do Banco de Dados

## Contexto

A aplicação Oficina Dinoco possui dados fortemente relacionais, como clientes, veículos, ordens de serviço, produtos, serviços e funcionários.

Essas entidades possuem vínculos bem definidos entre si, o que torna um banco relacional uma escolha adequada para o domínio.

## Escolha do PostgreSQL

O PostgreSQL foi escolhido por três motivos principais:

- atende bem ao modelo relacional da aplicação;
- possui boa integração com Java, Spring Data JPA, Hibernate e Flyway;
- já era uma tecnologia conhecida durante o desenvolvimento, reduzindo curva de aprendizado e risco de implementação.

Outros bancos relacionais, como MySQL, também poderiam atender ao projeto. Portanto, a escolha do PostgreSQL não foi por exclusividade técnica, mas por adequação ao contexto e ao stack utilizado.

## Escolha do Amazon RDS

O Amazon RDS foi utilizado para executar o PostgreSQL de forma gerenciada na AWS.

Com isso, evita-se a necessidade de administrar manualmente a instalação e a infraestrutura do banco em uma EC2 ou container.

Além disso, o RDS integra-se à VPC, Security Groups e ao provisionamento com Terraform já utilizados no projeto.

## Modelo Relacional

O modelo relacional permite representar de forma clara os principais relacionamentos do domínio, como:

- cliente e veículo;
- cliente e ordem de serviço;
- veículo e ordem de serviço;
- ordem de serviço e seus produtos;
- ordem de serviço e seus serviços.

Também permite utilizar chaves estrangeiras e constraints para manter a integridade dos dados.

## Evolução do Banco

As alterações no esquema são versionadas com Flyway, permitindo que a estrutura do banco evolua de forma controlada junto com a aplicação.

## Conclusão

A combinação PostgreSQL + Amazon RDS foi escolhida por atender bem ao modelo relacional da Oficina Dinoco, integrar-se ao stack Java/Spring e reduzir complexidade operacional.

Outras tecnologias poderiam ser utilizadas, mas essa combinação apresentou um equilíbrio adequado entre simplicidade, conhecimento prévio, consistência dos dados e integração com a arquitetura AWS adotada.
