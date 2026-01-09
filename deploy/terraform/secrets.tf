resource "aws_secretsmanager_secret" "secrets" {
  name                    = "${local.project_name}-secrets"
  description             = "Secrets for ${local.project_name} project"
  recovery_window_in_days = 0

  tags = {
    Name = "${local.project_name}-secrets"
  }
}


locals {
  RDS_HOST = local.aws_infra_secrets["RDS_HOST"]
  RDS_DB   = postgresql_database.app_db.name
  RDS_USER = postgresql_role.app_db_user.name
  RDS_PASS = random_password.app_db_password.result
}

resource "aws_secretsmanager_secret_version" "secrets" {
  secret_id = aws_secretsmanager_secret.secrets.id
  secret_string = jsonencode({
    "spring.datasource.url"               = "jdbc:postgresql://${local.RDS_HOST}:5432/${local.RDS_DB}?sslmode=require"
    "spring.datasource.username"          = local.RDS_USER
    "spring.datasource.password"          = local.RDS_PASS
    "spring.datasource.driver-class-name" = "org.postgresql.Driver" 
    "fase4.order.service.apigateway.url"  = trimsuffix(local.aws_infra_secrets["GTW_ENDPOINT"], "/")
    "fase4.order.service.auth.jwk"        = local.aws_infra_secrets["JWT_JWK"]
  })
}
