package org.cave.rabbit.rabbit.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpoint;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Etapa opcional.
 *
 * Nessa classe iremos configurar o ContainerFactory.
 *
 * A Mágica da mensageria é que definimos um Listener para uma fila e as mensagens aparecem automagicamente
 * no método anotado com @RabbitListener.
 *
 * A realidade é que alguem tem de fazer o processo trabalhoso de ir buscar as mensagens
 * na fila entregar ao metodo consumidor correto. Esse é o cara responsável por esse papel.
 *
 * Por padrão, quando anotamos um método com @RabbitListener sem definirmos um container
 * factory, o spring nos entrega um default, que possui configurações por padrão boas
 * para a grande maioria dos casos de uso.
 *
 * Caso seja necessário configurar o container factory padrão, é possível fazer essa configuração de uma maneira
 * muito mais simples via application.properties/application.yml porém, essa aplicação irá refletir de maneira geral
 * para todos os Listeners.
 *
 * Essa classe é usada quando uma aplicação possui diferentes necessidades para diferentes listeners.
 */
@Configuration
public class RabbitContainerFactoryConfig {

    public static final String NOME_DO_PROCESSO_FACTORY_NAME = "nomeDoProcessoContainerFactory";
    public static final String OUTRO_FACTORY_NAME = "...";

    /**
     * Esses valores do .properties/.yml, por favor
     */
    private static final Integer CONCURRENT_CONSUMER = 3;
    private static final Integer CONCURRENT_MAX_CONSUMER = 3;
    private static final boolean DEFAULT_REQUEUE_REJECTED = false;

    public static final int RECOVERY_INTERVAL = 30000;

    /**
     * Aqui iremos programáticamente criar o dito cujo.
     *
     * Existe uma outra maneira arcaica e horrorosa de fazer, que envolve criar uma classe externa que implementa
     * RabbitListenerContainerFactory<SimpleMessageListenerContainer> para sobreescrever o metodo createListenerContainer.
     *
     * Hoje a noite eu fiz dessa maneira e validei, e tudo parece funcionar sem problemas. Meu deus eu amo lamdas.
     *
     * OK.
     *
     * @param connectionFactory
     * @return
     */
    @Bean(name = RabbitContainerFactoryConfig.NOME_DO_PROCESSO_FACTORY_NAME)
    public RabbitListenerContainerFactory<SimpleMessageListenerContainer> nomeDoProcessoContainerFactory(ConnectionFactory connectionFactory) {
        return (RabbitListenerEndpoint endpoint) -> {

            // Existem diversos tipos de containers. O SimpleMessageListenerContainer é o indicado na grande maioria dos casos.
            SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
            container.setConnectionFactory(connectionFactory); // Obiviamente, para fazer o papel de aviãozinho, buscando e recebendo mensagens, ele deve ter conhecimento dos dados de conexão
            container.setConcurrentConsumers(CONCURRENT_CONSUMER); // Define quantas threads farão o papel de aviãozinho
            container.setMaxConcurrentConsumers(CONCURRENT_MAX_CONSUMER); // Limita um numero X de consumers. Caso deseje SEMPRE um numero X de threads constantes. Considere preencher o valor no .setConcurrentConsumers e deixar o .setMaxConcurrentConsumers vazio
            container.setRecoveryInterval(RECOVERY_INTERVAL); // Caso ocorra um erro ao consumir mensagens da fila, o container entra em Recovery Mode (se o erro não for absurdo). Espera X tempo antes de tentar reiniciar o consumo.
            container.setDefaultRequeueRejected(false); // As mensagens não vão ser reenfileiradas se estourarmos uma exception. (deve ter algum caso de uso, ainda não usei como true).

            if (endpoint != null) {
                endpoint.setupListenerContainer(container); // Essa linha é um resquicio da forma antiga, programática, sem @Anotações de fazer configuração de filas no Spring. Não me recordo se ela ainda é necessária. Só usamos lambda pra receber o endpoint por causa dessa linha. Vale testar sem.
            }

            return container;
        };
    }

    @Bean
    public RabbitListenerContainerFactory<SimpleMessageListenerContainer> customerPromotionCreateContainerFactory (ConnectionFactory connectionFactory) {
        return null;
    }

}
