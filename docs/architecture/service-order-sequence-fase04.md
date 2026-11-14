# Diagrama de Sequência — Abertura de Ordem de Serviço

## Objetivo

Esta documentação representa as duas formas de abertura de uma Ordem de Serviço (OS):

- abertura simples, sem itens de produtos ou serviços;
- abertura com itens, consultando os microserviços de Catálogo de Produtos e Catálogo de Serviços antes de persistir a OS.

O fluxo foi dividido em duas visões para facilitar a leitura: entrada da requisição e consultas aos catálogos. A aplicação Núcleo é responsável pela OS; cada catálogo mantém seus próprios dados.

## Fluxo geral de abertura da Ordem de Serviço

```mermaid
sequenceDiagram
    autonumber

    actor Atendente
    participant APIGW as AWS API Gateway
    participant Nucleo as API Núcleo
    participant DB as PostgreSQL do Núcleo

    Atendente->>APIGW: POST /api/ordens-servico
    APIGW->>Nucleo: Encaminha requisição pelo Load Balancer
    Nucleo->>Nucleo: Executa AbrirOrdemServicoHandler e instancia a OS

    alt Abertura simples, sem itens
        Note over Nucleo: Não consulta os catálogos
    else Abertura com itens
        opt Existem itens de produtos
            Nucleo->>Nucleo: Consulta Catálogo de Produtos e monta itens
        end
        opt Existem itens de serviços
            Nucleo->>Nucleo: Consulta Catálogo de Serviços e monta itens
        end
        Note over Nucleo: Consultas REST detalhadas no próximo diagrama
    end

    Nucleo->>DB: Persiste OS e seus itens, quando informados
    DB-->>Nucleo: OS salva
    Nucleo-->>APIGW: HTTP 201 com dados da OS
    APIGW-->>Atendente: Ordem de Serviço criada
```

O diagrama apresenta o caminho de sucesso. As chamadas aos catálogos acontecem antes de salvar a OS. Quando a requisição contém apenas produtos ou apenas serviços, somente o catálogo correspondente é consultado.

## Detalhamento — abertura com produtos e serviços

```mermaid
sequenceDiagram
    autonumber

    participant Nucleo as Núcleo / AbrirOrdemServicoHandler
    participant Produtos as Microserviço Catálogo de Produtos
    participant Servicos as Microserviço Catálogo de Serviços
    participant DB as PostgreSQL do Núcleo

    Note over Nucleo: Recebe IDs dos itens e instancia a OS em memória

    loop Para cada produto informado
        Nucleo->>Produtos: GET do produto por produtoId, com X-API-Key
        Note over Produtos: Consulta seu banco PostgreSQL
        Produtos-->>Nucleo: Dados do produto, incluindo preço de venda
        Nucleo->>Nucleo: Cria ItemOSProduto com ID, quantidade e preço retornado
    end

    loop Para cada serviço informado
        Nucleo->>Servicos: GET do serviço por servicoId, com API key
        Note over Servicos: Consulta seu banco MongoDB
        Servicos-->>Nucleo: Dados do serviço, incluindo preço padrão
        Nucleo->>Nucleo: Cria ItemOSServico com ID, mecânico e preço retornado
    end

    Note over Nucleo: Itens montados com os valores obtidos dos catálogos
    Nucleo->>DB: Salva a OS com os itens de produtos e serviços
    DB-->>Nucleo: OS salva com identificador e totais
    Note over Nucleo: Retorna o resultado para a camada HTTP
```

Os nomes das operações GET são descritivos; os caminhos HTTP concretos são definidos pelos adaptadores de integração de cada catálogo.

## Responsabilidades e origem dos dados

| Informação | Origem | Utilização no núcleo |
| --- | --- | --- |
| Identificador do produto e quantidade | Requisição de abertura | Identificam o produto e a quantidade do item da OS. |
| Preço de venda do produto | API do Catálogo de Produtos, com persistência em PostgreSQL | Copiado para o valor unitário de venda do item da OS. |
| Identificador do serviço e mecânico | Requisição de abertura | Identificam o serviço e o profissional associado ao item. |
| Preço padrão do serviço | API do Catálogo de Serviços, com persistência em MongoDB | Copiado para o valor cobrado do item da OS. |
| OS e seus itens | Núcleo | Persistidos no banco PostgreSQL do núcleo. |

## Integração e Clean Architecture

O `AbrirOrdemServicoHandler` coordena a criação por meio das interfaces `CatalogoProdutoGateway`, `CatalogoServicoGateway` e `OrdemServicoCommandGateway`.

- `CatalogoProdutoGateway.buscarPrecoVendaAtual(produtoId)` obtém o preço do produto.
- `CatalogoServicoGateway.buscarPrecoPadrao(servicoId)` obtém o preço do serviço.
- `OrdemServicoCommandGateway.salvar(novaOs)` persiste a OS com os itens montados.

As implementações dos gateways na infraestrutura realizam as chamadas REST e enviam a API key. O domínio não conhece HTTP, credenciais ou detalhes dos bancos dos catálogos. Os participantes “Núcleo” nos diagramas agrupam essas camadas para evitar excesso de elementos visuais.

No Kubernetes, o núcleo acessa os catálogos pelos endereços internos dos Services. Essas consultas não precisam passar pelo API Gateway nem pelos Load Balancers públicos dos catálogos. O núcleo também não consulta diretamente os bancos dos microserviços.

## Valores da OS e tratamento de falhas

Os preços retornados são copiados para os itens da OS no momento da abertura. Uma alteração posterior no catálogo não atualiza automaticamente uma OS já criada; os valores dos itens permanecem sob responsabilidade do núcleo.

Se um produto ou serviço não for encontrado, ou se a consulta ao catálogo falhar sem recuperação, a abertura é interrompida antes de persistir a OS. O tratamento de exceções da API define a resposta HTTP correspondente. Os diagramas não representam esses caminhos de erro.

## Estoque, mensageria e pagamento

As consultas de produtos e serviços durante a abertura são síncronas via REST. Não há publicação no RabbitMQ para obter esses preços.

A abertura da OS não reserva nem retira peças do estoque. A reserva pertence ao fluxo de aprovação do orçamento, e a retirada ocorre no início da execução. O evento de cadastro de um novo produto, que inicializa seu saldo no núcleo, é um fluxo assíncrono separado.

A cobrança também não ocorre na abertura: a SAGA de pagamento é iniciada após a finalização da execução da OS, conforme as regras do fluxo financeiro.

## Observabilidade

Durante o processamento, as aplicações instrumentadas enviam logs, traces e métricas ao New Relic. Essa telemetria permite acompanhar a criação da OS e investigar falhas nas consultas aos catálogos. O New Relic foi omitido dos participantes para manter os diagramas focados no fluxo funcional; seu envio de telemetria não é uma etapa de negócio que autoriza a criação da OS.
