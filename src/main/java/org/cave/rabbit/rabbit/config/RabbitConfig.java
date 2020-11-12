package org.cave.rabbit.rabbit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;


/**
 * Essa é a primeira classe que devemos criar e dar atenção quando pensamos em um serviço que usa RabbitMQ.
 * <p>
 * Essa configuração não precisa estar centralizada aqui. Algumas pessoas gostam quebrar ela em diversas configs.
 */
@Data
@EnableRabbit // Habilita a detecção das anotações @RabbitListener
@Configuration
public class RabbitConfig implements RabbitListenerConfigurer {

    /**
     * Esses valores do .properties/.yml, por favor
     */
    private String addresses = "localhost";
    private String username = "use-exemplo";
    private String password = "C0nnect123";
    private String virtualHost = "para-exemplo";

    /**
     * Dados da conexão etc...
     *
     * @return ConnectionFactory
     */
    @Bean
    @Primary
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses(addresses);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualHost);
        return connectionFactory;
    }

    /**
     * RabbitAdmin, como o nome diz, é um Bean com funcionalidades administrativas do RabbitMQ.
     * <p>
     * Criamos esse Bean, passando as informações da conexão e principalmente o ApplicationContext.
     * <p>
     * As Filas e outros Knowlege Objects do Rabbit, caso não existam, sao criados durante o POST de uma mensagem.
     * Porém, não é possível criar esses KOs com consumidores, a menos que se invoque método #initialize do RabbitAdmin.
     * <p>
     * Uma forma de fazer isso é invocando manualmente o initialize dentro da classe Main do micro serviço.
     * Outra forma é declarando o bean para que receba o Application Context, assim ele participa do contexto de startup da aplicação
     * e invoca o #initialize automáticamente.
     *
     * @param applicationContext ApplicationContext do Spring
     * @param connectionFactory  #{@link #connectionFactory()}
     * @return RabbitAdmin
     */
    @Bean
    @Primary
    public RabbitAdmin rabbitAdmin(ApplicationContext applicationContext, ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setApplicationContext(applicationContext);
        rabbitAdmin.afterPropertiesSet();
        return rabbitAdmin;
    }

    /**
     * Bean do RabbitTemplate.
     * <p>
     * Usado para POSTAR mensagens na fila.
     * Aqui usamos para fins de retentativas.
     *
     * @param connectionFactory {@link #connectionFactory()}
     * @return RabbitTemplate
     */
    @Bean
    @Primary
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }


    /**
     * {@link ObjectMapper}, para converter mensagens para objetos
     *
     * @return MappingJackson2MessageConverter usando {@link ObjectMapper}
     */
    @Bean
    public MappingJackson2MessageConverter jackson2Converter() {
        MappingJackson2MessageConverter conveter = new MappingJackson2MessageConverter();
        conveter.setObjectMapper(new ObjectMapper());
        return conveter;
    }

    /**
     * Existem N formas de usar RabbitMQ. A que usamos é a Annotation-Driven.
     * <p>
     * Resumidamente serve para lidar com os argumentos dos metodos anotados com @{@link org.springframework.amqp.rabbit.annotation.RabbitListener}
     * <p>
     * ref: https://docs.spring.io/spring-amqp/reference/pdf/spring-amqp-reference.pdf
     *
     * @return DefaultMessageHandlerMethodFactory
     */
    @Bean
    DefaultMessageHandlerMethodFactory handlerMethodFactory() {
        DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();
        factory.setMessageConverter(jackson2Converter());
        return factory;
    }

    /**
     * Adiciona o handler para @{@link org.springframework.amqp.rabbit.annotation.RabbitListener}
     *
     * @param register
     */
    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar register) {
        register.setMessageHandlerMethodFactory(handlerMethodFactory());
    }

}
