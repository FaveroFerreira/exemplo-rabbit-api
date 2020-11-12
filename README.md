# Exemplo Rabbit API

Serviço exemplificando e documentando passo a passo das configurações necessárias para
iniciar um desenvolvimento sustentável com RabbitMQ.

## Dependências (Principais)

1. Java 8
2. Docker
3. RabbitMQ

## Para subir uma instância do RabbitMQ

Execute o comando:
```
$ docker run -d -p 5672:5672 -p 15672:15672 --name rabbit rabbitmq:3-management
```
Acesse: http://localhost:15672/

Usuário: guest

Senha: guest

## Configurações

Configurações básicas para usar RabbitMQ + Springboot:
```
RabbitConfig.java
```
Configurar todo um flow de Queues, Exchanges e Bindings (consumindo ou postando):
```
NomeDoProcessoConfig.java
```
Customizações específica para @RabbitListeners:
```
RabbitContainerFactoryConfig.java
```
Constantes, por favor, use constantes:
```
RabbitQueueConstants.java
```