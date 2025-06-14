package pe.edu.vallegrande.database.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class HousingDetailsDTO {
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
