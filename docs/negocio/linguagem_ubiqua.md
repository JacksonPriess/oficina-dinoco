# Dicionário de Dados e Linguagem Ubíqua - Fluxos Principais

## Entidades Principais

### Ordem de Serviço (OS)
* **Definição:** O documento vital que centraliza todo o ciclo de vida do atendimento de um veículo. Contém a reclamação do cliente, o diagnóstico do mecânico, os produtos (peças e insumos) necessários e os serviços.
* **Regras:** Uma OS deve sempre estar vinculada a um Veículo e a um Cliente.

### Serviço
* **Definição:** Representa o catálogo de mão de obra e procedimentos técnicos oferecidos pela oficina (ex: Alinhamento, Balanceamento, Troca de Óleo, Mão de Obra Mecânica).
* **Regras:** Define o valor padrão opcionalmente, um tempo estimado de execução. O preço neste catálogo pode ser reajustado com o tempo sem alterar o histórico das Ordens de Serviço passadas.

### Produto (Catálogo de Peças e Insumos)
* **Definição:** Representa qualquer componente físico, seja uma peça de reposição ou material de consumo da oficina.
* **Alerta de Linguagem (Decisão Arquitetural):** No dia a dia, a operação utiliza os termos "Peça" e "Insumo" separadamente. No entanto, optou-se por aglutinar ambos na entidade genérica `Produto` no código, diferenciando-os apenas por um `Enum`, visando simplicidade inicial.
* **Atenção para o Futuro:** Caso as regras comecem a divergir muito entre Peças e Insumos, a entidade `Produto` deverá ser separada para refletir melhor o domínio.

### Item de Produto (na OS)
* **Definição:** Representa a aplicação efetiva de um `Produto` (Peça ou Insumo) em uma Ordem de Serviço específica.
* **Regras:** Deve armazenar a quantidade utilizada, o valor unitário cobrado. Os valores copiados para o item tornam-se imutáveis em relação às flutuações de preço do catálogo central.

### Item de Serviço (na OS)
* **Definição:** Representa a execução de um esforço ou serviço específico dentro de uma Ordem de Serviço.
* **Regras:** Assim como o item de produto, deve registrar o valor da mão de obra fixado no momento do orçamento.
* **Status:** O item de serviço OS tem evolução individual, e passa por 3 status:
* **PENDENTE:** Quando o item é adicionado na OS.
* **EM_ANDAMENTO:** Quando o mecânico inicia o serviço, registrando a data e hora de inicio. 
* **CONCLUIDO:** Quando o mecânico conclúi o serviço, registrando a data e hora da conclusão.

---

## Ciclo de Vida da OS (Dicionário de Status)

* **RECEBIDA:** O veículo deu entrada na oficina, foram registrados dados do cliente, veículo e a reclamação, mas nenhum mecânico foi alocado ou iniciou o diagnóstico inicial.
* **EM_DIAGNOSTICO:** O veículo está no elevador/pátio sob avaliação do mecânico para descobrir a causa do problema relatado.
* **AGUARDANDO_ORCAMENTO:** O diagnóstico foi concluído. O atendente está levantando o custo das horas de serviço e checando a disponibilidade das peças no estoque.
* **AGUARDANDO_APROVACAO:** O orçamento está com o cliente. A oficina aguarda o "Aprovar" ou "Reprovar" do cliente.
* **REPROVADA:** O cliente não aceitou o orçamento. A OS é encerrada sem execução de serviço.
* **AGUARDANDO_FORNECEDOR:** O orçamento foi aprovado, mas a execução da OS não pode começar porque peças necessárias não estão no estoque físico e foram encomendadas de fornecedores.
* **AGUARDANDO_EXECUCAO:** Tudo está pronto (orçamento aprovado e peças separadas no estoque), esperando apenas a disponibilidade do mecânico para colocar a mão na massa.
* **EM_EXECUCAO:** O mecânico está ativamente trocando as peças e realizando os serviços.
* **FINALIZADA:** O serviço técnico terminou, o carro foi testado. A OS vai para o setor financeiro/faturamento.
* **ENTREGUE:** O cliente retirou o veículo e a chave foi devolvida.

---

## Controle de Estoque

### Movimentação de Estoque
* **Definição:** O registro histórico, imutável e auditável de qualquer alteração na quantidade de um `Produto` no estoque da oficina.
* **Regras:** O saldo atual de um produto nunca deve ser alterado diretamente (sem rastreio). Toda movimentação deve registrar a data, a quantidade alterada, o tipo da operação (`TipoMovimentacao`) e a justificativa ou documento de origem (ex: o número da Ordem de Serviço).

### Tipos de Movimentação (Operações)

* **ENTRADA:** Aumenta o saldo físico. Utilizado exclusivamente no momento do cadastro inicial do produto no sistema, quando este já entra com uma quantidade maior que zero.
* **RESERVA_OS:** Não altera o saldo físico da prateleira, mas diminui o "saldo disponível" para novos orçamentos. Ocorre para garantir que as peças necessárias para uma OS aprovada não sejam vendidas ou usadas em outro veículo.
* **BAIXA_EXECUCAO_OS:** Efetiva o consumo da peça. Diminui a quantidade física do estoque e consome a quantidade que estava reservada. Ocorre quando o mecânico inicia a execução dos serviços e aplica fisicamente a peça/insumo no veículo.
* **AJUSTE_ENTRADA:** Aumenta o saldo físico através de uma intervenção manual justificada. Utilizado para corrigir divergências positivas de inventário (ex: contagem física encontrou mais peças do que o sistema marcava) ou recebimentos avulsos.
* **AJUSTE_SAIDA:** Diminui o saldo físico através de uma intervenção manual justificada. Utilizado para corrigir divergências negativas de inventário. Ocorrendo fora do fluxo normal de uma Ordem de Serviço.