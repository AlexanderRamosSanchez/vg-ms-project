package pe.edu.vallegrande.information.model;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
@Table("housing_details")
public class HousingDetails {
    @Id
    private Integer id;
    private String tenure;
    private String typeOfHousing;
    private String housingMaterial;
    private String housingSecurity;
    private Integer homeEnvironment;
    private Integer bedroomNumber;
    private String habitability;
    private String caregiverCondition;
    private String caringCondition;
    private Integer membersWork;
    private String workingTime;
    private BigDecimal monthlyIncome;
    private BigDecimal monthlyExpense;
}
