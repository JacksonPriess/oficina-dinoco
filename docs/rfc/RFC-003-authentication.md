# RFC-003 — Estratégia de Autenticação com CPF, AWS Lambda e JWT

## Status

**Aceito**

## Contexto

O projeto possui dois perfis principais de acesso:

- funcionários, que já utilizam autenticação própria da aplicação;
- clientes, que precisam acessar funcionalidades específicas utilizando CPF.

Um dos requisitos da solução era utilizar uma função serverless para autenticação do cliente.

Também era necessário garantir que, após a autenticação, o cliente pudesse acessar apenas os recursos vinculados à sua própria identidade.

## Problema

Era necessário definir uma estratégia que permitisse:

- autenticar clientes por CPF;
- validar a existência e situação do cliente no banco;
- utilizar uma função serverless;
- evitar manutenção de sessão no servidor;
- proteger rotas específicas da API;
- identificar o cliente nas chamadas seguintes;
- impedir acesso a recursos pertencentes a outro cliente.

## Alternativas consideradas

### Autenticação diretamente no Spring Boot

Toda a autenticação poderia ser realizada pela aplicação principal.

Essa opção simplificaria a arquitetura, porém não atenderia diretamente ao requisito de utilização de uma função serverless no fluxo de autenticação do cliente.

### AWS Cognito

O Amazon Cognito poderia ser utilizado como serviço de identidade.

Apesar de ser uma solução adequada para autenticação, o requisito de autenticação baseada em CPF e validação do cliente no banco da aplicação tornaria necessária uma customização adicional.

Para o escopo acadêmico, essa opção adicionaria complexidade sem benefício proporcional.

### Lambda dedicada + JWT

Uma Lambda recebe o CPF, consulta o cliente no banco e, caso a autenticação seja válida, gera um JWT.

Nas chamadas seguintes, o cliente envia o token para a aplicação Spring Boot, que valida sua assinatura e utiliza as claims para identificar o cliente autenticado.

## Decisão proposta

Utilizar uma **AWS Lambda dedicada para autenticação de clientes por CPF**, com emissão de **JWT stateless**.

O fluxo adotado é:

1. o cliente envia o CPF para o endpoint de autenticação;
2. o API Gateway direciona a requisição para a Lambda;
3. a Lambda consulta o cliente no PostgreSQL;
4. caso o cliente seja válido e esteja autorizado, a Lambda gera um JWT;
5. o JWT contém o identificador do cliente em `sub` e a claim `tipo=CLIENTE`;
6. nas chamadas seguintes, o token é enviado no header `Authorization`;
7. a aplicação Spring Boot valida o JWT;
8. o identificador do cliente presente no token é utilizado para autorização por recurso.

## Impactos

### Positivos

- autenticação desacoplada da aplicação principal;
- atendimento ao requisito de uso de função serverless;
- arquitetura stateless;
- não há necessidade de sessão compartilhada;
- identificação do cliente transportada de forma segura no token;
- permite autorização por recurso usando o `clienteId` do próprio JWT;
- integração simples com Spring Security.

### Negativos

- adiciona um componente a mais na arquitetura;
- exige gerenciamento seguro do segredo utilizado para assinatura do JWT;
- Lambda precisa de acesso ao banco;
- qualquer alteração na estratégia de token pode impactar Lambda e aplicação.

## Riscos

- exposição do segredo de assinatura;
- geração de tokens com claims incorretas;
- uso de token expirado ou inválido;
- acesso indevido caso a aplicação confie em identificadores enviados pela requisição em vez da identidade do token.

Os riscos são mitigados com:

- armazenamento do segredo no AWS Secrets Manager;
- validação da assinatura e expiração do JWT;
- utilização do `sub` como identidade do cliente;
- autorização por recurso na aplicação;
- retorno apropriado de HTTP 401 e HTTP 403.

## Conclusão

A estratégia com AWS Lambda e JWT foi escolhida por atender ao requisito de autenticação por CPF utilizando uma função serverless e, ao mesmo tempo, manter a aplicação stateless.

O uso do identificador do cliente no JWT permite que a aplicação realize autorização de acesso aos recursos sem depender de um `clienteId` informado livremente pelo consumidor da API.
