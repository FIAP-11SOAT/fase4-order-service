resource "aws_sqs_queue" "order_service_queue" {
  name = "${local.project_name}-queue"

  tags = {
    Name = "${local.project_name}-queue"
  }
}