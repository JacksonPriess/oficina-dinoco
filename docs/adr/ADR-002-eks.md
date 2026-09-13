# ADR-002 — Uso do Amazon EKS para Execução da Aplicação

## Status
**Aceito**

## Contexto
A aplicação principal da Oficina Dinoco é uma API Java 21 com Spring Boot, empacotada em container Docker.

Era necessário utilizar uma plataforma que permitisse execução containerizada, escalabilidade horizontal, integração com Load Balancer, deploy automatizado, ConfigMaps, Secrets e monitoramento.

## Decisão
Utilizar o **Amazon Elastic Kubernetes Service (EKS)** como plataforma de execução da aplicação principal.

A aplicação é executada em Pods Kubernetes dentro de um Deployment, utilizando um EC2 Node Group como capacidade computacional.

A imagem da aplicação é armazenada no Amazon ECR e utilizada pelo Deployment nos processos de atualização.

## Consequências

### Positivas
- execução padronizada por containers;
- suporte nativo a escalabilidade horizontal;
- facilidade para atualização de versões;
- separação entre configuração, segredos e imagem;
- integração com Load Balancer, ECR e observabilidade.

### Negativas
- maior complexidade operacional;
- necessidade de conhecimentos de Kubernetes;
- custo do cluster e dos nodes EC2;
- manutenção de manifests e configurações.

## Observações
O EKS permite o uso de recursos como Deployment, Service, ConfigMap, Secret e Horizontal Pod Autoscaler, utilizados pela solução.
