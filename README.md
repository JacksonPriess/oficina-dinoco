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
* Abertura de Ordem de Serviço (OS): receber os dados do cliente, veículo, serviços e peças;
* Consulta de status da OS;
* Aprovação de orçamento - Externo;
* Listagem de ordens de serviço;
* Atualização de status da OS - Externo;

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
- **Testcontainers** (Testes de integração com PostgreSQL real)
- **Padrão AAA** (Arrange, Act, Assert)

### Cloud, DevOps e Infraestrutura como Código (IaC) 
- **Docker & Docker Compose** para execução do ambiente local.
> O Docker Compose utiliza um PostgreSQL local e não depende do RDS da AWS.
- **AWS (Amazon Web Services):**
  - **EKS** para orquestração dos containers.
  - **EC2** através dos Node Groups do EKS.
  - **RDS PostgreSQL** como banco de dados gerenciado.
  - **ECR** para armazenamento das imagens Docker.
  - **S3** para armazenamento remoto dos estados do Terraform.
  - **AWS Secrets Manager** para gerenciamento seguro das credenciais do banco.
- **Terraform** para provisionamento da infraestrutura AWS.
- **Kubernetes** com Deployment, Service, HPA e ConfigMap.
- **GitHub Actions** para CI/CD, build da aplicação, publicação da imagem no ECR e deploy no EKS.

### Separação da Infraestrutura - TC Fase 03

A infraestrutura da solução foi separada em repositórios independentes, cada um com sua própria pipeline de CI/CD:

- **`oficina-dinoco`**  
  Aplicação principal Java/Spring Boot, Dockerfile, migrations Flyway e manifestos Kubernetes.

- **`oficina-infra-k8s`**  
  Provisionamento via Terraform da VPC, Subnets, Internet Gateway, EKS, Node Groups EC2 e ECR.

- **`oficina-infra-db`**  
  Provisionamento via Terraform do RDS PostgreSQL, DB Subnet Group, Security Group e integração com AWS Secrets Manager.

A separação permite que infraestrutura, banco de dados e aplicação tenham ciclos de deploy independentes.
---

## 📚 Documentação e Testes da API

* [Linguagem Ubíqua e Dicionário de Dados](./docs/negocio/linguagem_ubiqua.md)
* [Swagger UI - Oficina Dinoco](http://localhost:8080/swagger-ui/index.html) *(Disponível após subir a aplicação)*

### 🚀 Insomnia Collection (Recomendado para Avaliação)
Para facilitar os testes e a avaliação do fluxo completo da aplicação, disponibilizamos uma collection do Insomnia com todas as requisições organizadas na ordem lógica de execução:
Autenticação, Abrir OS, Iniciar diagnóstico, Adicionar Itens de Produto na OS, Adicionar Itens de Serviço na OS, Concluir Diagnóstico, Enviar Orçamento, Aprovar Orçamento, Verificar Estoque (Condicional), Iniciar Execução da OS, Iniciar Execução dos Itens de Serviços, Concluir Execução dos Itens de Serviços, Finalizar execução da OS, e por fim, Concluir a OS. 

1. Baixe o arquivo da collection: [`oficina-dinoco-insomnia-collection.yaml`](./docs/api/oficina-dinoco-insomnia-collection.yaml)
2. No Insomnia, vá em **Import/Export** > **Import Data** > **From File** e selecione o arquivo baixado.
3. As requisições já estão na ordem ideal para testar o ciclo de vida completo da API.

---

### 🔐 Autenticação (Acesso Inicial)

A API é protegida por JWT (Spring Security). Para facilitar os testes, ao subir a aplicação, o **Flyway** executa um script de *seed* que já cria um usuário administrador padrão (com a senha devidamente criptografada em BCrypt).

Para gerar seu token de acesso no endpoint de Login, utilize as seguintes credenciais:

* **Usuário:** `admin`
* **Senha:** `123456`

> 💡 **Dica:** Na collection do Insomnia disponibilizada acima, a primeira requisição (Login) já está salva com esse payload. Basta executá-la para se autenticar! E o token será reaproveitado nas demais requisições.

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

## Arquitetura Cloud e CI/CD - TC Fase 03

A solução utiliza repositórios separados para infraestrutura e aplicação, permitindo maior isolamento de responsabilidades e pipelines independentes.

### Fluxo de Provisionamento

A infraestrutura deve ser criada na seguinte ordem:

1. `oficina-infra-k8s`
2. `oficina-infra-db`
3. `oficina-dinoco`

O repositório `oficina-infra-k8s` cria a infraestrutura base da AWS, incluindo VPC, Subnets, EKS e ECR.

O repositório `oficina-infra-db` utiliza o Remote State do Terraform para obter informações da VPC e Subnets e provisionar o RDS PostgreSQL na mesma rede.

A senha do usuário master do banco é gerenciada automaticamente pelo AWS Secrets Manager.

### Fluxo da Aplicação

A pipeline do `oficina-dinoco` é responsável por:

1. Executar build e testes automatizados com Maven.
2. Criar a imagem Docker da aplicação.
3. Publicar a imagem no Amazon ECR utilizando o SHA do commit como versão.
4. Configurar o acesso ao cluster EKS.
5. Consultar o endpoint do RDS.
6. Consultar as credenciais do banco no AWS Secrets Manager.
7. Criar ou atualizar o Kubernetes Secret.
8. Aplicar os manifestos Kubernetes através do `kubectl`.
9. Aguardar o rollout do Deployment.

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