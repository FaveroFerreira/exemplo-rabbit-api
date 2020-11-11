package org.cave.rabbit.rabbit.config;

import java.util.HashMap;
import java.util.Map;
import org.cave.rabbit.rabbit.config.constants.RabbitQueueConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Nessa classe, iremos programaticamente configurar o fluxo de exchange, fila, binding, delayed e dlq do RabbitMQ.
 * <p>
 * - Exchange
 * - Fila Normal
 * - Bindings
 * - Fila Delayed
 * - Fila Morta
 */
@Configuration
public class NomeDoProcessoConfig {

    public static final String X_DEAD_LETTER_EXCHANGE = "x-dead-letter-exchange"; // Nome do parametro da DLQ Exchange
    public static final String X_DEAD_LETTER_ROUTING_KEY = "x-dead-letter-routing-key"; // Nome do parâmetro da DLQ Routing Key

    @Autowired
    private RabbitAdmin rabbitAdmin; // Esse cara vai fazer a declaração das filas, no caso, cria-las automáticamente caso não existam.

    /**
     * Aqui fazemos a declaração da exchange responsável pelo processo.
     * <p>
     * Nesse caso, usamos uma fanout exchange. Existem diferentes tipos de exchanges, para diferentes casos de uso.
     * <p>
     * Uma fanout exchange vai distribuir a mensagem para uma ou mais filas que estejam bindadas nessa exchange.
     * É um jeito fácil de distribuir uma mensagem pra várias filas de uma só vez!
     *
     * @return Exchange
     */
    @Bean
    FanoutExchange exchangeNomeDoProcesso() {
        FanoutExchange fanoutExchange = new FanoutExchange(RabbitQueueConstants.PROCESSO_EX); // Nome
        fanoutExchange.setAdminsThatShouldDeclare(rabbitAdmin); // Admin irá declara-la
        return fanoutExchange;
    }


    /**
     * Aqui é a FAMOSA fila.
     * <p>
     * Aqui iremos fazer algumas configurações para o que deve acontecer com as mensagens caso seu (TTL)Time To Live expire.
     * <p>
     * x-dead-letter-exchange -  Não usaremos uma DLQ Exchange iremos direcionar as mensagens mortas diretamente a uma fila.
     * x-dead-letter-routing-key - Declaramos diretamente o nome da fila. O Rabbit irá direcionar as mensagens mortas diretamente pra lá.
     *
     * @return Fila
     */
    @Bean
    Queue queueNomeDoProcesso() {
        Map<String, Object> args = new HashMap<>();
        args.put(X_DEAD_LETTER_EXCHANGE, "");
        args.put(X_DEAD_LETTER_ROUTING_KEY, RabbitQueueConstants.PROCESSO_DLQ); // Fila morta mesmo
        Queue queue = new Queue(RabbitQueueConstants.PROCESSO_QUEUE, true, false, false, args);
        queue.setAdminsThatShouldDeclare(rabbitAdmin);
        return queue;
    }

    /**
     * Aqui fazemos a configuração da Fila Delayed.
     * <p>
     * Perceba que agora, a "X_DEAD_LETTER_ROUTING_KEY" configurada para essa fila nada mais é do que a fila NORMAL.
     * Ou seja, depois que a mensagem passar um tempo X aqui (TTL), ela irá expirar e cair na fila normal, para ser consumida novamente.
     * <p>
     * Dessa forma conseguimos fazer uma espécie de retentativa para processar uma mensagem.
     *
     * @return Delayed
     */
    @Bean
    Queue delayedNomeDoProcesso() {
        Map<String, Object> args = new HashMap<>();
        args.put(X_DEAD_LETTER_EXCHANGE, "");
        args.put(X_DEAD_LETTER_ROUTING_KEY, RabbitQueueConstants.PROCESSO_QUEUE); // IMPORTANTEEEEEEEEEEEEEEEEEEEEE
        Queue queue = new Queue(RabbitQueueConstants.PROCESSO_DELAYED, true, false, false, args);
        queue.setAdminsThatShouldDeclare(rabbitAdmin);
        return queue;
    }

    /**
     * A Fila morta, nada, zefini, morreu, cabo.
     * <p>
     * A partir daqui tratamos manualmente. Retentou várias vezes e ainda ta com problema.
     * Análisamos e reprocessamos as mensagens quando o problema for solucionado.
     *
     * @return Dlq
     */
    @Bean
    Queue dlqNomeDoProcesso() {
        Queue queue = new Queue(RabbitQueueConstants.PROCESSO_DLQ, true, false, false);
        queue.setAdminsThatShouldDeclare(rabbitAdmin);
        return queue;
    }

    /**
     * Fazemos o bindin da fila com a exchange. Simples não?
     *
     * @return Binding
     */
    @Bean
    Binding bindingNomeDoProcesso() {
        Binding binding = BindingBuilder
                .bind(queueNomeDoProcesso())
                .to(exchangeNomeDoProcesso());
        binding.setAdminsThatShouldDeclare(rabbitAdmin);
        return binding;
    }

}
