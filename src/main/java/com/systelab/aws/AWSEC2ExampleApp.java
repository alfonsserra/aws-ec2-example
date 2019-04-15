package com.systelab.aws;

public class AWSEC2ExampleApp {

    public static void main(String[] args) {
        EC2Service service = new EC2Service();
        service.getInstances();
        //  service.createInstance("example", AMI.AMAZON_LINUX2_AMI, InstanceType.T2_MICRO);
        VPCService service2 = new VPCService();
        service2.getVPCs();
    }
}
