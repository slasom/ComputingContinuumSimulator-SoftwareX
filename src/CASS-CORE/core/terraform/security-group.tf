# Security group
resource "aws_security_group" "virtual_environment_perses" {
  name = "${var.author}_perses_${var.project_name}"

  lifecycle {
    create_before_destroy = true
  }

   egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
  
  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    description = "ssh 22 / admin machines"
  }

  ingress {
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    description = "expose 443 to outside"
  }

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    description = "expose 80 to outside"
  }

  ingress {
    from_port   = 6000
    to_port     = var.end_port
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    description = "expose 80 to outside"
  }

}
