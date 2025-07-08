package pe.edu.vallegrande.database.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.vallegrande.database.dto.FamilyDTO;
import pe.edu.vallegrande.database.model.Family;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FamilyMapper Tests")
class FamilyMapperTest {

    @InjectMocks
    private FamilyMapper familyMapper;

    private Family family;
    private FamilyDTO familyDTO;
    private LocalDateTime testDateTime;

    @BeforeEach
    @DisplayName("Setup test data")
    void setUp() {
        testDateTime = LocalDateTime.now();
        
        // Setup Family entity
        family = new Family();
        family.setId(1);
        family.setLastName("García");
        family.setDirection("Av. Principal 123");
        family.setReasibAdmission(1);
        family.setNumberMembers(4);
        family.setNumberChildren(2);
        family.setFamilyType("Nuclear");
        family.setSocialProblems("Desempleo");
        family.setWeeklyFrequency("3 veces al dia");
        family.setFeedingType("Balanceada");
        family.setSafeType("Segura");
        family.setFamilyDisease("Diabetes");
        family.setTreatment("Medicamentos");
        family.setDiseaseHistory("Hipertensión familiar");
        family.setMedicalExam("Examen general");
        family.setStatus("A");
        family.setCreated(testDateTime);
        family.setDeleted(testDateTime.plusDays(1));

        // Setup FamilyDTO
        familyDTO = new FamilyDTO();
        familyDTO.setId(2);
        familyDTO.setLastName("Pérez");
        familyDTO.setDirection("Calle Secundaria 456");
        familyDTO.setReasibAdmission(2);
        familyDTO.setNumberMembers(5);
        familyDTO.setNumberChildren(3);
        familyDTO.setFamilyType("Extendida");
        familyDTO.setSocialProblems("Pobreza");
        familyDTO.setWeeklyFrequency("3 veces al dia");
        familyDTO.setFeedingType("Deficiente");
        familyDTO.setSafeType("Insegura");
        familyDTO.setFamilyDisease("Asma");
        familyDTO.setTreatment("Inhaladores");
        familyDTO.setDiseaseHistory("Alergias");
        familyDTO.setMedicalExam("Examen respiratorio");
        familyDTO.setStatus("I");
        familyDTO.setCreated(testDateTime);
        familyDTO.setDeleted(testDateTime.plusDays(2));
    }

    @Test
    @DisplayName("toDTO should convert Family entity to FamilyDTO successfully")
    void testToDTO_WithValidFamily_ShouldReturnFamilyDTO() {
        // When
        FamilyDTO result = familyMapper.toDTO(family);

        // Then
        assertNotNull(result);
        assertEquals(family.getId(), result.getId());
        assertEquals(family.getLastName(), result.getLastName());
        assertEquals(family.getDirection(), result.getDirection());
        assertEquals(family.getReasibAdmission(), result.getReasibAdmission());
        assertEquals(family.getNumberMembers(), result.getNumberMembers());
        assertEquals(family.getNumberChildren(), result.getNumberChildren());
        assertEquals(family.getFamilyType(), result.getFamilyType());
        assertEquals(family.getSocialProblems(), result.getSocialProblems());
        assertEquals(family.getWeeklyFrequency(), result.getWeeklyFrequency());
        assertEquals(family.getFeedingType(), result.getFeedingType());
        assertEquals(family.getSafeType(), result.getSafeType());
        assertEquals(family.getFamilyDisease(), result.getFamilyDisease());
        assertEquals(family.getTreatment(), result.getTreatment());
        assertEquals(family.getDiseaseHistory(), result.getDiseaseHistory());
        assertEquals(family.getMedicalExam(), result.getMedicalExam());
        assertEquals(family.getStatus(), result.getStatus());
        assertEquals(family.getCreated(), result.getCreated());
        assertEquals(family.getDeleted(), result.getDeleted());
    }

