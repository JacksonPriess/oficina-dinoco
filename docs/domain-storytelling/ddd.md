Domínio/Core da aplicação já foi definido pelo escopo do projeto: 

	○ Event Storming completo dos fluxos: 
		■ Criação e acompanhamento da OS;   
		■ Gestão de peças e insumos;
	○ Linguagem Ubíqua aplicada. “Dicionário da Linguagem”    

Mas segue estudo!

Domain Storytelling 

    Processo que auxilia no entendimento do negócio do cliente.

    Subdomínio Principal - Core
    
    Subdomínio Genérico - Comum a outros projetos (RH, Contabil, Folha, Autenticação da APP)
    
    Subdomínio Suporte - Complementa o principal, não há vantagem estratégica em ter ele, 
    e é diferente de empresa para empresa ( Marketing, Vendas)
    
    Para identificar os Subdomínios, marcamos reuniões com envolvidos diretos do negócio.

        - Domain Experts        

    App Egon.io para criar os diagramas.

        Identificar:
        Atores, (Atendente, Cliente, Mecânico, Fornecedor)
        Atividade, ação que o ator faz ( cria, envia, excluir, avisa, etc)
        Objeto de trabalho, é que recebe a ação do ator.  
        Número de sequência, orde das atividades.
        Anotação, para auxiliar no entendimento., sempre no objeto de trabalho.
            Ex: Atendente(ator) -> Cria(atividade) -> OS(obj de trabalho)

    Documentação da História

        Identificar se o user está falando sobre o negocio atual ou sobre o desejo.
    
    Domínios Puros -> Não ressalta tecnologias utilizadas, mostra as iterações dos atores apenas.
    Domínios Digitalizados -> Mostra iterações com sistemas, caso o negócio já tenha.

Contexto Delimitado / Bounded Context  
    