output "postgres_endpoint" {
  description = "Endereço de conexão do banco de dados"
  value       = aws_db_instance.postgres.endpoint
}

output "eks_cluster_name" {
  description = "Nome do Cluster EKS"
  value       = aws_eks_cluster.eks_cluster.name
}