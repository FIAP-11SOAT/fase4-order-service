resource "aws_secretsmanager_secret" "secrets" {
  name                    = "${local.project_name}-secrets"
  description             = "Secrets for ${local.project_name} project"
  recovery_window_in_days = 0

  tags = {
    Name = "${local.project_name}-secrets"
  }
}

resource "aws_secretsmanager_secret_version" "secrets" {
  secret_id     = aws_secretsmanager_secret.secrets.id
  secret_string = jsonencode({
    "spring.datasource.url"      = "jdbc:postgresql://localhost:5432/default",
    "spring.datasource.username" = "default",
    "spring.datasource.password" = "default",
    "spring.datasource.driver-class-name" = "org.postgresql.Driver"
  })
}
