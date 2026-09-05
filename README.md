# 🚗 API Oficina Mecânica Dinoco

Uma API RESTful desenvolvida para gerenciar o fluxo principal de uma oficina mecânica, contemplando o ciclo de vida completo de Ordens de Serviço (OS) + controle de estoque (Peças e Insumos) + controles administrativos (Clientes, Funcionários, Veículos, Serviços e Peças).

## 🎯 Objetivos Gerais do Projeto

* Gestão de ordens de serviço;
* Controle de estoque;
* Gestão administrativa de clientes, veículos, serviços, produtos e funcionários;
* Aplicação de Domain-Driven Design (DDD);
* Boas práticas de qualidade de software e segurança;
* Orçamento gerado automaticamente com base nos serviços e peças;
* Envio do orçamento ao cliente para aprovação via WhatsApp;
* Permitir consulta por parte do cliente via API para acompanhar o progresso;
* Listagem e detalhamento de ordens de serviço;
* Relatório para monitoramento do tempo médio de execução dos serviços dentro de uma OS.

## Features adicionadas
* Abertura de Ordem de Serviço (OS);
* Consulta de status da OS;
* Aprovação e reprovação de orçamento;
* Listagem de ordens de serviço;
* Atualização de status da OS;
* Autenticação de funcionários com e-mail/senha;
* Autenticação de clientes via CPF utilizando AWS Lambda;
* Autorização da consulta de rastreio por cliente autenticado;
* Integração com AWS API Gateway como ponto de entrada da solução.

## Documentações / Diagramas

*  **Componentes da aplicação:** [Visualizar](docs/diagramas-fase2/DiagramaComponentesArquitetura.png)
* ️ **Infraestrutura provisionada:** [Visualizar](docs/diagramas-fase2/InfraestruturaProvisionada.png)
* ️ **Fluxo de deploy:** [Visualizar](docs/diagramas-fase2/FluxoDeployCICD.png)

## Tecnologias Utilizadas

### Arquitetura de Software (Clean Architecture) 

Refatorado para seguir as diretrizes da **Clean Architecture**, visando o baixo acoplamento e a alta coesão, separando claramente as responsabilidades:

- **Domain Layer (Núcleo):** Contém as Entidades ricas e regras de negócio puras (agregados, *value objects*), totalmente independentes de bibliotecas externas ou frameworks.
- **Use Cases / Application Layer:** Orquestra as intenções do usuário (ex: `AprovarOrcamentoUseCase`, `FinalizarOrdemServicoUseCase`), ditando o fluxo da aplicação sem conhecer detalhes de banco de dados ou web.
- **Adapters / Presentation Layer:** Controladores REST e DTOs que servem como portas de entrada para a API, traduzindo o mundo externo (JSON) para os casos de uso.
- **Infrastructure Layer:** Onde residem os detalhes técnicos (Spring Boot, Spring Data JPA, Flyway, integrações com AWS, e Spring Security), implementando as interfaces exigidas pelos casos de uso.

### Backend
- **Java 21**
- **Spring Boot 4.0.6** 
- **Spring Web MVC & Spring Data JPA**
- **Spring Security & JWT (Auth0 Java JWT)**
- **Bean Validation & Lombok**

### Banco de Dados
- **PostgreSQL**
- **Flyway** (migrações e versionamento)
- **Seed inicial de dados no profile dev**

### Build, Qualidade e Testes
- **Maven**
- **JUnit 5, Mockito & Rest Assured**
- **Testcontainers** para testes de integração com banco PostgreSQL real.
- **Padrão AAA** (Arrange, Act, Assert)

### Cloud, DevOps e Infraestrutura como Código (IaC)

- **Docker & Docker Compose** para desenvolvimento local.
- **AWS EKS** para orquestração dos containers.
- **EC2** através dos Node Groups do EKS.
- **RDS PostgreSQL** como banco de dados gerenciado.
- **ECR** para armazenamento das imagens Docker.
- **S3** para armazenamento remoto dos estados Terraform.
- **AWS Secrets Manager** para credenciais do banco e chave JWT.
- **AWS Lambda** para autenticação serverless dos clientes via CPF.
- **AWS API Gateway** como ponto de entrada e roteamento da solução.
- **Terraform** para provisionamento da infraestrutura.
- **Kubernetes** com Deployment, Service, HPA e ConfigMap.
- **GitHub Actions** para CI/CD.

> O Docker Compose utiliza PostgreSQL local e não depende do RDS da AWS.

---

## Separação da Infraestrutura - TC Fase 03

A solução foi separada em repositórios independentes:

- **`oficina-dinoco`**  
  Aplicação principal Java/Spring Boot, regras de negócio, autenticação dos funcionários, Dockerfile, migrations Flyway e manifestos Kubernetes.

