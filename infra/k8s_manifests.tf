
# Autenticação do Terraform no Cluster EKS recém-criado
data "aws_eks_cluster_auth" "cluster" {
  name = aws_eks_cluster.eks_cluster.name
}

provider "kubectl" {
  host                   = aws_eks_cluster.eks_cluster.endpoint
  cluster_ca_certificate = base64decode(aws_eks_cluster.eks_cluster.certificate_authority[0].data)
  token                  = data.aws_eks_cluster_auth.cluster.token
  load_config_file       = false
}

# Variável que receberá a URL da imagem Docker vinda do GitHub Actions
variable "api_image" {
  description = "Imagem Docker da API"
  type        = string
  default     = "nginx:latest" # Valor de fallback
}

# Aplicação dos manifestos do Kubernetes no Cluster EKS
resource "kubectl_manifest" "configmap" {
  yaml_body = templatefile("../k8s/configmap.yaml", {
    # Aponta para o recurso do seu RDS (verifique se no seu database.tf o nome é "postgres")
    DB_ENDPOINT = aws_db_instance.postgres.endpoint
  })

  # Garante que o ConfigMap só seja criado após os servidores (nodes) e o banco estarem prontos
  depends_on = [aws_eks_node_group.eks_nodes, aws_db_instance.postgres]
}

resource "kubectl_manifest" "secret" {
  yaml_body  = file("../k8s/secret.yaml")
  depends_on = [aws_eks_node_group.eks_nodes]
}

resource "kubectl_manifest" "deployment" {
  # O templatefile substitui a variável ${DOCKER_IMAGE} pela tag gerada no CI/CD
  yaml_body = templatefile("../k8s/deployment.yaml", {
    DOCKER_IMAGE = var.api_image
  })
  depends_on = [kubectl_manifest.configmap, kubectl_manifest.secret]
}

resource "kubectl_manifest" "service" {
  yaml_body  = file("../k8s/service.yaml")
  depends_on = [kubectl_manifest.deployment]
}

resource "kubectl_manifest" "hpa" {
  yaml_body  = file("../k8s/hpa.yaml")
  depends_on = [kubectl_manifest.deployment]
}