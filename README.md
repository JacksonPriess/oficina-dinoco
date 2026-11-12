# 🚗 API Oficina Mecânica Dinoco

Este repositório contém o **núcleo da Oficina Mecânica Dinoco**, desenvolvido para o Tech Challenge (TC) de Arquitetura de Software da FIAP. A API coordena o ciclo da ordem de serviço (OS), do atendimento à entrega, e mantém as regras de orçamento, execução, estoque e acompanhamento. Os catálogos de produtos e serviços e o financeiro têm APIs e dados próprios.

## 🎯 Objetivos Gerais do Projeto

- Gerenciar clientes, funcionários, veículos e ordens de serviço.
- Montar orçamentos com peças e serviços, registrar a decisão do cliente e acompanhar a execução.
- Controlar o estoque de peças e insumos e acompanhar o tempo médio de execução dos serviços.
- Coordenar a cobrança e condicionar a entrega à confirmação do pagamento quando houver valor a cobrar.
- Separar os domínios em componentes com responsabilidades claras, usando Domain-Driven Design (DDD) e Clean Architecture.

## Features adicionadas

- Abertura, listagem e rastreio de OS, diagnóstico, envio do orçamento por WhatsApp, aprovação ou reprovação, execução e entrega.
- Autenticação de funcionários por e-mail e senha e de clientes por CPF via AWS Lambda. O rastreio verifica o vínculo da OS com o cliente autenticado.
- Catálogos independentes. O núcleo consulta os preços por HTTP com o header `X-API-Key` e registra o valor cobrado no item da OS. Alterar o catálogo afeta novas inclusões, enquanto os itens existentes preservam seus valores.
- Inicialização de estoque por evento de cadastro de produto no RabbitMQ. O saldo e as movimentações são responsabilidade do núcleo.
- Consulta em lote ao catálogo de serviços para enriquecer as métricas de execução com descrição e tempo estimado.
- Saga de pagamento orquestrada pelo núcleo. A finalização técnica solicita a cobrança, a aprovação financeira libera a entrega e o estorno antes da entrega funciona como compensação.

## Tecnologias Utilizadas

| Área | Tecnologias e responsabilidades |
| --- | --- |
| API e segurança | Java 21, Spring Boot 4.0.6, Spring Web MVC, Spring Security, JWT (JSON Web Token), Springdoc OpenAPI e Swagger UI. |
| Integração | Spring RestClient para os catálogos e Spring AMQP (Advanced Message Queuing Protocol) com RabbitMQ para eventos de estoque e mensagens da Saga. |
| Dados | PostgreSQL 16 no núcleo, no catálogo de produtos e no financeiro, com bancos separados. Spring Data JPA e Flyway no núcleo. MongoDB 8 e Spring Data MongoDB no catálogo de serviços. |
| Build e testes | Maven Wrapper, JUnit 5, Mockito, REST Assured e Testcontainers. |
| Infraestrutura | Docker e Docker Compose para desenvolvimento. Terraform e GitHub Actions para provisionamento e automação. Kubernetes no Amazon EKS (Elastic Kubernetes Service), imagens no Amazon ECR (Elastic Container Registry) e PostgreSQL no Amazon RDS (Relational Database Service). |
| Segurança e observabilidade na AWS | AWS Secrets Manager, AWS Lambda, API Gateway, Spring Boot Actuator, logs estruturados e New Relic. |

O núcleo separa regras de domínio, casos de uso e adaptadores. As chamadas HTTP, a publicação e o consumo de mensagens e o acesso a dados ficam nos adaptadores de infraestrutura.

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

A separação permite ciclos de deploy independentes para infraestrutura, banco, autenticação serverless e aplicação. O API Gateway encaminha `POST /auth/cliente` para a Lambda e as rotas do núcleo para o Load Balancer da API no EKS.

---

## Separação da Infraestrutura - TC Fase 04

