## Em desenvolvimento...

# 🚗 API Oficina Dinoco

Uma API RESTful desenvolvida para gerenciar o fluxo principal de uma oficina mecânica, 
contemplando o ciclo de vida completo de Ordens de Serviço (OS) + controle de estoque (Peças e Insumos).

## 🎯 Objetivos Gerais do Projeto

* Gestão de ordens de serviço e controle de estoque;
* Controle rigoroso de estoque através de reservas atreladas às Ordens de Serviço;
* Histórico imutável de valores de produtos e serviços prestados;
* Orçamento gerado automaticamente com base nos serviços e peças;
* Envio do orçamento ao cliente para aprovação;
* Acompanhamento da OS;
* Permitir consulta por parte do cliente via API para acompanhar o progresso;
* Listagem e detalhamento de ordens de serviço;
* Gestão administrativa com CRUD de clientes, veículos, serviços, produtos, funcionários;  
* Relatório para monitoramento do tempo médio de execução dos serviços dentro de uma OS; 

## 🛠️ Tecnologias Utilizadas

### Backend
- **Java 21**
- **Spring Boot 4.0.6**
- **Spring Web MVC**
- **Spring Data JPA**
- **Spring Security**
- **JWT (Auth0 Java JWT)**
- **Bean Validation**
- **Lombok**

### Banco de Dados
- **PostgreSQL**
- **Flyway** (migrações e versionamento)
- **Seed inicial de dados no profile dev**

### Build e Gerenciamento
- **Maven**
- **Docker / Docker Compose**

### Testes
- **JUnit 5**
- **Mockito**
- **Testcontainers**
- **Rest Assured**
- **Testes de integração com PostgreSQL real**
- **Padrão AAA (Arrange, Act, Assert)**

---
## 📚 Negócio
* [Linguagem Ubíqua e Dicionário de Dados](./docs/negocio/linguagem_ubiqua.md)

---

## 🚀 Como executar localmente

### Pré-requisitos
Antes de começar, você precisará ter instalado em sua máquina:
* [JDK 21](https://adoptium.net/)
* [Maven](https://maven.apache.org/)
* [Docker](https://www.docker.com/)

## 🚀 Como Executar

### 1. Preparação

Clone o repositório:

``` bash
git clone git@github.com:JacksonPriess/oficina-dinoco.git
cd oficina-dinoco
```
------------------------------------------------------------------------

### 2. Subir ambiente com (API + Banco) 

#### Suba todo o ecossistema:

``` bash
docker compose up --build -d
```

A API estará disponível em: http://localhost:8080

## Swagger UI (Interface gráfica)

* [Swagger UI - Oficina Dinoco](http://localhost:8080/swagger-ui/index.html)

## Documentação do Domínio

* [Linguagem Ubíqua e Dicionário de Dados](./docs/negocio/linguagem_ubiqua.md)
