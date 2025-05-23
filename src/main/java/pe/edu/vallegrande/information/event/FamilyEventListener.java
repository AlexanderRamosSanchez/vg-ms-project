package pe.edu.vallegrande.information.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Component
public class FamilyEventListener {
    
    private static final Logger logger = LoggerFactory.getLogger(FamilyEventListener.class);
    
    @KafkaListener(topics = "family-events", groupId = "housing-service-group")
    public void listen(FamilyEvent event) {
        logger.info("Received family event: {}", event);
        
        // Here you could implement logic to handle family events
        // For example, if a family is deleted, you might want to mark related services as orphaned
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FamilyEvent {
        private Integer familyId;
        private String eventType;
        private String lastName;
        private String status;
        private Integer serviceId;
        private Integer housingId;
    }
}
