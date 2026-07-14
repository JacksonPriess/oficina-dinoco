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

## Features adicionadas - TC Fase 02
* Abertura de Ordem de Serviço (OS): receber os dados do cliente, veículo, serviços e peças;
* Consulta de status da OS;
* Aprovação de orçamento - Externo;
* Listagem de ordens de serviço;
* Atualização de status da OS - Externo;

## Documentações / Diagramas - TC Fase 02

*  **Componentes da aplicação:** [Visualizar](docs/diagramas-fase2/DiagramaComponentesArquitetura.png)
* ️ **Infraestrutura provisionada:** [Visualizar](docs/diagramas-fase2/InfraestruturaProvisionada.png)
* ️ **Fluxo de deploy:** [Visualizar](docs/diagramas-fase2/FluxoDeployCICD.png)

## Tecnologias Utilizadas

### Arquitetura de Software (Clean Architecture) - TC Fase 02

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

### Cloud, DevOps e Infraestrutura como Código (IaC) - TC Fase 02
- **Docker & Docker Compose** (Ambiente de desenvolvimento local)
- **AWS (Amazon Web Services):**
    - **EKS** (Elastic Kubernetes Service) para orquestração de containers.
    - **RDS** (Relational Database Service) para banco de dados PostgreSQL gerenciado.
    - **ECR** (Elastic Container Registry) para armazenamento de imagens Docker.
    - **S3** para armazenamento seguro do estado do Terraform.
- **Terraform:** Provisionamento 100% automatizado da infraestrutura (VPC, Subnets, Security Groups, RDS, EKS).
- **Kubernetes:** Manifestos declarativos (Deployment, Service, HPA, ConfigMap, Secret).
- **GitHub Actions:** Esteira de CI/CD completa automatizando build, testes, provisionamento (IaC) e deploy.

###  Provisionamento de Infraestrutura na AWS (Terraform) - TC Fase 02

Toda a infraestrutura em nuvem do projeto é provisionada e gerenciada via código (IaC) utilizando o Terraform. A organização estrutural foi dividida em módulos lógicos para facilitar a manutenção e leitura:

- **Rede e Topologia (`network.tf`):** Criação de uma VPC dedicada (Virtual Private Cloud), Internet Gateway, Tabelas de Roteamento e Sub-redes Públicas distribuídas em múltiplas Zonas de Disponibilidade (us-east-1a e us-east-1b) para garantir resiliência.
- **Banco de Dados (`database.tf`):** Provisionamento de uma instância Amazon RDS (PostgreSQL 16) associada a um *Subnet Group* próprio. É blindada por um Security Group que restringe o tráfego da porta 5432 estritamente ao escopo da VPC.
- **Orquestração de Containers (`eks.tf`):** Criação do *Control Plane* do Amazon EKS e do *Node Group* responsável pelo processamento, já com regras de *auto-scaling* (limites mínimo e máximo de instâncias) para suportar a carga da API.
- **Registro de Imagens (`ecr.tf`):** Criação do repositório privado no Amazon ECR, configurado com varredura de vulnerabilidades habilitada (`scan_on_push = true`) para assegurar a integridade das imagens Docker.
- **Estado e Provedores (`backend.tf` & `providers.tf`):** Implementação de *Remote State*. O arquivo `terraform.tfstate` é salvo de forma remota em um Bucket S3 da AWS. Isso permite que a esteira CI/CD no GitHub Actions execute operações de infraestrutura sem perder o histórico.
- **Injeção de Manifestos Dinâmicos (`k8s_manifests.tf`):** Atua como ponte entre a infraestrutura recém-criada e a aplicação. O Terraform utiliza o provedor do Kubectl para se autenticar no EKS e aplicar os manifestos YAML (Deployment, HPA, Service, ConfigMap e Secret), injetando dinamicamente valores gerados em tempo de execução, como o *endpoint* do RDS que é entregue ao Spring Boot.
 
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

## Arquitetura Cloud e CI/CD (Ambiente de Produção/Lab) - TC Fase 02

Este projeto utiliza uma abordagem moderna de GitOps e Infraestrutura como Código (IaC) para garantir que todo o ambiente de nuvem seja construído, 
atualizado e versionado de forma automática e segura.

### Fluxo da Esteira (GitHub Actions)
A cada `push` nas branches principais, a nossa pipeline executa os seguintes passos:
1. **Continuous Integration (CI):** Validação de código e execução de testes automatizados via Maven.
2. **Resiliência de Estado:** Verificação e criação automática do bucket S3 (`oficina-state-***`) para armazenar o `terraform.tfstate`, garantindo que o cofre de infraestrutura sobreviva a resets do laboratório.
3. **Continuous Deployment (CD) - Infraestrutura:** O Terraform assume o controle e cria do zero (ou atualiza) toda a topologia de rede (VPC), o banco de dados (RDS) e o cluster Kubernetes (EKS).
4. **Continuous Deployment (CD) - Aplicação:** O Docker constrói a nova imagem da API com o SHA do commit e a envia para o AWS ECR. Em seguida, o Kubernetes puxa essa imagem e realiza o *rolling update* sem tempo de inatividade (*downtime*).

### Gestão do Kubernetes (Manifestos)
Toda a aplicação é orquestrada no Amazon EKS utilizando manifestos declarativos, garantindo escalabilidade e segurança para a API (Spring Boot):

- **Deployment (`deployment.yaml`):** Mantém os Pods da aplicação rodando com injeção dinâmica da imagem Docker gerada pela esteira CI/CD. Também define estritamente as requisições e limites de recursos (CPU: 250m a 500m / Memória: 512Mi a 1Gi) para garantir a estabilidade do container.
- **Service (`service.yaml`):** Expõe a aplicação externamente utilizando um *Load Balancer* provisionado automaticamente na AWS, mapeando o tráfego da porta 80 (pública internet) para a porta interna 8080.
- **ConfigMap & Secrets (`configmap.yaml` e `secret.yaml`):** Isolam as configurações e credenciais do código-fonte. O Terraform descobre dinamicamente o *endpoint* do RDS na AWS e o injeta no ConfigMap (`SPRING_DATASOURCE_URL`), enquanto o Secret protege o usuário e senha do banco de dados em Base64.
- **HPA - Autoscaling (`hpa.yaml`):** Configuração do *Horizontal Pod Autoscaler* para monitoramento de recursos. Caso a média de utilização de CPU dos pods ultrapasse 70%, o cluster é instruído a escalar automaticamente a aplicação de 1 para até 3 réplicas, garantindo alta disponibilidade durante picos de carga.