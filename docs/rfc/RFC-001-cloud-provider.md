# RFC-001 — Escolha da AWS como Plataforma de Nuvem

## Status

**Aceito**

## Contexto

O projeto Oficina Dinoco precisava de uma plataforma de nuvem capaz de hospedar os principais componentes da solução, incluindo API pública, execução da aplicação, função serverless, banco de dados relacional, armazenamento de estado de infraestrutura, registro de imagens de containers, gerenciamento de segredos e recursos de rede.

Além da execução da aplicação, a solução também precisava atender aos requisitos acadêmicos de infraestrutura como código, escalabilidade, autenticação serverless, monitoramento e deploy automatizado.

## Problema

Era necessário escolher uma plataforma de nuvem que permitisse:

- executar a aplicação containerizada;
- disponibilizar uma API pública;
- utilizar funções serverless;
- hospedar banco PostgreSQL gerenciado;
- criar rede, subnets e regras de segurança;
- armazenar imagens Docker;
- manter segredos de forma segura;
- permitir provisionamento com Terraform;
- integrar com pipeline de CI/CD;
- fornecer recursos compatíveis com monitoramento e escalabilidade.

## Alternativas consideradas

### AWS

A AWS oferece serviços diretamente aderentes à arquitetura definida para o projeto, como:

- Amazon API Gateway;
- AWS Lambda;
- Amazon EKS;
- Amazon EC2;
- Amazon RDS;
- Amazon ECR;
- Amazon S3;
- AWS Secrets Manager;
- VPC e Security Groups.

Também possui ampla compatibilidade com Terraform e GitHub Actions.

### Microsoft Azure

A Azure poderia atender aos mesmos requisitos por meio de serviços equivalentes, como Azure API Management, Azure Kubernetes Service, Azure Functions e Azure Database for PostgreSQL.

Entretanto, o projeto já estava sendo desenvolvido utilizando AWS Academy, o que tornava a AWS mais adequada ao contexto de execução e aprendizado.

### Google Cloud Platform

A Google Cloud também disponibiliza recursos equivalentes, como GKE, Cloud Run, Cloud Functions e Cloud SQL.

Apesar de tecnicamente viável, não apresentava vantagem relevante para o contexto do projeto em relação à AWS já utilizada no ambiente acadêmico.

## Decisão proposta

Utilizar a **Amazon Web Services (AWS)** como plataforma de nuvem principal da solução Oficina Dinoco.

A arquitetura utiliza os seguintes serviços principais:

- **API Gateway** como ponto de entrada público;
- **Lambda** para autenticação de clientes por CPF;
- **EKS** para execução da aplicação Spring Boot;
- **EC2 Node Group** como infraestrutura computacional do cluster;
- **RDS PostgreSQL** para persistência;
- **ECR** para armazenamento das imagens Docker;
- **Secrets Manager** para armazenamento de segredos e credenciais;
- **S3** para armazenamento remoto do estado do Terraform;
- **VPC, Subnets e Security Groups** para isolamento e controle de rede.

## Impactos

### Positivos

- integração nativa entre os componentes;
- ampla oferta de serviços gerenciados;
- aderência ao uso de infraestrutura como código;
- suporte a escalabilidade horizontal com Kubernetes;
- integração com GitHub Actions;
- possibilidade de utilizar serviços serverless e containers na mesma solução;
- compatibilidade com o ambiente acadêmico utilizado no projeto.

### Negativos

- maior complexidade operacional em comparação com uma arquitetura totalmente serverless;
- dependência da plataforma AWS;
- necessidade de conhecimentos específicos de IAM, redes, EKS e serviços gerenciados;
- custos podem aumentar em ambientes de produção dependendo da escala.

## Riscos

- configuração incorreta de permissões IAM;
- exposição indevida de recursos públicos;
- aumento de custos por uso excessivo de recursos;
- complexidade adicional na administração do cluster Kubernetes.

Esses riscos são reduzidos com uso de Terraform, Security Groups, Secrets Manager, controle de acesso e monitoramento.

## Conclusão

A AWS foi escolhida por oferecer todos os serviços necessários para a arquitetura proposta, possuir integração adequada com Terraform e GitHub Actions e estar alinhada ao ambiente acadêmico utilizado no desenvolvimento do projeto.

A escolha permite combinar serviços gerenciados, containers, serverless, banco relacional e observabilidade em uma única plataforma.
