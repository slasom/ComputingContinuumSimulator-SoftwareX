## Verification of .metal instance creation

Perses  requires  AWS ``.metal`` instances that allow nested virtualization, however they correspond with large instances and AWS usually define a maximum threshold (typically 32 vCPU) by default to the number of vCPUs the usuer could request and therefore ``.metal`` instances (starting at 96 CPU) are disabled in the default configuration.

In order to overcome this limitation, the user should require to enable them to AWS by increasing the vCPUs limit to, at least, 96 using the AWS form in http://aws.amazon.com/contact-us/ec2-request **and specifying an appropriate Use case description**;  as an example:
 
        We need to increase the VCPU limitation in order to use, at least, "C5.metal" instances 
        (with 96 vCPUs) to apply the Perses Framework for Distributed Application Performance 
        Testing. This framework requires ".metal" instances that allow nested virtualization 
        based on kvm x86 in order to generate the virtual android devices that conform 
        the testing scenario.
 
