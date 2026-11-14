# BDD — fluxo completo da ordem de serviço

BDD (Behavior-Driven Development) descreve o comportamento em linguagem de negócio 
e liga cada passo a uma implementação executável. 

## Step Definitions
O cenário Gherkin está em `src/test/resources/features/ordem-servico-pagamento.feature`.

## Escopo

Um único cenário de sucesso executa o fluxo por HTTP nas aplicações reais:

1. Autenticar um funcionário.
2. Cadastrar cliente e veículo, produto com 10 unidades e serviço.
3. Aguardar o saldo criado pelo evento do catálogo de produtos.
4. Abrir a OS com duas unidades de R$ 50 e um serviço de R$ 150; conferir o total de R$ 250.
5. Iniciar/concluir diagnóstico, enviar/aprovar orçamento; conferir a reserva de duas unidades.
6. Iniciar execução da OS e iniciar/concluir o item de serviço; finalizar a OS.
7. Conferir estoque físico de oito unidades e reserva zerada.
8. Aguardar pagamento pendente no financeiro, com o mesmo identificador de Saga, OS e valor.
9. Aprovar o pagamento no provedor mock e aguardar a aprovação no núcleo.
10. Concluir a OS e conferir OS/Saga no estado `ENTREGUE`.

A aprovação no provedor é uma operação de simulação. 
O teste não publica mensagens manualmente nem substitui os microserviços por mocks: 
publicação, consumo, outbox e processamento da Saga são executados pelas aplicações.

## Pré-requisitos

- Java 21 e Maven Wrapper do projeto.
- Núcleo, Catálogo de Produtos, Catálogo de Serviços, Financeiro e provedor mock 
em execução e acessíveis pela máquina que executa o teste.
- Bancos migrados e RabbitMQ compartilhado entre núcleo, produtos e financeiro, no mesmo virtual host.
- Perfil `rabbit` e workers/listeners habilitados. 
Os serviços devem iniciar antes do cenário para declarar exchanges, filas e bindings.
- Funcionário com permissão para as operações administrativas, com senha definitiva. 
A primeira troca de senha não faz parte deste cenário.
- API keys iguais às configuradas nas respectivas aplicações.
- Acesso de leitura ao PostgreSQL do núcleo para localizar o ID do item de serviço.

**Atenção ao Compose do financeiro:** ele possui RabbitMQ próprio, exposto em `5673` na máquina local. 
Subir os Composes isolados não garante que as aplicações compartilhem o broker. 
Use a configuração de integração já validada no ambiente, 
apontando núcleo, produtos e financeiro para o mesmo RabbitMQ. 
URLs configuradas abaixo indicam onde o teste chama as APIs; 
elas não alteram as integrações internas das aplicações.

O cenário cria registros novos e deixa-os disponíveis para inspeção. 
Execute em um ambiente de teste/laboratório. 
Não depende de IDs fixos nem remove dados preexistentes.

## Configuração no PowerShell

Defina as variáveis na mesma janela que executará o Maven. 
Os valores entre `<...>` devem ser substituídos pelas credenciais do ambiente; não os versione.

```powershell
$env:BDD_NUCLEO_URL = "http://localhost:8080"
$env:BDD_PRODUTO_URL = "http://localhost:8081"
$env:BDD_FINANCEIRO_URL = "http://localhost:8082"
$env:BDD_SERVICO_URL = "http://localhost:8083"
$env:BDD_MOCK_URL = "http://localhost:8090"

$env:BDD_LOGIN = "<login-do-funcionario>"
$env:BDD_PASSWORD = "<senha-definitiva>"
$env:CATALOGO_API_KEY = "<chave-do-catalogo-de-produtos>"
$env:CATALOGO_SERVICO_API_KEY = "<chave-do-catalogo-de-servicos>"
$env:FINANCEIRO_API_KEY = "<chave-do-financeiro>"
$env:MOCK_ADMIN_KEY = "<chave-de-simulacao-do-provedor>"

```

Para AWS, substitua as URLs por endereços acessíveis ao executor, inclusive através de port-forward, e configure acesso ao banco. 
Não é necessário expor publicamente o financeiro, o mock ou o RDS para executar o teste.

## Executar

Com as aplicações em execução:

```powershell
.\mvnw.cmd verify -Pbdd
```

Esse comando executa também a suíte padrão. 
O teste JPA existente requer Docker para iniciar PostgreSQL via Testcontainers.

Para executar **somente o cenário BDD**, sem executar novamente os testes unitários/JPA:

```powershell
.\mvnw.cmd test-compile failsafe:integration-test failsafe:verify -Pbdd
```

No Bash, substitua `.\mvnw.cmd` por `./mvnw`. 
O perfil `bdd` configura o Maven Failsafe para selecionar apenas `FluxoOficinaBddIT`; 
os outros arquivos `*IT` não são incluídos por esse perfil. 
A execução habitual `clean test` não inicia o BDD nem exige os microserviços.

## Relatórios

- HTML: `target/cucumber/relatorio.html`.
- JSON: `target/cucumber/relatorio.json`.
- XML JUnit: `target/cucumber/resultado.xml`.
- Resultado Maven Failsafe: `target/failsafe-reports/`.

O relatório inclui os identificadores criados e um `correlationId` para acompanhar os logs. 
Tokens, senhas e headers de autenticação não são registrados pelo cliente de teste.

Os passos assíncronos aguardam a condição esperada por até 90 segundos, consultando a cada segundo. 
Os requests HTTP têm timeout de 10 segundos. 
Uma falha informa a API, operação e caminho; Consultar os logs dessa aplicação com o identificador do cenário. 
Erros HTTP de validação nos passos síncronos interrompem o cenário imediatamente.

## Validação estrutural sem ambiente

```powershell
.\mvnw.cmd test-compile failsafe:integration-test failsafe:verify -Pbdd "-Dcucumber.execution.dry-run=true"
```

O dry-run verifica descoberta do cenário e associação das frases aos métodos Java. 
**Ele não chama APIs nem comprova o fluxo integrado.** 
Não utilize seu relatório como evidência de execução ponta a ponta. 
Execute novamente sem essa opção para gerar o resultado real.

## Cobertura

O BDD chama aplicações em outros processos e não soma automaticamente cobertura ao JaCoCo do núcleo. 
A verificação de 80% é independente:

```powershell
.\mvnw.cmd clean verify -Pcobertura-80
```

Os testes adicionais usam JUnit/Mockito para decisões, autenticação, outbox e gateways; 
testes de componentes com MockMvc verificam contratos HTTP, 
mapeamentos e casos de uso reais até a fronteira de persistência simulada. 
O cliente REST dos catálogos é exercitado contra um servidor HTTP local controlado. 
Esses testes não substituem validações reais de transações, SQL, concorrência ou 
entrega de mensagens no ambiente integrado.