| Componente | Responsabilidade | Dados |
| --- | --- | --- |
| Núcleo (`oficina-dinoco`) | OS, clientes, veículos, funcionários, estoque, métricas e estado da Saga de pagamento. | PostgreSQL |
| [Catálogo de produtos](https://github.com/JacksonPriess/oficina-produto-api) | Cadastro e consulta de produtos e publicação do evento de cadastro. | PostgreSQL próprio |
| [Catálogo de serviços](https://github.com/JacksonPriess/oficina-servico-api) | Cadastro e consulta de serviços, preços e tempos estimados. | MongoDB |
| [Financeiro](https://github.com/JacksonPriess/oficina-financeiro-api) | Criação, aprovação e estorno de pagamentos. Integração com um provedor simulado. | PostgreSQL próprio |

Os três bancos PostgreSQL têm credenciais de aplicação separadas no RDS. O catálogo de serviços e o MongoDB foram validados localmente. O deploy desse catálogo no EKS é uma etapa posterior. O ambiente da Fase 04 parte de um MongoDB vazio e recebe os serviços pela API.

### Mensageria e Saga de pagamento

O núcleo, o catálogo de produtos e o financeiro compartilham o RabbitMQ. O catálogo de serviços é consultado por HTTP.

| Fluxo | Chave de roteamento | Fila |
| --- | --- | --- |
| Produtos → núcleo | `produto.cadastrado.v1` | `nucleo.estoque.produto-cadastrado` |
| Núcleo → financeiro | `pagamento.criar.v1` | `financeiro.pagamento.criar` |
| Núcleo → financeiro | `pagamento.estornar.v1` | `financeiro.pagamento.estornar` |
| Financeiro → núcleo | `pagamento.criado.v1`, `pagamento.aprovado.v1`, `pagamento.estornado.v1` | `nucleo.financeiro.eventos` |

Uma *outbox* é uma tabela de mensagens pendentes. A aplicação grava a mensagem na mesma transação que altera os dados de negócio e tenta publicá-la em segundo plano. O processamento de eventos repetidos protege contra a duplicação de efeitos no estoque e na Saga. As mensagens rejeitadas após as tentativas de consumo seguem para as filas de falha (*dead-letter queues*, DLQs), com sufixo `.dlq`.

Após a finalização técnica de uma OS com valor a cobrar, o núcleo solicita o pagamento e aguarda a aprovação antes de permitir a entrega. O estorno solicitado antes da entrega bloqueia a conclusão. Consulte o [fluxo e roteiro de testes da Saga](docs/saga-pagamento.md).

### Provisionamento na AWS

1. Provisione a infraestrutura do cluster e o RabbitMQ pelo [repositório de infraestrutura Kubernetes](https://github.com/JacksonPriess/oficina-infra-k8s).
2. Provisione o RDS e prepare os bancos e usuários no [repositório de infraestrutura de banco](https://github.com/JacksonPriess/oficina-infra-db).
3. Publique a [Lambda de autenticação](https://github.com/JacksonPriess/oficina-auth-lambda) para disponibilizar a chave JWT compartilhada.
4. Publique o catálogo de produtos, o financeiro e o núcleo.
5. Execute o workflow do API Gateway depois que o Service do núcleo criar o Load Balancer. Instale a integração do New Relic pelo repositório de infraestrutura Kubernetes.

## 🚀 Como Executar Local (Desenvolvimento)

Instale Docker com Compose e Java 21 para executar a aplicação pela IDE ou pelo Maven Wrapper. Cada comando do Compose deve ser executado na raiz do repositório correspondente.

1. Inicie o RabbitMQ compartilhado na rede `dinoco-local` e suba o [catálogo de produtos](https://github.com/JacksonPriess/oficina-produto-api) conforme seu README.
2. Suba o [catálogo de serviços](https://github.com/JacksonPriess/oficina-servico-api) com `docker compose up --build -d`. Cadastre os serviços pela API e use os IDs retornados ao montar a OS.
3. Para testar pagamento e entrega, suba o banco e o provedor simulado no repositório financeiro com `docker compose up --build -d postgres-financeiro provedor-mock`. Rode a API financeira com os perfis `local,rabbit` e aponte-a para o mesmo RabbitMQ dos demais serviços. Veja o [guia da Saga](docs/saga-pagamento.md) para as variáveis e o roteiro de teste.
4. No repositório do núcleo, execute `docker compose up --build -d`. Para executar o núcleo pelo IntelliJ, suba apenas o banco com `docker compose up -d postgres` e use os perfis `dev,rabbit`.
5. Acesse o [Swagger do núcleo](http://localhost:8080/swagger-ui/index.html) ou consulte `GET http://localhost:8080/actuator/health/readiness`. Use `docker compose logs -f api` para acompanhar a inicialização pelo Compose.

| Configuração do núcleo | IntelliJ ou Maven na máquina | Núcleo em Docker |
| --- | --- | --- |
| `CATALOGO_PRODUTO_URL` | `http://localhost:8081` | `http://catalogo-produto-api:8081` |
| `CATALOGO_SERVICO_URL` | `http://localhost:8083` | `http://catalogo-servico-api:8083` |
| `RABBITMQ_HOST` | `localhost` | `rabbitmq` |

Configure `CATALOGO_API_KEY` e `CATALOGO_SERVICO_API_KEY` com os valores usados por cada catálogo. Os componentes que usam mensageria devem compartilhar o mesmo broker, credenciais e *virtual host*. Os endereços Docker pressupõem que as aplicações estejam conectadas à rede `dinoco-local`.

## 📚 Documentação e Testes da API

* [Linguagem Ubíqua e Dicionário de Dados](./docs/negocio/linguagem_ubiqua.md)
* [Swagger UI - Oficina Dinoco](http://localhost:8080/swagger-ui/index.html) *(Disponível após subir a aplicação)*

### Cobertura de Testes — Melhoria Futura

Foi criado no `pom.xml` o perfil Maven `cobertura-80`, que permite validar uma cobertura mínima de **80% das linhas de código**, utilizando o JaCoCo.

O perfil pode ser executado manualmente com o comando:

```bash
./mvnw clean verify -Pcobertura-80
```

### 🚀 Insomnia Collection

Para facilitar os testes e a avaliação do fluxo completo da aplicação, disponibilizamos uma collection do Insomnia com todas as requisições organizadas na ordem lógica de execução:
Autenticação, Abrir OS, Iniciar diagnóstico, Adicionar Itens de Produto na OS, Adicionar Itens de Serviço na OS, Concluir Diagnóstico, Enviar Orçamento, Aprovar Orçamento, Verificar Estoque (Condicional), Iniciar Execução da OS, Iniciar Execução dos Itens de Serviços, Concluir Execução dos Itens de Serviços, Finalizar execução da OS, e por fim, Concluir a OS. 

1. Baixe o arquivo da collection: [`oficina-dinoco-insomnia-collection.yaml`](./docs/api/oficina-dinoco-insomnia-collection.yaml)
2. No Insomnia, vá em **Import/Export** > **Import Data** > **From File** e selecione o arquivo baixado.
3. Para concluir uma OS com cobrança, siga também o [roteiro de aprovação de pagamento](docs/saga-pagamento.md#roteiro-de-teste-integrado) após finalizar a execução técnica.

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

## Autorização por Perfil

Após a autenticação do funcionário, o JWT é validado pelo Spring Security e o perfil do usuário é convertido em uma `role`.

Os perfis atualmente suportados são:

- `ADMIN`
- `MECANICO`
- `ATENDENTE`

Internamente, o Spring Security trabalha com as authorities:

- `ROLE_ADMIN`
- `ROLE_MECANICO`
- `ROLE_ATENDENTE`

### Regras de acesso

As rotas internas da aplicação exigem que o usuário autenticado possua um dos perfis de funcionário.

Exemplo:

"ADMIN", "MECANICO", "ATENDENTE"  

---
## Documentações / Diagramas

Os diagramas das fases anteriores registram a evolução da arquitetura. A seção da Fase 04 descreve a separação atual.

*  **Saga Pagamentos orquestrado pelo núcleo :** [Saga de pagamento da OS](docs/architecture/saga-pagamentos-fase04.png)
*  **Diagrama de Componentes :** [Visualizar](docs/architecture/component-diagram-fase04.png)
*  **Diagrama de Sequência de Autenticação** [Visualizar](docs/architecture/authentication-sequence.png)
*  **Diagrama de Sequência de Abertura de OS** [Visualizar](docs/architecture/service-order-sequence-fase04.md)
*  **RFC-001 - Provedor nuvem** [Visualizar](docs/rfc/RFC-001-cloud-provider.md)
*  **RFC-002 - Banco de dados** [Visualizar](docs/rfc/RFC-002-database.md)
*  **RFC-003 - Estratégia autenticação** [Visualizar](docs/rfc/RFC-003-authentication.md) 
*  **ADR-001 - API Gateway** [Visualizar](docs/adr/ADR-001-api-gateway.md)
*  **ADR-002 - Amazon EKS** [Visualizar](docs/adr/ADR-002-eks.md)
*  **ADR-003 - Escalabidade HPA** [Visualizar](docs/adr/ADR-003-hpa.md)
*  **ADR-004 - JWT** [Visualizar](docs/adr/ADR-004-jwt-stateless.md)
*  **ADR-005 - Observabilidade** [Visualizar](docs/adr/ADR-005-new-relic.md)
*  **Justificativa do Banco** [Visualizar](docs/database/database-justification.md)
*  **Diagrama Banco ER** [Visualizar](docs/database/database-er-diagram.png)

*  **Arquitetura interna aplicação:** [Visualizar](docs/diagramas-fase2/DiagramaComponentesArquitetura.png)
* ️ **Fluxo de deploy:** [Visualizar](docs/diagramas-fase2/oficina-dinoco-fluxo-deploy-geral.drawio.png)

