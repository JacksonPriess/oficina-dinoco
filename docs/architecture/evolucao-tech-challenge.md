# Evolução da arquitetura — Tech Challenge

## Fase 03 — separação da infraestrutura

A solução foi separada em repositórios independentes:

- **`oficina-dinoco`**  
  Aplicação principal Java/Spring Boot, regras de negócio, autenticação dos funcionários, Dockerfile, migrations Flyway e manifestos Kubernetes.

- **`oficina-infra-k8s`**  
  Provisionamento da VPC, Subnets, Internet Gateway, EKS, Node Groups EC2, ECR e API Gateway.

- **`oficina-infra-db`**  
  Provisionamento do RDS PostgreSQL, DB Subnet Group, Security Group e integração com AWS Secrets Manager.

- **`oficina-auth-lambda`**  
  Function Serverless responsável pela autenticação dos clientes via CPF, consulta do cliente na base, verificação de status e emissão do JWT.

A separação permite ciclos de deploy independentes para infraestrutura, banco, autenticação serverless e aplicação. O API Gateway encaminha `POST /auth/cliente` para a Lambda e as rotas do núcleo para o Load Balancer da API no EKS.

---

## Fase 04 — evolução para microserviços

Na Fase 04, os catálogos de produtos e serviços e o financeiro passam a ter APIs e dados próprios. 
O núcleo mantém as ordens de serviço, estoque e a orquestração da Saga de pagamento. 
