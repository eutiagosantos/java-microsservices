package com.myorg;

import software.constructs.Construct;

import java.util.HashMap;
import java.util.Map;

import software.amazon.awscdk.Fn;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ecs.Cluster;
import software.amazon.awscdk.services.ecs.ContainerImage;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedTaskImageOptions;

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
        ApplicationLoadBalancedFargateService.Builder.create(this, "Fargate-ms-pedidos")
                .cluster(cluster)
                .cpu(256)
                .desiredCount(6)
                .listenerPort(8080)
                .assignPublicIp(true)
                .taskImageOptions(
                        ApplicationLoadBalancedTaskImageOptions.builder()
                                .environment(autenticate)
                                .image(ContainerImage.fromRegistry("ctiagosantos/order-ms"))
                                .containerPort(8080)
                                .build())
                .memoryLimitMiB(512)
                .publicLoadBalancer(true)
                .build();

        Map<String, String> autenticatePagamentos = new HashMap<>();
        autenticatePagamentos.put("SPRING_DATASOURCE_URL",
                "jdbc:postgresql://" + Fn.importValue("pagamentos-db-endpoint")
                        + ":5432/pagamentos-db?createDatabaseIfNotExist=true");

        autenticatePagamentos.put("SPRING_DATASOURCE_USERNAME", "admin");
        autenticatePagamentos.put("SPRING_DATASOURCE_PASSWORD", Fn.importValue("pagamentos-db-senha"));
        ApplicationLoadBalancedFargateService.Builder.create(this, "Fargate-ms-pagamentos")
                .cluster(cluster)
                .cpu(256)
                .desiredCount(6)
                .listenerPort(8080)
                .assignPublicIp(true)
                .taskImageOptions(
                        ApplicationLoadBalancedTaskImageOptions.builder()
                                .environment(autenticatePagamentos)
                                .image(ContainerImage.fromRegistry("ctiagosantos/payment-ms"))
                                .containerPort(8080)
                                .build())
                .memoryLimitMiB(512)
                .publicLoadBalancer(true)
                .build();
    }
}
