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
