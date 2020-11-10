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
 *
 * Essa configuração não precisa estar centralizada aqui. Algumas pessoas gostam quebrar ela em diversas configs.
 *
 */
@Data
@EnableRabbit
@Configuration
public class RabbitConfig implements RabbitListenerConfigurer {

    private String addresses;
    private String username;
    private String password;
    private String virtualHost;

    /**
     * Dados da conexão etc...
     *
     * @return
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
     * Porém, não é possível criar esses KOs com consumidores, a menos que seja invocado ométodo #initialize do RabbitAdmin.
     * <p>
     * Uma forma de fazer isso é invocando manualmente o initialize dentro da classe Main do micro serviço.
     * Outra forma é declarando o bean para que receba o Application Context, assim ele participa do contexto de startup da aplicação
     * e invoca o #initialize automáticamente.
     *
     * @param applicationContext
     * @param connectionFactory
     * @return
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
     * @param connectionFactory
     * @return
     */
    @Bean
    @Primary
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }


    /**
     * ObjectMapper pra converter mensagens
     *
     * @return
     */
    @Bean
    public MappingJackson2MessageConverter jackson2Converter() {
        MappingJackson2MessageConverter conveter = new MappingJackson2MessageConverter();
        conveter.setObjectMapper(new ObjectMapper());
        return conveter;
    }

    /**
     * Existem N formas de usar RabbitMQ. A que usamos é a Annotation-Driven.
     *
     * Para garantir o funcionamento correto dos métodos anotados com @RabbitListener (métodos que recebem as mensagens)
     * precisamos de um Bean que lide com os ARGUMENTOS recebidos nos métodos (as mensagens)
     *
     * É para isso que serve esse cara
     *
     * ref: https://docs.spring.io/spring-amqp/reference/pdf/spring-amqp-reference.pdf
     *
     * @return
     */
    @Bean
    DefaultMessageHandlerMethodFactory handlerMethodFactory() {
        DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();
        factory.setMessageConverter(jackson2Converter());
        return factory;
    }

    /**
     * Configura @RabbitListener
     *
     * @param register
     */
    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar register) {
        register.setMessageHandlerMethodFactory(handlerMethodFactory());
    }

}
