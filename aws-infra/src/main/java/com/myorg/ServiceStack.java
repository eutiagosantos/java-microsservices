package com.myorg;

import software.constructs.Construct;

import java.util.HashMap;
import java.util.Map;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.Fn;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.applicationautoscaling.EnableScalingProps;
import software.amazon.awscdk.services.ecs.AwsLogDriverProps;
import software.amazon.awscdk.services.ecs.Cluster;
import software.amazon.awscdk.services.ecs.ContainerImage;
import software.amazon.awscdk.services.ecs.CpuUtilizationScalingProps;
import software.amazon.awscdk.services.ecs.LogDriver;
import software.amazon.awscdk.services.ecs.MemoryUtilizationScalingProps;
import software.amazon.awscdk.services.ecs.ScalableTaskCount;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedTaskImageOptions;
import software.amazon.awscdk.services.logs.LogGroup;
import software.amazon.awscdk.services.logs.RetentionDays;

public class ServiceStack extends Stack {
        public ServiceStack(final Construct scope, final String id, final Cluster cluster) {
                this(scope, id, null, cluster);
        }

        public ServiceStack(final Construct scope, final String id, final StackProps props, final Cluster cluster) {
                super(scope, id, props);

                Map<String, String> autenticate = new HashMap<>();
                autenticate.put("SPRING_DATASOURCE_URL", "jdbc:postgresql://" + Fn.importValue("pedidos-db-endpoint")
                                + ":5432/pedidos-db?createDatabaseIfNotExist=true");

                autenticate.put("SPRING_DATASOURCE_USERNAME", "admin");
                autenticate.put("SPRING_DATASOURCE_PASSWORD", Fn.importValue("pedidos-db-senha"));

                LogGroup logGroupOrder = LogGroup.Builder.create(this, "PedidosMsLogGroup")
                                .logGroupName("PedidosMsLog")
                                .removalPolicy(RemovalPolicy.DESTROY)
                                .retention(RetentionDays.ONE_WEEK)
                                .build();
                LogGroup logGroupPayments = LogGroup.Builder.create(this, "PaymentsMsLogGroup")
                                .logGroupName("PaymentsMsLog")
                                .removalPolicy(RemovalPolicy.DESTROY)
                                .retention(RetentionDays.ONE_WEEK)
                                .build();
                ApplicationLoadBalancedFargateService orderBuild = ApplicationLoadBalancedFargateService.Builder
                                .create(this, "Fargate-ms-pedidos")
                                .cluster(cluster)
                                .cpu(256)
                                .desiredCount(3)
                                .listenerPort(8080)
                                .assignPublicIp(true)
                                .taskImageOptions(
                                                ApplicationLoadBalancedTaskImageOptions.builder()
                                                                .environment(autenticate)
                                                                .logDriver(LogDriver.awsLogs(AwsLogDriverProps.builder()
                                                                                .logGroup(logGroupOrder)
                                                                                .streamPrefix("PedidosMS")
                                                                                .build()))
                                                                .image(ContainerImage
                                                                                .fromRegistry("ctiagosantos/order-ms"))
                                                                .containerPort(8080)
                                                                .build())
                                .memoryLimitMiB(512)
                                .publicLoadBalancer(true)
                                .build();

                ScalableTaskCount scalableTargetOrder = orderBuild.getService()
                                .autoScaleTaskCount(EnableScalingProps.builder()
                                                .minCapacity(1)
                                                .maxCapacity(20)
                                                .build());
                scalableTargetOrder.scaleOnCpuUtilization("CpuScaling", CpuUtilizationScalingProps.builder()
                                .targetUtilizationPercent(70)
                                .scaleInCooldown(Duration.minutes(3))
                                .scaleOutCooldown(Duration.minutes(2))
                                .build());
                scalableTargetOrder.scaleOnMemoryUtilization("MemoryScaling", MemoryUtilizationScalingProps.builder()
                                .targetUtilizationPercent(65)
                                .scaleInCooldown(Duration.minutes(3))
                                .scaleOutCooldown(Duration.minutes(2))
                                .build());

                Map<String, String> autenticatePagamentos = new HashMap<>();
                autenticatePagamentos.put("SPRING_DATASOURCE_URL",
                                "jdbc:postgresql://" + Fn.importValue("pagamentos-db-endpoint")
                                                + ":5432/pagamentos-db?createDatabaseIfNotExist=true");

                autenticatePagamentos.put("SPRING_DATASOURCE_USERNAME", "admin");
                autenticatePagamentos.put("SPRING_DATASOURCE_PASSWORD", Fn.importValue("pagamentos-db-senha"));
                ApplicationLoadBalancedFargateService paymentsBuild = ApplicationLoadBalancedFargateService.Builder
                                .create(this, "Fargate-ms-pagamentos")
                                .cluster(cluster)
                                .cpu(256)
                                .desiredCount(3)
                                .listenerPort(8080)
                                .assignPublicIp(true)
                                .taskImageOptions(
                                                ApplicationLoadBalancedTaskImageOptions.builder()
                                                                .environment(autenticatePagamentos)
                                                                .logDriver(LogDriver.awsLogs(AwsLogDriverProps.builder()
                                                                                .logGroup(logGroupPayments)
                                                                                .streamPrefix("PaymentsMS")
                                                                                .build()))
                                                                .image(ContainerImage.fromRegistry(
                                                                                "ctiagosantos/payment-ms"))
                                                                .containerPort(8080)
                                                                .build())
                                .memoryLimitMiB(512)
                                .publicLoadBalancer(true)
                                .build();

                ScalableTaskCount scalableTargetPayments = paymentsBuild.getService()
                                .autoScaleTaskCount(EnableScalingProps.builder()
                                                .minCapacity(1)
                                                .maxCapacity(20)
                                                .build());
                scalableTargetPayments.scaleOnCpuUtilization("CpuScaling", CpuUtilizationScalingProps.builder()
                                .targetUtilizationPercent(70)
                                .scaleInCooldown(Duration.minutes(3))
                                .scaleOutCooldown(Duration.minutes(2))
                                .build());
                scalableTargetPayments.scaleOnMemoryUtilization("MemoryScaling", MemoryUtilizationScalingProps.builder()
                                .targetUtilizationPercent(65)
                                .scaleInCooldown(Duration.minutes(3))
                                .scaleOutCooldown(Duration.minutes(2))
                                .build());
        }
}
