package com.systelab.aws;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

public class AWSEC2ExampleApp {

    private Region region = Region.EU_CENTRAL_1;
    private Ec2Client ec2 = Ec2Client.builder().region(region).build();

    private void printInstance(Instance instance) {
        System.out.printf(
                "Found reservation with id %s, " +
                        "AMI %s, " +
                        "type %s, " +
                        "state %s " +
                        "and monitoring state %s\n",
                instance.instanceId(),
                instance.imageId(),
                instance.instanceType(),
                instance.state().name(),
                instance.monitoring().state());
    }

    private void printReservation(Reservation reservation) {
        reservation.instances().forEach(this::printInstance);
    }

    public void getInstances() {
        String nextToken = null;
        do {
            DescribeInstancesRequest request = DescribeInstancesRequest.builder().maxResults(6).nextToken(nextToken).build();
            DescribeInstancesResponse response = ec2.describeInstances(request);
            response.reservations().forEach(this::printReservation);
            nextToken = response.nextToken();
        } while (nextToken != null);
    }

    public String createInstance(String name, AMI ami) throws Ec2Exception {
        return this.createInstance(name, ami, InstanceType.T2_SMALL);
    }

    public String createInstance(String name, AMI ami, InstanceType type) throws Ec2Exception {
        RunInstancesRequest request = RunInstancesRequest.builder()
                .imageId(ami.toString())
                .instanceType(type)
                .maxCount(1)
                .minCount(1)
                .build();

        RunInstancesResponse response = ec2.runInstances(request);

        String instanceId = response.instances().get(0).instanceId();

        addTagsToInstance("Name", name, instanceId);

        System.out.printf("Successfully started EC2 instance %s based on AMI %s", instanceId, ami.toString());
        return instanceId;
    }

    public void startInstance(String instanceId) {
        StartInstancesRequest request = StartInstancesRequest.builder()
                .instanceIds(instanceId).build();
        ec2.startInstances(request);
    }

    public void rebootInstance(String instanceId) {
        RebootInstancesRequest request = RebootInstancesRequest.builder()
                .instanceIds(instanceId).build();
        RebootInstancesResponse response = ec2.rebootInstances(request);
    }

    public void stopInstance(String instanceId) {
        StopInstancesRequest request = StopInstancesRequest.builder()
                .instanceIds(instanceId).build();
        ec2.stopInstances(request);
    }

    private void addTagsToInstance(String key, String value, String... instances) {
        Tag tag = Tag.builder()
                .key(key)
                .value(value)
                .build();

        CreateTagsRequest tagsRequest = CreateTagsRequest.builder()
                .resources(instances)
                .tags(tag)
                .build();

        ec2.createTags(tagsRequest);
    }

    public static void main(String[] args) {
        AWSEC2ExampleApp example = new AWSEC2ExampleApp();
        example.getInstances();
        example.createInstance("example", AMI.AMAZON_LINUX2_AMI, InstanceType.T2_MICRO);
    }
}
