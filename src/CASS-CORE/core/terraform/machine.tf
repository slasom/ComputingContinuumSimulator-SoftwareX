resource "aws_instance" "virtual_environment_perses" {

  ami                         = var.ami_id
  instance_type               = var.instance_type
  associate_public_ip_address = true
  vpc_security_group_ids      = [aws_security_group.virtual_environment_perses.id]
  key_name                    = var.key_name


  connection {
    type        = "ssh"
    host        = self.public_ip
    user        = var.ec2_username
    private_key = file(var.key_path)
    timeout     = "60m"
  }

  root_block_device {
    volume_size = var.volume 
  }

  provisioner "remote-exec" {
    inline = [
      "mkdir apk",
      "mkdir scripts",
      "mkdir docker",
      "mkdir pascal",
      "mkdir devices-logs",
      "mkdir devices-logs/espresso"
    ]
  }

  provisioner "file" {
    source      = "./scripts/"
    destination = "./scripts/"
  }

  provisioner "file" {
    source      = "./docker/"
    destination = "./docker/"
  }

  provisioner "file" {
    source      = "./pascal/"
    destination = "./pascal/"
  }

  provisioner "file" {
    source = var.directory_path
    destination = "./files/"
  }

  provisioner "remote-exec" {
    inline = [
      "bash ./scripts/connection.sh ${(var.key_path)} ${(var.ec2_username)} ${(self.public_ip)} ${(var.number_devices)}"]
  }

  provisioner "local-exec" {
    command = "scp -o StrictHostKeyChecking=no -i ${(var.key_path)} ${(var.ec2_username)}@${(self.public_ip)}:connection.txt connection.txt"
    
  }



  provisioner "remote-exec" {
    inline = [
      "sudo apt-get --assume-yes update",
      "sudo apt --assume-yes install docker.io",
      "sudo systemctl start docker",
      "sudo systemctl enable docker",
      "sudo apt-key adv --keyserver keyserver.ubuntu.com --recv-keys 21805A48E6CBBA6B991ABE76646193862B759810",
	    "sudo add-apt-repository ppa:katharaframework/kathara -y",
	    "sudo apt --assume-yes update",
	    "sudo apt --assume-yes install kathara",
      "sudo apt-get --assume-yes install jq",
      "sudo apt --assume-yes update",
      "sudo apt --assume-yes install xterm",
      "sudo apt-get --assume-yes install android-tools-adb android-tools-fastboot",
      "sudo apt  --assume-yes install curl",
      "sudo apt-get update && sudo apt-get install -y ca-certificates curl gnupg",
      "sudo mkdir -p /etc/apt/keyrings",
      "sudo apt-get -y install gpg",
      "curl -fsSL https://deb.nodesource.com/gpgkey/nodesource-repo.gpg.key | sudo gpg --dearmor -o /etc/apt/keyrings/nodesource.gpg",
      "echo 'deb [signed-by=/etc/apt/keyrings/nodesource.gpg] https://deb.nodesource.com/node_16.x nodistro main' | sudo tee /etc/apt/sources.list.d/nodesource.list",
      "sudo apt-get update && sudo apt-get install nodejs -y",
      "sudo apt-get install python3.10 pyyaml",
      "cd scripts",
      "sudo chmod 777 *",
      "npm install"
    ]
  }



    provisioner "remote-exec" {
    inline = [
      "sudo bash ./scripts/archBuilder.sh",
      "sudo docker system prune -f",
      "sudo ufw disable",
      "sudo bash ./scripts/startVE.sh",
      "sudo bash ./scripts/apkManager.sh ${(var.number_mobiles)} ${(var.mobile_devices)} ${(var.apk_path)} ${(var.apk_test_path)} ${(var.time_wait)} ${(var.idle_time)}"
    ]
  }

  tags = {
    Name = var.project_name
  }
}
