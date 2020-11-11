package org.cave.rabbit.rabbit.config.constants;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Sugiro usar uma constante pra representar os nomes. Facilita bastante.
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RabbitQueueConstants {

    public static final String PROCESSO_EX = "servico_ex_nome_do_processo";
    public static final String PROCESSO_QUEUE = "servico_queue_nome_do_processo";
    public static final String PROCESSO_DELAYED = "servico_delayed_nome_do_processo";
    public static final String PROCESSO_DLQ = "servico_dlq_nome_do_processo";

    // Separe com espaços os nomes dos processos / fluxos. Confia em mim, parece bobo, mas depois ajuda bastante.

    public static final String OUTRA_EXCHANGE = "...";
    public static final String OUTRA_QUEUE = "...";
    public static final String OUTRA_DELAYED = "...";
    public static final String OUTRA_DLQ = "...";

}
