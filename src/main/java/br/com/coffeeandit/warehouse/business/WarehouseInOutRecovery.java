package br.com.sicredi.warehouse.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WarehouseInOutRecovery {

    private Logger LOG = LoggerFactory.getLogger(WarehouseInOutRecovery.class);
    @Value("${app.topicError}")
    private String topic;
    private KafkaTemplate<String, String> kafkaTemplate;


    public WarehouseInOutRecovery(final KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional("kafkaTransactionManager")
    ///@Async
    public void recovery(final String saleMovement) {

        sendMessage(saleMovement);

    }

    private void sendMessage(final String sale) {
        try {


            var payload = sale;
            var message = MessageBuilder
                    .withPayload(payload)
                    .setHeader(KafkaHeaders.TOPIC, topic)
                    .setHeader(KafkaHeaders.PARTITION_ID, 0)
                    .build();
            LOG.info(String.format("Enviando para compensação pelo kafka %s", payload));
            kafkaTemplate.send(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
