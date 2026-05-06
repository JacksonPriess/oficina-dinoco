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

## 🛠️ Tecnologias Utilizadas

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

### Build, Infraestrutura e Testes
- **Maven & Docker / Docker Compose**
- **JUnit 5 & Mockito**
- **Testcontainers & Rest Assured**
- **Testes de integração com PostgreSQL real**
- **Padrão AAA (Arrange, Act, Assert)**

---

## 📚 Documentação e Testes da API

* [Linguagem Ubíqua e Dicionário de Dados](./docs/negocio/linguagem_ubiqua.md)
* [Swagger UI - Oficina Dinoco](http://localhost:8080/swagger-ui/index.html) *(Disponível após subir a aplicação)*

### 🚀 Insomnia Collection (Recomendado para Avaliação)
Para facilitar os testes e a avaliação do fluxo completo da aplicação, disponibilizamos uma collection do Insomnia com todas as requisições organizadas na ordem lógica de execução:
Autenticação, Abrir OS, Iniciar diagnóstico, Adicionar Itens de Produto na OS, Adicionar Itens de Serviço na OS, Concluir Diagnóstico, Enviar Orçamento, Aprovar Orçamento, Verificar Estoque (Condicional), Iniciar Execução da OS, Iniciar Execução dos Itens de Serviços, Concluir Execução dos Itens de Serviços, Finalizar execução da OS, e por fim, Concluir a OS. 

1. Baixe o arquivo da collection: [`oficina-dinoco-insomnia-collection.json`](./docs/api/oficina-dinoco-insomnia-collection.yaml)
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
## 🚀 Como Executar

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