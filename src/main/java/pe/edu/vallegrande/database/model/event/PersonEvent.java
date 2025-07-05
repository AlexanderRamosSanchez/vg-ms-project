package pe.edu.vallegrande.database.model.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonEvent {
    private Integer idPerson;
    private String name;
    private String surname;
    private Integer age;
    private String typeKinship;
    private String sponsored;
    private String state;
    private Integer familyIdFamily;
    private String eventType; // "CREATED", "UPDATED", "DELETED"
}
