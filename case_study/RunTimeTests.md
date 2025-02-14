
# Evaluating CCSIM runtime

Scalability in large-scale simulation environments is essential for evaluating complex distributed architectures. Beyond the ability to support high-demand scenarios, it is crucial to maintain stable execution times to optimize the developer's experience. Consistent runtimes allow developers to plan their workflows more effectively, ensuring predictable performance when scaling applications. This is particularly important in continuous integration and testing pipelines, where unpredictable simulation times can hinder automation and delay software releases.

## Experiment Setup

To validate CCSIM’s scalability, we conducted a series of experiments measuring execution times across different pricing plans using the Air Quality App. Each experiment was repeated **five times per plan** to ensure reliability and consistency in the results. The tests were designed to simulate the worst-case scenario, deploying the maximum number of devices supported by each plan. Specifically, each test involved the deployment of:

### Basic Plan (vCPU: 96 and RAM: 192 GB)
- **X** switches
- **X** microservices
- **X** mobile devices
- **X** edge nodes
- **X** cloud instances

### Pro Plan (vCPU: 128 and RAM: 512 GB)
- **X** switches
- **X** microservices
- **X** mobile devices
- **X** edge nodes
- **X** cloud instances

### Enterprise Plan (vCPU: 192 and RAM: 768 GB)
- **X** switches
- **X** microservices
- **X** mobile devices
- **X** edge nodes
- **X** cloud instances

Each experiment evaluated the full CCSIM workflow, which includes **Initialization, Deployment, Execution, and Log Collection** stages.

## Results and Analysis

| Pricing Plan  | Initialization | Deployment | Execution | Log Collection | Total Runtime (avg) |
|--------------|---------------|------------|-----------|---------------|------------------|
| Basic       | 1 min         | 25 min     | 1 min     | 1 min         | **28 min**       |
| Pro         | 1 min         | 31 min     | 1.5 min   | 1 min         | **34.5 min**     |
| Enterprise  | 1 min         | 35 min     | 2.3 min   | 1.3 min       | **39.6 min**     |

### Key Observations

- **Deployment Stage Dominance**: The deployment phase accounts for approximately **82% of the total runtime**, primarily due to the time required for AWS EC2 instance provisioning and container deployment. Although higher pricing plans provide more resources, the virtualization tools used are not fully optimized for scaling large deployments, leading to increased deployment times as the number of devices grows.

- **Consistency Across Experiments**: Despite differences in plan configurations, CCSIM maintained simulation runtimes within the range of **28–40 minutes**, demonstrating predictable performance at scale.

## Importance of Consistency for Developers

For developers integrating CCSIM into their workflows, predictable and stable simulation runtimes offer several advantages:

- **Reliable Testing Pipelines**: Ensuring that tests complete within expected time frames allows seamless integration with CI/CD processes.
- **Scalable Evaluation**: Developers can confidently scale their simulations without facing unpredictable delays.

This validation confirms that CCSIM is capable of supporting large-scale simulations while maintaining execution times that remain within a reasonable and predictable range.
