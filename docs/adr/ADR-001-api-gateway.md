# ADR-001 — Uso do Amazon API Gateway como Ponto de Entrada Público

## Status
**Aceito**

## Contexto
A solução Oficina Dinoco possui diferentes componentes de backend, incluindo a aplicação principal executada no Amazon EKS e uma AWS Lambda responsável pela autenticação de clientes por CPF.

Era necessário definir um ponto de entrada público único para os consumidores da solução, capaz de direcionar diferentes rotas para diferentes componentes internos.

## Decisão
Utilizar o **Amazon API Gateway** como ponto de entrada público da solução.

O API Gateway recebe as requisições externas e encaminha cada rota ao componente adequado.

Exemplos:
- `POST /auth/cliente` é direcionado para a AWS Lambda de autenticação;
- as demais rotas da aplicação são encaminhadas para a API Spring Boot executada no Amazon EKS por meio do Load Balancer.

## Consequências

### Positivas
- centralização do acesso público;
- desacoplamento entre consumidor e componentes internos;
- possibilidade de aplicar políticas de segurança, rate limit e cache;
- exposição uniforme de múltiplos backends.

### Negativas
- adiciona um componente ao caminho da requisição;
- pode introduzir pequena latência adicional;
- exige configuração e manutenção das rotas.

## Observações
O Load Balancer do EKS continua responsável pelo encaminhamento para a aplicação Spring Boot, enquanto o API Gateway atua como camada pública de entrada e roteamento.
