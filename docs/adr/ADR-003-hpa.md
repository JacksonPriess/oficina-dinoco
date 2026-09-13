# ADR-003 — Uso de Horizontal Pod Autoscaler para Escalabilidade

## Status
**Aceito**

## Contexto
A carga da API pode variar ao longo do tempo. Uma quantidade fixa de Pods poderia gerar desperdício de recursos em baixa utilização ou capacidade insuficiente em períodos de maior demanda.

## Decisão
Utilizar o **Horizontal Pod Autoscaler (HPA)** do Kubernetes para ajustar automaticamente a quantidade de réplicas da aplicação com base na utilização de CPU.

Configuração adotada:
- `minReplicas: 1`
- `maxReplicas: 3`
- target de CPU: `70%`

O HPA utiliza métricas disponibilizadas pelo Metrics Server do cluster.

## Consequências

### Positivas
- ajuste automático da quantidade de Pods;
- melhor utilização de recursos;
- aumento de réplicas em períodos de maior carga;
- redução de réplicas quando a demanda diminui.

### Negativas
- depende da disponibilidade das métricas;
- parâmetros inadequados podem causar escala excessiva ou insuficiente;
- mais réplicas aumentam o consumo dos nodes;
- não resolve gargalos externos, como banco de dados.

## Observações
O HPA utiliza a utilização de CPU em relação ao `request` configurado para o container.

A variação da quantidade de Pods pode ser acompanhada pelo Kubernetes e pelo New Relic.