    @Test
    @DisplayName("toDTO should return null when Family entity is null")
    void testToDTO_WithNullFamily_ShouldReturnNull() {
        // When
        FamilyDTO result = familyMapper.toDTO(null);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("toDTO should handle Family entity with null values")
    void testToDTO_WithFamilyWithNullValues_ShouldReturnDTOWithNullValues() {
        // Given
        Family emptyFamily = new Family();
        emptyFamily.setId(1);

        // When
        FamilyDTO result = familyMapper.toDTO(emptyFamily);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertNull(result.getLastName());
        assertNull(result.getDirection());
        assertNull(result.getReasibAdmission());
        assertNull(result.getNumberMembers());
        assertNull(result.getNumberChildren());
        assertNull(result.getFamilyType());
        assertNull(result.getSocialProblems());
        assertNull(result.getWeeklyFrequency());
        assertNull(result.getFeedingType());
        assertNull(result.getSafeType());
        assertNull(result.getFamilyDisease());
        assertNull(result.getTreatment());
        assertNull(result.getDiseaseHistory());
        assertNull(result.getMedicalExam());
        assertNull(result.getStatus());
        assertNull(result.getCreated());
        assertNull(result.getDeleted());
    }

    @Test
    @DisplayName("toEntity should convert FamilyDTO to Family entity successfully")
    void testToEntity_WithValidFamilyDTO_ShouldReturnFamily() {
        // When
        Family result = familyMapper.toEntity(familyDTO);

        // Then
        assertNotNull(result);
        assertEquals(familyDTO.getLastName(), result.getLastName());
        assertEquals(familyDTO.getDirection(), result.getDirection());
        assertEquals(familyDTO.getReasibAdmission(), result.getReasibAdmission());
        assertEquals(familyDTO.getNumberMembers(), result.getNumberMembers());
        assertEquals(familyDTO.getNumberChildren(), result.getNumberChildren());
        assertEquals(familyDTO.getFamilyType(), result.getFamilyType());
        assertEquals(familyDTO.getSocialProblems(), result.getSocialProblems());
        assertEquals(familyDTO.getWeeklyFrequency(), result.getWeeklyFrequency());
        assertEquals(familyDTO.getFeedingType(), result.getFeedingType());
        assertEquals(familyDTO.getSafeType(), result.getSafeType());
        assertEquals(familyDTO.getFamilyDisease(), result.getFamilyDisease());
        assertEquals(familyDTO.getTreatment(), result.getTreatment());
        assertEquals(familyDTO.getDiseaseHistory(), result.getDiseaseHistory());
        assertEquals(familyDTO.getMedicalExam(), result.getMedicalExam());
        assertEquals(familyDTO.getStatus(), result.getStatus());
        // Note: ID is not copied in toEntity, created and deleted are not set
        assertNull(result.getId());
        assertNull(result.getCreated());
        assertNull(result.getDeleted());
    }

    @Test
    @DisplayName("toEntity should return null when FamilyDTO is null")
    void testToEntity_WithNullFamilyDTO_ShouldReturnNull() {
        // When
        Family result = familyMapper.toEntity(null);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("toEntity should handle FamilyDTO with null values")
    void testToEntity_WithFamilyDTOWithNullValues_ShouldReturnFamilyWithNullValues() {
        // Given
        FamilyDTO emptyDTO = new FamilyDTO();

        // When
        Family result = familyMapper.toEntity(emptyDTO);

        // Then
        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getLastName());
        assertNull(result.getDirection());
        assertNull(result.getReasibAdmission());
        assertNull(result.getNumberMembers());
        assertNull(result.getNumberChildren());
        assertNull(result.getFamilyType());
        assertNull(result.getSocialProblems());
        assertNull(result.getWeeklyFrequency());
        assertNull(result.getFeedingType());
        assertNull(result.getSafeType());
        assertNull(result.getFamilyDisease());
        assertNull(result.getTreatment());
        assertNull(result.getDiseaseHistory());
        assertNull(result.getMedicalExam());
        assertNull(result.getStatus());
        assertNull(result.getCreated());
        assertNull(result.getDeleted());
    }

    @Test
    @DisplayName("updateEntityFromDTO should update Family entity with FamilyDTO data")
    void testUpdateEntityFromDTO_WithValidData_ShouldUpdateEntity() {
        // Given
        Family existingFamily = new Family();
        existingFamily.setId(99);
        existingFamily.setLastName("OldName");
        existingFamily.setCreated(testDateTime.minusDays(5));
        existingFamily.setDeleted(testDateTime.minusDays(3));

        // When
        familyMapper.updateEntityFromDTO(existingFamily, familyDTO);

        // Then
        assertNotNull(existingFamily);
        assertEquals(99, existingFamily.getId()); // ID should not change
        assertEquals(familyDTO.getLastName(), existingFamily.getLastName());
        assertEquals(familyDTO.getDirection(), existingFamily.getDirection());
        assertEquals(familyDTO.getReasibAdmission(), existingFamily.getReasibAdmission());
        assertEquals(familyDTO.getNumberMembers(), existingFamily.getNumberMembers());
        assertEquals(familyDTO.getNumberChildren(), existingFamily.getNumberChildren());
        assertEquals(familyDTO.getFamilyType(), existingFamily.getFamilyType());
        assertEquals(familyDTO.getSocialProblems(), existingFamily.getSocialProblems());
        assertEquals(familyDTO.getWeeklyFrequency(), existingFamily.getWeeklyFrequency());
        assertEquals(familyDTO.getFeedingType(), existingFamily.getFeedingType());
        assertEquals(familyDTO.getSafeType(), existingFamily.getSafeType());
        assertEquals(familyDTO.getFamilyDisease(), existingFamily.getFamilyDisease());
        assertEquals(familyDTO.getTreatment(), existingFamily.getTreatment());
        assertEquals(familyDTO.getDiseaseHistory(), existingFamily.getDiseaseHistory());
        assertEquals(familyDTO.getMedicalExam(), existingFamily.getMedicalExam());
        assertEquals(familyDTO.getStatus(), existingFamily.getStatus());
        
        // Created and deleted timestamps should remain unchanged
        assertEquals(testDateTime.minusDays(5), existingFamily.getCreated());
        assertEquals(testDateTime.minusDays(3), existingFamily.getDeleted());
    }

    @Test
    @DisplayName("updateEntityFromDTO should do nothing when Family entity is null")
    void testUpdateEntityFromDTO_WithNullFamily_ShouldDoNothing() {
        // Given
        Family nullFamily = null;

        // When & Then - should not throw exception
        assertDoesNotThrow(() -> familyMapper.updateEntityFromDTO(nullFamily, familyDTO));
    }

    @Test
    @DisplayName("updateEntityFromDTO should do nothing when FamilyDTO is null")
    void testUpdateEntityFromDTO_WithNullDTO_ShouldDoNothing() {
        // Given
        Family existingFamily = new Family();
        existingFamily.setId(1);
        existingFamily.setLastName("OriginalName");

        // When
        familyMapper.updateEntityFromDTO(existingFamily, null);

        // Then - family should remain unchanged
        assertEquals(1, existingFamily.getId());
        assertEquals("OriginalName", existingFamily.getLastName());
    }

    @Test
    @DisplayName("updateEntityFromDTO should do nothing when both parameters are null")
    void testUpdateEntityFromDTO_WithBothNull_ShouldDoNothing() {
        // When & Then - should not throw exception
        assertDoesNotThrow(() -> familyMapper.updateEntityFromDTO(null, null));
    }

    @Test
    @DisplayName("updateEntityFromDTO should update with null values when FamilyDTO has null fields")
    void testUpdateEntityFromDTO_WithDTOWithNullValues_ShouldUpdateWithNullValues() {
        // Given
        Family existingFamily = new Family();
        existingFamily.setId(1);
        existingFamily.setLastName("OriginalName");
        existingFamily.setDirection("Original Direction");
        existingFamily.setStatus("A");

        FamilyDTO nullValuesDTO = new FamilyDTO();
        // All fields are null by default

        // When
        familyMapper.updateEntityFromDTO(existingFamily, nullValuesDTO);

        // Then
        assertEquals(1, existingFamily.getId()); // ID should not change
        assertNull(existingFamily.getLastName());
        assertNull(existingFamily.getDirection());
        assertNull(existingFamily.getReasibAdmission());
        assertNull(existingFamily.getNumberMembers());
        assertNull(existingFamily.getNumberChildren());
        assertNull(existingFamily.getFamilyType());
        assertNull(existingFamily.getSocialProblems());
        assertNull(existingFamily.getWeeklyFrequency());
        assertNull(existingFamily.getFeedingType());
        assertNull(existingFamily.getSafeType());
        assertNull(existingFamily.getFamilyDisease());
        assertNull(existingFamily.getTreatment());
        assertNull(existingFamily.getDiseaseHistory());
        assertNull(existingFamily.getMedicalExam());
        assertNull(existingFamily.getStatus());
    }

    @Test
    @DisplayName("Complete workflow: toEntity and toDTO should maintain data integrity")
    void testCompleteWorkflow_ToEntityAndToDTO_ShouldMaintainDataIntegrity() {
        // Given
        FamilyDTO originalDTO = new FamilyDTO();
        originalDTO.setLastName("Test Family");
        originalDTO.setDirection("Test Address");
        originalDTO.setStatus("A");
        originalDTO.setNumberMembers(3);

        // When
        Family entity = familyMapper.toEntity(originalDTO);
        FamilyDTO resultDTO = familyMapper.toDTO(entity);

        // Then
        assertNotNull(entity);
        assertNotNull(resultDTO);
        assertEquals(originalDTO.getLastName(), resultDTO.getLastName());
        assertEquals(originalDTO.getDirection(), resultDTO.getDirection());
        assertEquals(originalDTO.getStatus(), resultDTO.getStatus());
        assertEquals(originalDTO.getNumberMembers(), resultDTO.getNumberMembers());
        
        // ID should be null since it's not set in toEntity
        assertNull(resultDTO.getId());
        // Created and deleted should be null since they're not set in toEntity
        assertNull(resultDTO.getCreated());
        assertNull(resultDTO.getDeleted());
    }
}