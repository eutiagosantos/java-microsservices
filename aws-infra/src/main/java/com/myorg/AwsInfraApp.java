package com.myorg;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

public class AwsInfraApp {
        public static void main(final String[] args) {
                App app = new App();

                Environment env = Environment.builder()
                                .account(System.getenv("CDK_DEFAULT_ACCOUNT"))
                                .region(System.getenv("CDK_DEFAULT_REGION"))
                                .build();

                var vpcStack = new VpcStack(app, "vpc-ms", StackProps.builder()
                                .env(env)
                                .build());

                var clusterStack = new ClusterStack(app, "cluster-ms", StackProps.builder()
                                .env(env)
                                .build(), vpcStack.getVpc());
                clusterStack.addDependency(vpcStack);
                var rdsStack = new RdsStack(app, "RDS", StackProps.builder()
                                .env(env)
                                .build(), vpcStack.getVpc());
                rdsStack.addDependency(vpcStack);
                var serviceStack = new ServiceStack(app, "service", StackProps.builder()
                                .env(env)
                                .build(), clusterStack.getCluster());
                serviceStack.addDependency(clusterStack);
                serviceStack.addDependency(rdsStack);
                app.synth();
        }
}
