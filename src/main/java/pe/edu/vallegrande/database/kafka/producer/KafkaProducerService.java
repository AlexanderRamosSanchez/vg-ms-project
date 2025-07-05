package pe.edu.vallegrande.database.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.database.model.event.PersonEvent;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, PersonEvent> kafkaTemplate;

    private static final String TOPIC = "person-events";

    public void sendPersonEvent(PersonEvent event) {
        kafkaTemplate.send(TOPIC, event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Evento enviado a Kafka: {}", event);
                    } else {
                        log.error("Error al enviar evento a Kafka", ex);
                    }
                });
    }
}
