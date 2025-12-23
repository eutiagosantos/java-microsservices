package com.myorg;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

public class AwsInfraApp {
        public static void main(final String[] args) {
                App app = new App();

                var vpcStack = new VpcStack(app, "vpc-ms", StackProps.builder()
                                .env(Environment.builder()
                                                .account(System.getenv("CDK_DEFAULT_ACCOUNT"))
                                                .region(System.getenv("CDK_DEFAULT_REGION"))
                                                .build())
                                .build());

                var clusterStack = new ClusterStack(app, "cluster-ms", vpcStack.getVpc());
                clusterStack.addDependency(vpcStack);
                var serviceStack = new ServiceStack(app, "service", clusterStack.getCluster());
                serviceStack.addDependency(clusterStack);
                app.synth();
        }
}
