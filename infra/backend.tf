terraform {
  backend "s3" {
    bucket = "oficina-state-priess951"
    key    = "infra/terraform.tfstate"
    region = "us-east-1"
  }
}