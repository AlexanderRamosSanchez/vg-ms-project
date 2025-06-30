package pe.edu.vallegrande.database.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Data;

@Data
@Table("admission_reasons")
public class AdmissionReason {
    @Id
    private Integer id;
    private String reason;
}