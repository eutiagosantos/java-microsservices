# AWS CDK Infrastructure - Microsserviços

Projeto de infraestrutura AWS utilizando CDK (Cloud Development Kit) para deploy de microsserviços em Java.

## Estrutura do Projeto

O projeto está organizado em 4 stacks principais:

- **VpcStack**: Configuração da Virtual Private Cloud
- **ClusterStack**: Cluster ECS Fargate
- **RdsStack**: Bancos de dados RDS PostgreSQL para os microsserviços
- **ServiceStack**: Serviços Fargate com Load Balancer

## Microsserviços Deployados

### 1. Pedidos (Orders)
- Imagem Docker: `ctiagosantos/order-ms`
- Porta: 8080
- Database: `pedidos-db`

### 2. Pagamentos (Payments)
- Imagem Docker: `ctiagosantos/payment-ms`
- Porta: 8080
- Database: `pagamentos-db`

## Uso de Imagens Docker

### Repositórios Públicos (Docker Hub)

Atualmente, o projeto utiliza imagens públicas do Docker Hub com `ContainerImage.fromRegistry()`:

```java
.image(ContainerImage.fromRegistry("ctiagosantos/order-ms"))
```

### Repositórios Privados (AWS ECR)

**Se você estiver utilizando repositórios privados**, é recomendado usar o Amazon ECR (Elastic Container Registry). Para isso:

1. **Importe o repositório ECR no `ServiceStack`**:

```java
import software.amazon.awscdk.services.ecr.IRepository;
import software.amazon.awscdk.services.ecr.Repository;
```

2. **Referencie o repositório ECR**:

```java
IRepository repository = Repository.fromRepositoryName(this, "OrderMsRepo", "order-ms");
```

3. **Utilize `fromEcrRepository()` ao invés de `fromRegistry()`**:

```java
.image(ContainerImage.fromEcrRepository(repository, "latest"))
```

#### Exemplo Completo:

```java

IRepository orderRepository = Repository.fromRepositoryName(this, "OrderMsRepo", "order-ms");

ApplicationLoadBalancedFargateService.Builder.create(this, "Fargate-ms-pedidos")
    .cluster(cluster)
    .cpu(256)
    .desiredCount(6)
    .listenerPort(8080)
    .assignPublicIp(true)
    .taskImageOptions(
        ApplicationLoadBalancedTaskImageOptions.builder()
            .environment(autenticate)
            .image(ContainerImage.fromEcrRepository(orderRepository, "latest"))
            .containerPort(8080)
            .build())
    .memoryLimitMiB(512)
    .publicLoadBalancer(true)
    .build();
```

## Comandos Úteis

 * `mvn package`     compilar e executar testes
 * `cdk ls`          listar todas as stacks da aplicação
 * `cdk synth`       gerar template CloudFormation sintetizado
 * `cdk deploy`      fazer deploy desta stack na sua conta/região AWS padrão
 * `cdk diff`        comparar stack deployada com estado atual
 * `cdk docs`        abrir documentação do CDK

## Deploy

Para fazer deploy de todas as stacks:

```bash
cdk deploy --all
```

Para fazer deploy de uma stack específica:

```bash
cdk deploy ServiceStack
```
