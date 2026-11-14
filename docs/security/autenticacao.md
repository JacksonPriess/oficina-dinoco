# Autenticação e autorização — Oficina Dinoco

Detalhamento dos mecanismos de autenticação de funcionários e clientes, autorização por propriedade da OS, webhooks e perfis de acesso.

## Autenticação de Funcionários

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
