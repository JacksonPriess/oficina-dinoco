# ADR-005 — Uso do New Relic para Observabilidade Centralizada

## Status
**Aceito**

## Contexto
A solução precisava monitorar tanto a aplicação quanto a infraestrutura Kubernetes.

Os requisitos incluíam latência, erros HTTP, logs estruturados, traces, correlationId, CPU, memória, Pods, restarts, uptime, healthcheck, alertas e dashboards.

## Decisão
Utilizar o **New Relic** como plataforma central de observabilidade.

A solução possui duas fontes principais de telemetria:

1. **New Relic Java Agent**, executado junto da aplicação Spring Boot, responsável por APM, transações, traces e encaminhamento de logs.
2. **New Relic Kubernetes Integration (`nri-bundle`)**, instalada dentro do cluster EKS via Helm, responsável pela coleta de métricas e eventos do Kubernetes.

Também são utilizados Synthetic Monitors para verificar externamente o endpoint de healthcheck.

## Consequências

### Positivas
- centralização de métricas, logs e traces;
- correlação entre requisições e eventos;
- visualização da saúde da aplicação e do cluster;
- criação de dashboards técnicos e de negócio;
- configuração de alertas e notificações.

### Negativas
- dependência de plataforma SaaS externa;
- necessidade de instalação e configuração de agentes;
- consumo adicional de recursos pelos componentes de coleta;
- custos podem aumentar com volume e retenção.

## Observações
A aplicação utiliza logs estruturados em JSON e `correlationId`.

O monitoramento do Kubernetes é realizado por componentes executados dentro do próprio cluster, que enviam os dados para o New Relic SaaS.

O New Relic não participa do fluxo funcional da aplicação; sua função é exclusivamente de observabilidade.
