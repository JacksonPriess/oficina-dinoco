# language: pt
Funcionalidade: Entrega de veículo após pagamento da ordem de serviço
  A oficina cobra somente após finalizar o trabalho e entrega após receber.

  Cenário: Executar uma OS com produto e serviço e entregá-la após pagamento aprovado
    Dado que um funcionário está autenticado na oficina
    E existe um cliente com veículo cadastrado
    E existe um produto de 50 reais com 10 unidades em estoque
    E existe um serviço de 150 reais no catálogo
    Quando abro uma OS com 2 unidades do produto e o serviço
    Então o orçamento deve totalizar 250 reais
    Quando inicio e diagnóstico
    Quando concluo o diagnóstico
    E envio e aprovo o orçamento
    Então devem estar reservadas 2 unidades do produto
    Quando executo o serviço e finalizo a OS
    Então o estoque deve conter 8 unidades reais e nenhuma reservada
    E deve existir um pagamento pendente de 250 reais para essa OS
    Quando aprovo esse pagamento no provedor mock
    Então o núcleo deve reconhecer a aprovação na mesma Saga
    Quando concluo a ordem de serviço
    Então a OS e sua Saga devem estar entregues
