# CASS: Continuum Architecture Simulation Service

## Overview

CASS (Continuum Architecture Simulation Service) is a sophisticated simulation tool designed to evaluate the Quality of Service (QoS) of applications within Cloud Continuum architectures. Offered as Software as a Service (SaaS), CASS facilitates the deployment of virtualized, highly customizable environments for running comprehensive tests and simulations. By leveraging CASS, users can simulate real-world conditions to assess the performance and reliability of their IoT applications before deployment in production environments.

## Repository Structure

This repository contains the following main directories and files:

 - **case_study**: This folder includes an illustrative case study focused on air quality measurement in a smart city environment. The case study demonstrates the practical application of CASS and provides detailed examples of configurations and test results.
 - **documentation**: This folder includes comprehensive documentation covering the usage of CASS through the API.
 - **src**: This folder includes the source code for the microservices that constitute CASS. This includes all necessary components for managing project creation, execution, data retrieval, and integration with continuous integration workflows.
 - **LICENSE**: [CC BY-NC 4.0](https://creativecommons.org/licenses/by-nc/4.0/legalcode.en)
 - **README**: this file.

## Microservices

### Core

The Core microservice is the central component that orchestrates the entire simulation process. It oversees the execution stages, manages user validation, sets up the environment, and handles the main workflow logic. The Core ensures that each phase of the simulation, from initialization to log collection, is executed seamlessly.

### API

The API microservice provides the interface through which users interact with CASS. It includes endpoints for creating and launching projects, retrieving results, and accessing specific data details. The API is designed to be user-friendly, enabling efficient management and evaluation of simulation projects without requiring deep technical expertise.

### Project Manager

The Project Manager microservice is responsible for managing project creation and configuration. It acts as an intermediary between the user, the Core, and the Database. The Project Manager simplifies the process of project setup by handling configuration files and ensuring that all necessary project information is accurately recorded and accessible.

### Database

The Database microservice stores all relevant data related to CASS. It maintains a structured record of all projects and their associated executions, enabling easy retrieval and management of important data. The Database ensures data integrity and accessibility throughout the simulation lifecycle.

## Requirements

For using CASS, you need to register in order to get a username and API key. To register, send an email to slasom@unex.es.

## Contact and Support

For questions or support, please contact Sergio Laso at slasom@unex.es.