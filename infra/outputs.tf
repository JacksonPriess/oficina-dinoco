output "postgres_endpoint" {
  description = "Endereço de conexão do banco de dados"
  value       = aws_db_instance.postgres.endpoint
}