- **`oficina-infra-k8s`**  
  Provisionamento da VPC, Subnets, Internet Gateway, EKS, Node Groups EC2, ECR e API Gateway.

- **`oficina-infra-db`**  
  Provisionamento do RDS PostgreSQL, DB Subnet Group, Security Group e integração com AWS Secrets Manager.

- **`oficina-auth-lambda`**  
  Function Serverless responsável pela autenticação dos clientes via CPF, consulta do cliente na base, verificação de status e emissão do JWT.

A separação permite ciclos de deploy independentes para infraestrutura, banco, autenticação serverless e aplicação.

---

## 📚 Documentação e Testes da API

* [Linguagem Ubíqua e Dicionário de Dados](./docs/negocio/linguagem_ubiqua.md)
* [Swagger UI - Oficina Dinoco](http://localhost:8080/swagger-ui/index.html) *(Disponível após subir a aplicação)*

### 🚀 Insomnia Collection

Para facilitar os testes e a avaliação do fluxo completo da aplicação, disponibilizamos uma collection do Insomnia com todas as requisições organizadas na ordem lógica de execução:
Autenticação, Abrir OS, Iniciar diagnóstico, Adicionar Itens de Produto na OS, Adicionar Itens de Serviço na OS, Concluir Diagnóstico, Enviar Orçamento, Aprovar Orçamento, Verificar Estoque (Condicional), Iniciar Execução da OS, Iniciar Execução dos Itens de Serviços, Concluir Execução dos Itens de Serviços, Finalizar execução da OS, e por fim, Concluir a OS. 

1. Baixe o arquivo da collection: [`oficina-dinoco-insomnia-collection.yaml`](./docs/api/oficina-dinoco-insomnia-collection.yaml)
2. No Insomnia, vá em **Import/Export** > **Import Data** > **From File** e selecione o arquivo baixado.
3. As requisições já estão na ordem ideal para testar o ciclo de vida completo da API.

---

## 🔐 Autenticação de Funcionários

Funcionários continuam utilizando o fluxo já existente:

```text
Funcionário
   ↓
e-mail + senha
   ↓
Spring Boot
   ↓
validação no banco
   ↓
JWT funcionário
```

A API utiliza Spring Security e JWT para proteger as rotas administrativas.

Para facilitar testes locais, o seed cria um usuário administrador padrão:

* **Usuário:** `admin`
* **Senha:** `123456`

---

## 🔐 Autenticação de Clientes via CPF

A Fase 3 adicionou um segundo fluxo de autenticação, destinado exclusivamente aos **clientes da oficina**.

```text
Cliente
   ↓
CPF
   ↓
API Gateway
   ↓
AWS Lambda
   ↓
validação do CPF
   ↓
consulta no PostgreSQL
   ↓
verificação de existência e status
   ↓
JWT CLIENTE
```

O JWT do cliente possui estrutura semelhante a:

```json
{
  "iss": "oficina-api",
  "sub": "11",
  "tipo": "CLIENTE",
  "iat": 1788141203,
  "exp": 1788148403
}
```

O campo `sub` contém o **ID interno do cliente**. O CPF não é colocado no token.

Esse desenho permite que a aplicação identifique o cliente autenticado utilizando apenas o `clienteId` confiável presente no JWT.

---

## 🔒 Rotas Utilizadas por cliente protegidas por JWT

Um dos requisitos da Fase 3 é proteger as rotas utilizadas pelo cliente. 

A rota:

```http
GET /api/ordens-servico/rastreio/{codigoRastreio}
```

antes era pública. Agora exige um JWT válido do tipo `CLIENTE`:

```http
Authorization: Bearer <JWT_CLIENTE>
```

### Fluxo de autorização

```text
JWT CLIENTE
sub = clienteId
tipo = CLIENTE
        ↓
Spring Security
        ↓
ClientePrincipal
        ↓
Controller Web
        ↓
BuscarOSPorCodigoRastreioQuery
codigoRastreio + clienteId
        ↓
Use Case
        ↓
OrdemServicoQueryGateway
        ↓
PostgreSQL
```

A consulta considera simultaneamente o código de rastreio e o cliente autenticado:

Assim, possuir um JWT válido não é suficiente: o cliente precisa ser o proprietário da OS consultada.

### Comportamentos esperados

```text
Cliente dono da OS
→ 200 OK

Cliente autenticado tentando consultar OS de outro cliente
→ 404 Not Found

Funcionário tentando utilizar a rota exclusiva de cliente
→ 403 Forbidden

Requisição sem JWT válido
→ acesso negado
```

Essa estratégia implementa **autorização por propriedade do recurso**, evitando que um cliente consulte informações pertencentes a outro cliente.

Conceitualmente:

```text
Autenticação
→ Quem é o cliente?

Autorização
→ Este cliente pode acessar esta OS?
```

O CPF é utilizado apenas para o fluxo inicial de autenticação serverless. Após a emissão do token, a aplicação utiliza o `clienteId` presente no JWT.

---
Criado também uma rota nova para aprovação/recusa de orçamento pelo cliente autenticado:

```http
POST /api/ordens-servico/rastreio/{codigoRastreio}/decisao
```

Exige um JWT válido do tipo `CLIENTE`:

```http
Authorization: Bearer <JWT_CLIENTE>
```
---

## Rotas que simulam um WebHook, não exigirão autenticação, mas são protegidas com header específico 

```http
POST /api/ordens-servico/webhooks/orcamentos/{codigoRastreio}
POST /api/ordens-servico/webhooks/status/{codigoRastreio}                        
```

---


## 🚀 Como Executar Local (Desenvolvimento)

### Pré-requisitos
Antes de começar, você precisará ter instalado em sua máquina:
* [JDK 21](https://www.oracle.com/br/java/technologies/downloads/#java21)
* [Docker](https://www.docker.com/)

⚠️ **Nota:** Você **não** precisa instalar o Maven. O projeto já inclui o Maven Wrapper (`mvnw`), que fará o download da versão correta de forma automática e isolada durante a primeira execução.

### Passo a Passo

**1. Clone o repositório:**
```bash
git clone git@github.com:JacksonPriess/oficina-dinoco.git
cd oficina-dinoco
```
------------------------------------------------------------------------
**2. Subir ambiente com (API + Banco)**
* Inicie todo o ecossistema utilizando o Docker Compose:
``` bash
docker compose up --build -d
```
------------------------------------------------------------------------

A API estará disponível em: http://localhost:8080

---

# Arquitetura Cloud e CI/CD - TC Fase 03

## API Gateway como ponto de entrada

O AWS API Gateway recebe as requisições e direciona o fluxo conforme a rota:

```text
                         Internet
                            |
                            v
                      API Gateway
                      /         \
                     /           \
        POST /auth/cliente      Demais rotas
                |                    |
                v                    v
          Auth Lambda          Load Balancer
                                    |
                                    v
                                   EKS
                                    |
                                    v
                               Spring Boot
```

Exemplo:

```text
POST /auth/cliente
→ Lambda de autenticação

POST /api/auth/login
→ Spring Boot

GET /api/ordens-servico/rastreio/{codigo}
→ Spring Boot
```

A Lambda é acionada apenas quando a rota exige o fluxo serverless de autenticação do cliente.

---

## Fluxo de Provisionamento

Após iniciar ou resetar o AWS LAB, a ordem recomendada é:

1. `oficina-infra-k8s` — VPC, Subnets, EKS e ECR.
2. `oficina-infra-db` — PostgreSQL RDS e Secrets Manager.
3. `oficina-auth-lambda` — Lambda, integração com VPC e autenticação do cliente.
4. `oficina-dinoco` — build, ECR e deploy no EKS.
5. Workflow do **API Gateway** no `oficina-infra-k8s`.

O API Gateway é aplicado após a aplicação porque sua integração HTTP utiliza o Load Balancer criado pelo Service Kubernetes.

---

## Fluxo da Aplicação

A pipeline do `oficina-dinoco` é responsável por:

1. Executar build e testes automatizados com Maven.
2. Criar a imagem Docker.
3. Publicar a imagem no Amazon ECR usando o SHA do commit.
4. Configurar acesso ao EKS.
5. Consultar o endpoint do RDS.
6. Consultar as credenciais do banco no AWS Secrets Manager.
7. Consultar a chave JWT compartilhada com a Auth Lambda.
8. Criar/atualizar o Kubernetes Secret com credenciais e `JWT_SECRET`.
9. Aplicar os manifestos Kubernetes via `kubectl`.
10. Aguardar o rollout do Deployment.

---

### Gestão do Kubernetes

Os manifestos Kubernetes permanecem no repositório da aplicação:

- **Deployment (`deployment.yaml`)**  
  Define a execução da aplicação, imagem Docker, recursos de CPU e memória.

- **Service (`service.yaml`)**  
  Expõe temporariamente a aplicação através de um Load Balancer AWS.

- **ConfigMap (`configmap.yaml`)**  
  Armazena configurações não sensíveis, como profile Spring e endereço do banco.

- **Kubernetes Secret**  
  Não é versionado no Git. É criado dinamicamente durante o deploy utilizando as credenciais obtidas do AWS Secrets Manager.

- **HPA (`hpa.yaml`)**  
  Escala automaticamente a aplicação entre 1 e 3 réplicas quando a utilização média de CPU ultrapassa 70%.