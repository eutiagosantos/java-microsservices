package com.myorg;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.ecs.Cluster;
import software.constructs.Construct;

public class ClusterStack extends Stack {

    private Cluster cluster;

    public ClusterStack(final Construct scope, final String id, final Vpc vpc) {
        this(scope, id, null, vpc);
    }

    public ClusterStack(final Construct scope, final String id, final StackProps props, final Vpc vpc) {
        super(scope, id, props);

        this.cluster = Cluster.Builder.create(this, "MyCluster")
                .vpc(vpc).build();

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

    public Cluster getCluster() {
        return cluster;
    }
}
