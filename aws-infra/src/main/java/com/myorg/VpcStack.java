package com.myorg;

import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.Vpc;

public class VpcStack extends Stack {
    public VpcStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public VpcStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        Vpc vpc = Vpc.Builder.create(this, "Vpc-microsservice")
                .maxAzs(3)
                .build();

        // Cluster cluster = Cluster.Builder.create(this, "MyCluster")
        // .vpc(vpc).build();

        // // Create a load-balanced Fargate service and make it public
        // ApplicationLoadBalancedFargateService.Builder.create(this,
        // "MyFargateService")
        // .cluster(cluster) // Required
        // .cpu(512) // Default is 256
        // .desiredCount(6) // Default is 1
        // .taskImageOptions(
        // ApplicationLoadBalancedTaskImageOptions.builder()
        // .image(ContainerImage.fromRegistry("amazon/amazon-ecs-sample"))
        // .build())
        // .memoryLimitMiB(2048) // Default is 512
        // .publicLoadBalancer(true) // Default is true
        // .build();
    }
}
