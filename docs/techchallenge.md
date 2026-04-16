Proposta

	Desenvolver a primeira versão (MVP) do back-end do sistema da oficina, 	com foco em gestão de ordens de serviço, clientes e peças, aplicando Domain Driven Design (DDD) e garantindo boas práticas de Qualidade de Software e Segurança

Funcionalidades obrigatórias

Fluxos principais

Criação da Ordem de Serviço (OS):

	● Identificação do cliente por CPF/CNPJ;      
	● Cadastro de veículo (placa, marca, modelo, ano);    
	● Inclusão dos serviços solicitados (exemplo: troca de óleo, alinhamento )
	● Possibilidade de incluir peças e insumos necessários;
	● Orçamento gerado automaticamente com base nos serviços e peças;
	● Envio do orçamento ao cliente para aprovação.

Acompanhamento da OS:

	● Status da OS:
        ○ Recebida; Carro estacionou, recepção pegou os dados o mecanico recebeu OS para iniciar diagnostico;
        ○ Em diagnóstico;
        ○ Aguardando Cotação -> (NOVO!) (Recepção está caçando os preços das peças que não tem na oficina).
        ○ Aguardando aprovação;
        ○ Aprovada -> (NOVO!) (Cliente disse sim, a oficina vai encomendar as peças e preparar o elevador).
        ○ Reprovada -> (Cliente achou caro e foi embora na fase 4. A OS morre aqui). 
        ○ Em execução;
        ○ Finalizada;
        ○ Entregue.

● Alteração automática dos status conforme ações no sistema;
● Permitir consulta por parte do cliente via API para acompanhar o progresso.

Gestão administrativa:

	● CRUD de clientes;
	● CRUD de veículos;
	● CRUD de serviços;
	● CRUD de peças e insumos, com controle de estoque; (integração da os com estoque)
	● Listagem e detalhamento de ordens de serviço;
	● Monitoramento do tempo médio de execução dos serviços. ( Apenas da execução - timestamp )

Segurança e qualidade:

	● Implementação de autenticação JWT para APIs administrativas;
	● Validação dos dados sensíveis (CPF/CNPJ, placa de veículo (mercosul etc) );    0-10
	● Testes unitários e de integração para os principais fluxos, todos basicamente.

Requisitos técnicos

	● Back-end monolítico.
	● Como será um MVP, é possível criar um Monolito utilizando a arquitetura em camadas.
	● A escolha do banco de dados é livre, mas é necessário justificar a preferência pelo banco utilizado.
	● APIs RESTful documentadas via Swagger ou similar.
	● Dockerfile para build da aplicação.
	● docker-compose.yml para orquestrar ambiente completo.
	● Testes automatizados com cobertura mínima de 80% nos domínios críticos.
	● Configuração para execução local simples (README.md explicativo).
	● Organização em repositório privado com acesso ao usuário soat-architecture 

Github da Fiap - Rep Privado

Entregáveis da Fase 1

● Vídeo de até 15 minutos demonstrando todos os pontos (pode ser em grupo ou individual);

● Documentação DDD (Miro ou equivalente), com:

	○ Domain StoryTelling:

	○ Event Storming completo dos fluxos: 
		■ Criação e acompanhamento da OS;   
		■ Gestão de peças e insumos;              
	○ Diagramas conforme apresentado na disciplina de DDD;
	○ Linguagem Ubíqua aplicada. “Dicionário da Linguagem”

https://miro.com/app/board/uXjVGyCMYF8=/

● Código-fonte no repositório privado, incluindo:

       ○ APIs conforme requisitos;   
       ○ Dockerfile e docker-compose configurados;
       ○ README.md completo com instruções de uso e objetivos.

● Relatório com análise de vulnerabilidades:

        ○ Adicionar no relatório PDF  a análise do scan realizado no código.
        ○ Sonar/AspZapProxy

● Documento de entrega (PDF) com:

        ○ Nome do grupo;
        ○ Participantes e usernames no Discord;
        ○ Link da documentação;
        ○ Link do repositório;
        ○ Relatório com análise de vulnerabilidades encontradas no sistema.
