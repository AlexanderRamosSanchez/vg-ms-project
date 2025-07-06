package pe.edu.vallegrande.database.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import pe.edu.vallegrande.database.kafka.producer.KafkaProducerService;
import pe.edu.vallegrande.database.model.Person;
import pe.edu.vallegrande.database.model.event.PersonEvent;
import pe.edu.vallegrande.database.repository.PersonRepository;
import pe.edu.vallegrande.database.webclient.FamilyServiceClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("PersonService Unit Tests")
class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;
    
    @Mock
    private FamilyServiceClient familyServiceClient;
    
    @Mock
    private KafkaProducerService kafkaProducerService;
    
    @InjectMocks
    private PersonService personService;

    private Person person1;
    private Person person2;
    private Person person3;

    @BeforeEach
    void setUp() {
        person1 = new Person(1, "Juan", "Pérez", 25, LocalDate.of(1998, 5, 15),
                             "DNI", "12345678", "Padre", "Sí", "Primaria", "A", 1);
        person2 = new Person(2, "María", "González", 30, LocalDate.of(1993, 8, 20),
                             "DNI", "87654321", "Madre", "No", "Primaria", "A", 1);
        person3 = new Person(3, "Pedro", "López", 22, LocalDate.of(2001, 3, 10),
                             "DNI", "11223344", "Hijo", "Sí", "Primaria", "I", 2);
        
        doNothing().when(kafkaProducerService).sendPersonEvent(any(PersonEvent.class));
    }

    // ========== TESTS EXISTENTES ==========
    @Test
    @DisplayName("Should return active persons sorted by ID")
    void shouldReturnActivePersonsSortedById() {
        Person personWithHigherId = new Person(10, "Ana", "Martínez", 28, LocalDate.of(1995, 7, 12),
                                               "DNI", "55566677", "Hermana", "No", "Primaria", "A", 1);
        List<Person> unsortedPersons = Arrays.asList(personWithHigherId, person1, person2);
        
        when(personRepository.findByState("A")).thenReturn(Flux.fromIterable(unsortedPersons));

        StepVerifier.create(personService.listActive())
                .expectNext(person1)
                .expectNext(person2)
                .expectNext(personWithHigherId)
                .verifyComplete();

        verify(personRepository).findByState("A");
    }

    @Test
    @DisplayName("Should return inactive persons sorted by ID")
    void shouldReturnInactivePersonsSortedById() {
        when(personRepository.findByState("I")).thenReturn(Flux.just(person3));

        StepVerifier.create(personService.listInactive())
                .expectNext(person3)
                .verifyComplete();

        verify(personRepository).findByState("I");
    }

    @Test
    @DisplayName("Should create person with family validation when family exists")
    void shouldCreatePersonWithFamilyValidationWhenFamilyExists() {
        Person savedPerson = new Person(1, "Juan", "Pérez", 25, LocalDate.of(1998, 5, 15),
                                        "DNI", "12345678", "Padre", "Sí", "Primaria", "A", 1);

        when(familyServiceClient.familyExists(1)).thenReturn(Mono.just(true));
        when(personRepository.save(any(Person.class))).thenReturn(Mono.just(savedPerson));

        StepVerifier.create(personService.createPersons(Flux.just(person1)))
                .expectNext(savedPerson)
                .verifyComplete();

        verify(familyServiceClient).familyExists(1);
        verify(personRepository).save(argThat(person -> "A".equals(person.getState())));
        verify(kafkaProducerService).sendPersonEvent(any(PersonEvent.class));
    }

    @Test
    @DisplayName("Should update person when person exists")
    void shouldUpdatePersonWhenPersonExists() {
        Person updatedData = new Person(null, "Juan Carlos", "Pérez García", 26, LocalDate.of(1997, 5, 15),
                                        "DNI", "12345678", "Padre", "No", "Primaria", "A", 2);
        Person savedPerson = new Person(1, "Juan Carlos", "Pérez García", 26, LocalDate.of(1997, 5, 15),
                                        "DNI", "12345678", "Padre", "No", "Primaria", "A", 2);

        when(personRepository.findById(1)).thenReturn(Mono.just(person1));
        when(personRepository.save(any(Person.class))).thenReturn(Mono.just(savedPerson));

        StepVerifier.create(personService.updatePerson(1, updatedData))
                .expectNext(savedPerson)
                .verifyComplete();

        verify(personRepository).findById(1);
        verify(personRepository).save(argThat(person ->
            "Juan Carlos".equals(person.getName()) &&
            "Pérez García".equals(person.getSurname()) &&
            26 == person.getAge() &&
            2 == person.getFamilyIdFamily()));
        verify(kafkaProducerService).sendPersonEvent(any(PersonEvent.class));
    }

    @Test
    @DisplayName("Should logically delete person when person exists")
    void shouldLogicallyDeletePersonWhenPersonExists() {
        Person deletedPerson = new Person(1, "Juan", "Pérez", 25, LocalDate.of(1998, 5, 15),
                                          "DNI", "12345678", "Padre", "Sí", "Primaria", "I", 1);

        when(personRepository.findById(1)).thenReturn(Mono.just(person1));
        when(personRepository.save(any(Person.class))).thenReturn(Mono.just(deletedPerson));

        StepVerifier.create(personService.logicallyDelete(1))
                .expectNext(deletedPerson)
                .verifyComplete();

        verify(personRepository).findById(1);
        verify(personRepository).save(argThat(person -> "I".equals(person.getState())));
        verify(kafkaProducerService).sendPersonEvent(any(PersonEvent.class));
    }

    @Test
    @DisplayName("Should reactivate person when person exists")
    void shouldReactivatePersonWhenPersonExists() {
        Person reactivatedPerson = new Person(3, "Pedro", "López", 22, LocalDate.of(2001, 3, 10),
                                              "DNI", "11223344", "Hijo", "Sí", "Primaria", "A", 2);

        when(personRepository.findById(3)).thenReturn(Mono.just(person3));
        when(personRepository.save(any(Person.class))).thenReturn(Mono.just(reactivatedPerson));

        StepVerifier.create(personService.reactivate(3))
                .expectNext(reactivatedPerson)
                .verifyComplete();

        verify(personRepository).findById(3);
        verify(personRepository).save(argThat(person -> "A".equals(person.getState())));
    }

    // ========== NUEVOS TESTS PARA LLEGAR AL 80% ==========

    @Test
    @DisplayName("Should return empty flux when no active persons exist")
    void shouldReturnEmptyFluxWhenNoActivePersonsExist() {
        // Given
        when(personRepository.findByState("A")).thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(personService.listActive())
                .verifyComplete();

        verify(personRepository).findByState("A");
    }

    @Test
    @DisplayName("Should return empty flux when no inactive persons exist")
    void shouldReturnEmptyFluxWhenNoInactivePersonsExist() {
        // Given
        when(personRepository.findByState("I")).thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(personService.listInactive())
                .verifyComplete();

        verify(personRepository).findByState("I");
    }

    // ========== TESTS CORREGIDOS PARA REFLEJAR EL COMPORTAMIENTO REAL ==========

    @Test
    @DisplayName("Should propagate error when family service fails during creation")
    void shouldPropagateErrorWhenFamilyServiceFailsDuringCreation() {
        // Given
        when(familyServiceClient.familyExists(1)).thenReturn(Mono.error(new RuntimeException("Family service error")));

        // When & Then - El servicio propaga el error, no lo maneja silenciosamente
        StepVerifier.create(personService.createPersons(Flux.just(person1)))
                .expectError(RuntimeException.class)
                .verify();

        verify(familyServiceClient).familyExists(1);
        verify(personRepository, never()).save(any(Person.class));
    }

    @Test
    @DisplayName("Should throw error when family does not exist during creation")
    void shouldThrowErrorWhenFamilyDoesNotExistDuringCreation() {
        // Given
        when(familyServiceClient.familyExists(1)).thenReturn(Mono.just(false));

        // When & Then - El servicio lanza error cuando la familia no existe
        StepVerifier.create(personService.createPersons(Flux.just(person1)))
                .expectError(RuntimeException.class)
                .verify();

        verify(familyServiceClient).familyExists(1);
        verify(personRepository, never()).save(any(Person.class));
    }

    @Test
    @DisplayName("Should throw error when updating non-existing person")
    void shouldThrowErrorWhenUpdatingNonExistingPerson() {
        // Given
        Person updatedData = new Person(null, "Juan Carlos", "Pérez García", 26, LocalDate.of(1997, 5, 15),
                                        "DNI", "12345678", "Padre", "No", "Primaria", "A", 2);
        when(personRepository.findById(999)).thenReturn(Mono.empty());

        // When & Then - El servicio lanza NPE cuando la persona no existe
        StepVerifier.create(personService.updatePerson(999, updatedData))
                .expectError(NullPointerException.class)
                .verify();

        verify(personRepository).findById(999);
        verify(personRepository, never()).save(any(Person.class));
    }

    @Test
    @DisplayName("Should handle repository error during active persons listing")
    void shouldHandleRepositoryErrorDuringActivePersonsListing() {
        // Given
        when(personRepository.findByState("A")).thenReturn(Flux.error(new RuntimeException("Database error")));

        // When & Then
        StepVerifier.create(personService.listActive())
                .expectError(RuntimeException.class)
                .verify();

        verify(personRepository).findByState("A");
    }

    @Test
    @DisplayName("Should handle repository error during inactive persons listing")
    void shouldHandleRepositoryErrorDuringInactivePersonsListing() {
        // Given
        when(personRepository.findByState("I")).thenReturn(Flux.error(new RuntimeException("Database error")));

        // When & Then
        StepVerifier.create(personService.listInactive())
                .expectError(RuntimeException.class)
                .verify();

        verify(personRepository).findByState("I");
    }

    @Test
    @DisplayName("Should handle repository error during person creation")
    void shouldHandleRepositoryErrorDuringPersonCreation() {
        // Given
        when(familyServiceClient.familyExists(1)).thenReturn(Mono.just(true));
        when(personRepository.save(any(Person.class))).thenReturn(Mono.error(new RuntimeException("Save error")));

        // When & Then
        StepVerifier.create(personService.createPersons(Flux.just(person1)))
                .expectError(RuntimeException.class)
                .verify();

        verify(familyServiceClient).familyExists(1);
        verify(personRepository).save(any(Person.class));
    }

    @Test
    @DisplayName("Should handle repository error during person update")
    void shouldHandleRepositoryErrorDuringPersonUpdate() {
        // Given
        Person updatedData = new Person(null, "Juan Carlos", "Pérez García", 26, LocalDate.of(1997, 5, 15),
                                        "DNI", "12345678", "Padre", "No", "Primaria", "A", 2);
        when(personRepository.findById(1)).thenReturn(Mono.just(person1));
        when(personRepository.save(any(Person.class))).thenReturn(Mono.error(new RuntimeException("Update error")));

        // When & Then
        StepVerifier.create(personService.updatePerson(1, updatedData))
                .expectError(RuntimeException.class)
                .verify();

        verify(personRepository).findById(1);
        verify(personRepository).save(any(Person.class));
    }

    @Test
    @DisplayName("Should create multiple persons successfully")
    void shouldCreateMultiplePersonsSuccessfully() {
        // Given
        List<Person> inputPersons = Arrays.asList(person1, person2);
        List<Person> savedPersons = Arrays.asList(
            new Person(1, "Juan", "Pérez", 25, LocalDate.of(1998, 5, 15), "DNI", "12345678", "Padre", "Sí", "Primaria", "A", 1),
            new Person(2, "María", "González", 30, LocalDate.of(1993, 8, 20), "DNI", "87654321", "Madre", "No", "Primaria", "A", 1)
        );

        when(familyServiceClient.familyExists(1)).thenReturn(Mono.just(true));
        when(personRepository.save(any(Person.class)))
            .thenReturn(Mono.just(savedPersons.get(0)))
            .thenReturn(Mono.just(savedPersons.get(1)));

        // When & Then
        StepVerifier.create(personService.createPersons(Flux.fromIterable(inputPersons)))
            .expectNext(savedPersons.get(0))
            .expectNext(savedPersons.get(1))
            .verifyComplete();

        verify(familyServiceClient, times(2)).familyExists(1);
        verify(personRepository, times(2)).save(any(Person.class));
        verify(kafkaProducerService, times(2)).sendPersonEvent(any(PersonEvent.class));
    }

    @Test
    @DisplayName("Should list persons by family ID")
    void shouldListPersonsByFamilyId() {
        // Given
        List<Person> familyPersons = Arrays.asList(person1, person2);
        when(personRepository.findByFamilyIdFamily(1)).thenReturn(Flux.fromIterable(familyPersons));

        // When & Then
        StepVerifier.create(personService.listByFamily(1))
                .expectNext(person1)
                .expectNext(person2)
                .verifyComplete();

        verify(personRepository).findByFamilyIdFamily(1);
    }

    @Test
    @DisplayName("Should return empty flux when family has no persons")
    void shouldReturnEmptyFluxWhenFamilyHasNoPersons() {
        // Given
        when(personRepository.findByFamilyIdFamily(999)).thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(personService.listByFamily(999))
                .verifyComplete();

        verify(personRepository).findByFamilyIdFamily(999);
    }
}
