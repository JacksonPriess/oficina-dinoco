# 1. Grupo de Sub-redes do Banco (Exigência da AWS para alta disponibilidade)
resource "aws_db_subnet_group" "db_subnet_group" {
  name       = "${local.prefix}-db-subnet-group"
  subnet_ids = [aws_subnet.public_a.id, aws_subnet.public_b.id]

  tags = {
    Name = "${local.prefix}-db-subnet-group"
  }
}

# 2. Security Group (Firewall) para o Banco de Dados
resource "aws_security_group" "db_sg" {
  name        = "${local.prefix}-db-sg"
  description = "Permitir trafego para o PostgreSQL"
  vpc_id      = aws_vpc.main.id

  # Regra de entrada: Permite conexões na porta 5432 vindas de qualquer lugar da VPC
  ingress {
    from_port   = 5432
    to_port     = 5432
    protocol    = "tcp"
    cidr_blocks = [aws_vpc.main.cidr_block]
  }

  # Regra de saída: Permite que o banco responda às requisições
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "${local.prefix}-db-sg"
  }
}

# 3. Instância do Banco de Dados PostgreSQL 16
resource "aws_db_instance" "postgres" {
  identifier             = "${local.prefix}-postgres"
  engine                 = "postgres"
  engine_version         = "16"
  instance_class         = "db.t3.micro"
  allocated_storage      = 20
  storage_type           = "gp2"

  db_name                = "oficina_db"
  username               = "dbadmin"
  password               = "adminpassword"

  db_subnet_group_name   = aws_db_subnet_group.db_subnet_group.name
  vpc_security_group_ids = [aws_security_group.db_sg.id]

  skip_final_snapshot    = true
  publicly_accessible    = false

  # Garante que o Enhanced Monitoring fique desligado (regra do lab)
  monitoring_interval    = 0

  # Força Single-AZ para não dobrar o consumo de créditos
  multi_az               = false

  tags = {
    Name = "${local.prefix}-postgres"
  }
}