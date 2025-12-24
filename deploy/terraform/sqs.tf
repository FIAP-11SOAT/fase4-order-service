resource "aws_sqs_queue" "order_service_dlq" {
  name = "${local.project_name}-dlq"

  tags = {
    Name = "${local.project_name}-dlq"
  }
}

resource "aws_sqs_queue" "order_service_queue" {
  name = "${local.project_name}-queue"

  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.order_service_dlq.arn
    maxReceiveCount     = 3
  })

  tags = {
    Name = "${local.project_name}-queue"
  }
}