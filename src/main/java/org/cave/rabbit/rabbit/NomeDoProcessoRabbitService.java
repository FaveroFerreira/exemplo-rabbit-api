package org.cave.rabbit.rabbit;

import org.cave.rabbit.rabbit.config.RabbitContainerFactoryConfig;
import org.cave.rabbit.rabbit.config.constants.RabbitQueueConstants;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class NomeDoProcessoRabbitService {

    @RabbitListener(
            queues = { RabbitQueueConstants.PROCESSO_QUEUE },
            containerFactory = RabbitContainerFactoryConfig.NOME_DO_PROCESSO_FACTORY_NAME
    )
    public void consumer(Message message) {
        System.out.println(message);
    }

}
