# CCSIM: A Cloud Continuum Simulator as a Service

## Overview

CCSIM (A Cloud Continuum Simulator as a Service) is a sophisticated simulation tool designed to evaluate the Quality of Service (QoS) of applications within Cloud Continuum architectures. Offered as Software as a Service (SaaS), CCSIM facilitates the deployment of virtualized, highly customizable environments for running comprehensive tests and simulations. By leveraging CCSIM, users can simulate real-world conditions to assess the performance and reliability of their IoT applications before deployment in production environments.

## Repository Structure

This repository contains the following main directories and files:

 - **case_study**: This folder includes an illustrative case study focused on air quality measurement in a smart city environment. The case study demonstrates the practical application of CCSIM and provides detailed examples of configurations and test results.
 - **documentation**: This folder includes comprehensive documentation covering the usage of CCSIM through the API.
 - **src**: This folder includes the source code for the microservices that constitute CCSIM. This includes all necessary components for managing project creation, execution, data retrieval, and integration with continuous integration workflows.
 - **LICENSE**: [CC BY-NC 4.0](https://creativecommons.org/licenses/by-nc/4.0/legalcode.en)
 - **README**: this file.

## Microservices

### Core

The Core microservice is the central component that orchestrates the entire simulation process. It oversees the execution stages, manages user validation, sets up the environment, and handles the main workflow logic. The Core ensures that each phase of the simulation, from initialization to log collection, is executed seamlessly.

### API

The API microservice provides the interface through which users interact with CCSIM. It includes endpoints for creating and launching projects, retrieving results, and accessing specific data details. The API is designed to be user-friendly, enabling efficient management and evaluation of simulation projects without requiring deep technical expertise.

### Project Manager

The Project Manager microservice is responsible for managing project creation and configuration. It acts as an intermediary between the user, the Core, and the Database. The Project Manager simplifies the process of project setup by handling configuration files and ensuring that all necessary project information is accurately recorded and accessible.

### Database

The Database microservice stores all relevant data related to CCSIM. It maintains a structured record of all projects and their associated executions, enabling easy retrieval and management of important data. The Database ensures data integrity and accessibility throughout the simulation lifecycle.

## Installation & Deployment Guide

### Prerequisites

- AWS Account
- Node.js (LTS version recommended)
- MySQL 8.0 or higher
- Python 3.5.2
- npm (comes with Node.js)
- pip3 (Python package manager)

### Verification of .metal AWS EC2 instance creation

CCSIM  requires  AWS ``.metal`` instances that allow nested virtualization, however they correspond with large instances and AWS usually define a maximum threshold (typically 32 vCPU) by default to the number of vCPUs the usuer could request and therefore ``.metal`` instances (starting at 96 CPU) are disabled in the default configuration.

In order to overcome this limitation, the user should require to enable them to AWS by increasing the vCPUs limit to, at least, 96 using the AWS form in http://aws.amazon.com/contact-us/ec2-request **and specifying an appropriate Use case description**;  as an example:
 
        We need to increase the VCPU limitation in order to use, at least, "C5.metal" instances 
        (with 96 vCPUs) to apply the Perses Framework for Distributed Application Performance 
        Testing. This framework requires ".metal" instances that allow nested virtualization 
        based on kvm x86 in order to generate the virtual android devices that conform 
        the testing scenario.

### API Keys Setup

At `src/CCSIM-CORE/s3Operations.js`, set up your S3 keys:

```javascript
// This is for creating the S3 object
const s3 = new AWS.S3({
    accessKeyId: "" /*  YOUR IAM USER KEY HERE */,
    secretAccessKey:
        "" /* YOUR IAM USER SECRET KEY HERE */,
    region: "" /* YOUR AWS REGION NAME HERE */,
    Bucket: "" /* YOUR AWS BUCKET NAME HERE */,
});
```

Do the same for `src/CCSIM-Project-Manager/service/UploadService.js`:

```javascript
// S3 constants definition
const s3 = new AWS.S3({
  accessKeyId: "",  /* required  # Put your iam user key */
  secretAccessKey: "", /* required   # Put your iam user secret key */
  region: "", /* required   # Put your region name */
  Bucket: ""     /* required      # Put your bucket name */
});
```

Add your RSA Private Key in a file named `src/CCSIM-CORE/test/terraform.pem`.

### 1. Database Setup

First, create a MySQL database named "perses" and set up the required tables:

```sql
-- Users Table
CREATE TABLE `users` (
  `name` varchar(255) DEFAULT NULL,
  `last_name_1` varchar(255) DEFAULT NULL,
  `last_name_2` varchar(255) DEFAULT NULL,
  `nid` varchar(255) DEFAULT NULL,
  `birth_date` date DEFAULT NULL,
  `user_name` varchar(255) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `api_key` varchar(255) DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Projects Table
CREATE TABLE `projects` (
  `project_name` varchar(255) NOT NULL,
  `user_name` varchar(255) DEFAULT NULL,
  `json_config` varchar(255) DEFAULT NULL,
  `apk_file` varchar(255) DEFAULT NULL,
  `apk_test_file` varchar(255) DEFAULT NULL,
  `creation_date` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`project_name`),
  KEY `user_name` (`user_name`),
  CONSTRAINT `projects_ibfk_1` FOREIGN KEY (`user_name`) REFERENCES `users` (`user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Executions Table
CREATE TABLE `executions` (
  `execution_name` varchar(255) NOT NULL,
  `project_name` varchar(255) DEFAULT NULL,
  `ms_start_execution` int DEFAULT NULL,
  `ms_end_execution` int DEFAULT NULL,
  `devices_logs` varchar(255) DEFAULT NULL,
  `associated_cost` float(9,2) DEFAULT NULL,
  `execution_finished` tinyint(1) DEFAULT NULL,
  `execution_state` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`execution_name`),
  KEY `project_name` (`project_name`),
  CONSTRAINT `executions_ibfk_1` FOREIGN KEY (`project_name`) REFERENCES `projects` (`project_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

After creating the database and tables, deploy the database API:

```bash
cd src/CCSIM-DB
npm install
npm run start
```

The database API will be available at `http://localhost:8081`

### 2. CCSIM Core

Deploy the core component:

```bash
cd src/CCSIM-CORE
npm install
npm run start
```

The core service will be available at `http://localhost:8080`

### 3. Project Manager

Deploy the project manager:

```bash
cd src/CCSIM-Project-Manager
npm install
npm run start
```

The project manager will be available at `http://localhost:8082`

### 4. CCSIM API

#### Installation

```bash
cd src/CCSIM-API
pip3 install -r requirements.txt
```

#### Running the Server

```bash
python3 -m swagger_server
```

The API will be available at:
- Swagger UI: `http://localhost:8084/ui/`
- Swagger JSON: `http://localhost:8084/swagger.json`

#### Running Tests

To run integration tests:

```bash
pip install tox
tox
```

## Verification

To verify your installation:

1. Check that all services are running on their respective ports:
   - Database API: Port 8081
   - CCSIM Core: Port 8080
   - Project Manager: Port 8082
   - CCSIM API: Port 8084

2. Access the Swagger UI at `http://localhost:8084/ui/` to test the API endpoints

## Troubleshooting

Common issues and their solutions:

- Port conflicts: Ensure no other services are running on ports 8080, 8081, 8082 and 8084
- Database connection issues: Verify MySQL is running and the "perses" database exists
- Missing dependencies: Run `npm install` or `pip3 install -r requirements.txt` again

## Requirements

For using CCSIM, you need to register in order to get a username and API key. To register, send an email to slasom@unex.es.

## Contact and Support

For questions or support, please contact Sergio Laso at slasom@unex.es.
