# Microsserviços

Projeto desenvolvido para estudo de arquitetura de microsserviços com ecossistema Spring.

## 🚀 Tecnologias e Aprendizados

*   **Spring Boot**: Base dos microsserviços.
*   **Service Discovery**: Utilização do **Eureka Server** para registro e descoberta de serviços.
*   **API Gateway**: Implementação de gateway para roteamento centralizado.
*   **Comunicação Síncrona**: Uso de **OpenFeign** para comunicação REST entre microsserviços (ex: Pagamentos notificando Pedidos).
*   **Resiliência**: Implementação de **Circuit Breaker** com **Resilience4j** para tratamento de falhas e métodos de *fallback*, garantindo que o sistema degrade graciosamente quando serviços dependentes estão indisponíveis.
*   **Banco de Dados**: Persistência com PostgreSQL e JPA.
*   **Migrações**: Versionamento de banco de dados com **Flyway**.
*   **Load Balancer**: Balanceamento de carga no lado do cliente.

## 📦 Estrutura do Projeto

*   `server`: Servidor de descoberta (Eureka).
*   `gateway`: Gateway de entrada da aplicação.
*   `pagamentos`: Microsserviço responsável pelo processamento de pagamentos. Implementa padrões de resiliência.
*   `pedidos`: Microsserviço de gestão de pedidos.
*   `aws-infra`: Infraestrutura como código (IaC) usando AWS CDK para deployment na AWS.

## ☁️ Infraestrutura AWS (CDK)

O projeto possui uma infraestrutura completa na AWS gerenciada via CDK (Cloud Development Kit), localizada em `aws-infra/`.

### Stacks Implementadas

*   **VpcStack**: Configuração da Virtual Private Cloud para isolamento de rede
*   **ClusterStack**: Cluster ECS Fargate para execução dos containers
*   **RdsStack**: Bancos de dados RDS PostgreSQL dedicados para cada microsserviço
*   **ServiceStack**: Deployment dos serviços com Application Load Balancer

### Recursos Configurados

#### 🐳 Deployment dos Microsserviços

*   **Pedidos (Orders)**: Imagem `ctiagosantos/order-ms` deployada no ECS Fargate
*   **Pagamentos (Payments)**: Imagem `ctiagosantos/payment-ms` deployada no ECS Fargate

Ambos executando com:
*   CPU: 256 units
*   Memória: 512 MiB
*   Porta: 8080
*   Desired Count inicial: 3 tarefas

#### 📊 CloudWatch Logs

Logs centralizados configurados para cada microsserviço:
*   **Log Group**: `PedidosMsLog` e `PaymentsMsLog`
*   **Retention**: 1 semana
*   **Stream Prefix**: `PedidosMS` e `PaymentsMS`
*   **Removal Policy**: Destroy (para ambientes de desenvolvimento)

#### 🔄 Auto Scaling

Auto Scaling configurado para ambos os microsserviços com:

**Capacidade:**
*   Mínimo: 1 tarefa
*   Máximo: 20 tarefas

**Métricas de Scaling:**
*   **CPU**: Target 70% de utilização
  *   Scale Out Cooldown: 2 minutos
  *   Scale In Cooldown: 3 minutos
*   **Memória**: Target 65% de utilização
  *   Scale Out Cooldown: 2 minutos
  *   Scale In Cooldown: 3 minutos

#### 🗄️ Banco de Dados

*   **PostgreSQL (RDS)** com bancos dedicados:
  *   `pedidos-db` para o serviço de Pedidos
  *   `pagamentos-db` para o serviço de Pagamentos
*   Conexões configuradas via variáveis de ambiente injetadas nas tarefas ECS

### Deploy da Infraestrutura

```bash
cd aws-infra
cdk deploy --all
```

Para mais detalhes, consulte [aws-infra/README.md](file:///Users/tiagosantos/Documents/microsservices/aws-infra/README.md)
