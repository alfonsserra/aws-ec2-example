package com.systelab.aws;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.Reservation;

public class AWSEC2ExampleApp {
    private Region region = Region.EU_CENTRAL_1;
    private Ec2Client ec2 = Ec2Client.builder().region(region).build();

    public void getInstances() {
        String nextToken = null;
        do {
            DescribeInstancesRequest request = DescribeInstancesRequest.builder().maxResults(6).nextToken(nextToken).build();
            DescribeInstancesResponse response = ec2.describeInstances(request);

            for (Reservation reservation : response.reservations()) {
                for (Instance instance : reservation.instances()) {
                    System.out.printf(
                            "Found reservation with id %s, " +
                                    "AMI %s, " +
                                    "type %s, " +
                                    "state %s " +
                                    "and monitoring state %s",
                            instance.instanceId(),
                            instance.imageId(),
                            instance.instanceType(),
                            instance.state().name(),
                            instance.monitoring().state());
                    System.out.println("");
                }
            }
            nextToken = response.nextToken();


        } while (nextToken != null);


    }

    public static void main(String[] args) {
        AWSEC2ExampleApp example=new AWSEC2ExampleApp();
        example.getInstances();
    }
}
