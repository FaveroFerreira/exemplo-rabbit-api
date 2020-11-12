package org.cave.rabbit.rabbit;

import java.util.Map;
import org.cave.rabbit.rabbit.config.constants.RabbitQueueConstants;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NomeDoProcessoController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostMapping
    public void postaNaFila(@RequestBody Map<String, Object> body) {
        rabbitTemplate.convertAndSend(RabbitQueueConstants.PROCESSO_EX, null, body);
    }

}
