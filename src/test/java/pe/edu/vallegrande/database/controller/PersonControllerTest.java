package pe.edu.vallegrande.database.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import pe.edu.vallegrande.database.model.Person;
import pe.edu.vallegrande.database.security.TestSecurityConfig;
import pe.edu.vallegrande.database.service.PersonService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(PersonController.class)
@Import(TestSecurityConfig.class)
@DisplayName("PersonController - Tests Completos")
class PersonControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private PersonService personService;

    private Person testPerson;
    private List<Person> testPersonList;

    @BeforeEach
    void setUp() {
        testPerson = createTestPerson();
        testPersonList = createTestPersonList();
    }

    // ========== TESTS EXISTENTES ==========
    @Test
    @DisplayName("GET /active - Debe retornar todas las personas activas")
    void listActive_ShouldReturnActivePersons() {
        when(personService.listActive()).thenReturn(Flux.fromIterable(testPersonList));

        webTestClient.get()
                .uri("/api/v1/person/active")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Person.class)
                .hasSize(2);
    }

    @Test
    @DisplayName("POST / - Debe crear múltiples personas exitosamente")
    void createPersons_ValidData_ShouldCreatePersons() {
        List<Person> inputPersons = createTestPersonList();
        inputPersons.forEach(person -> person.setIdPerson(null));
        
        when(personService.createPersons(any())).thenReturn(Flux.fromIterable(testPersonList));

        webTestClient.post()
                .uri("/api/v1/person")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Flux.fromIterable(inputPersons), Person.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Person.class)
                .hasSize(2);
    }

    @Test
    @DisplayName("PUT /{id} - Debe actualizar persona existente")
    void updatePerson_ExistingPerson_ShouldUpdatePerson() {
        Person updatedPerson = createTestPerson();
        updatedPerson.setName("Juan Carlos");
        
        when(personService.updatePerson(eq(1), any(Person.class))).thenReturn(Mono.just(updatedPerson));

        webTestClient.put()
                .uri("/api/v1/person/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(testPerson)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Person.class)
                .isEqualTo(updatedPerson);
    }

    @Test
    @DisplayName("PATCH /delete/{id} - Debe eliminar lógicamente persona existente")
    void logicallyDelete_ExistingPerson_ShouldDeletePerson() {
        Person deletedPerson = createTestPerson();
        deletedPerson.setState("I");
        when(personService.logicallyDelete(1)).thenReturn(Mono.just(deletedPerson));

        webTestClient.patch()
                .uri("/api/v1/person/delete/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Person.class)
                .isEqualTo(deletedPerson);
    }

    @Test
    @DisplayName("PATCH /active/{id} - Debe reactivar persona existente")
    void reactivate_ExistingPerson_ShouldReactivatePerson() {
        Person reactivatedPerson = createTestPerson();
        reactivatedPerson.setState("A");
        when(personService.reactivate(1)).thenReturn(Mono.just(reactivatedPerson));

        webTestClient.patch()
                .uri("/api/v1/person/active/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Person.class)
                .isEqualTo(reactivatedPerson);
    }

    // ========== NUEVOS TESTS PARA LLEGAR AL 80% ==========

    @Test
    @DisplayName("GET /active - Debe retornar lista vacía cuando no hay personas activas")
    void listActive_WhenNoActivePersons_ShouldReturnEmptyList() {
        // Given
        when(personService.listActive()).thenReturn(Flux.empty());

        // When & Then
        webTestClient.get()
                .uri("/api/v1/person/active")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Person.class)
                .hasSize(0);
    }

    @Test
    @DisplayName("GET /inactive - Debe retornar personas inactivas")
    void listInactive_ShouldReturnInactivePersons() {
        // Given
        Person inactivePerson = createTestPerson();
        inactivePerson.setState("I");
        when(personService.listInactive()).thenReturn(Flux.just(inactivePerson));

        // When & Then
        webTestClient.get()
                .uri("/api/v1/person/inactive")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Person.class)
                .hasSize(1);
    }

    @Test
    @DisplayName("GET /family/{familyId} - Debe retornar personas de una familia")
    void listByFamily_ShouldReturnPersonsFromFamily() {
        // Given
        when(personService.listByFamily(1)).thenReturn(Flux.fromIterable(testPersonList));

        // When & Then
        webTestClient.get()
                .uri("/api/v1/person/family/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Person.class)
                .hasSize(2);
    }

    @Test
    @DisplayName("PUT /{id} - Debe retornar 404 para persona inexistente")
    void updatePerson_NonExistingPerson_ShouldReturn404() {
        // Given
        when(personService.updatePerson(eq(999), any(Person.class))).thenReturn(Mono.empty());

        // When & Then
        webTestClient.put()
                .uri("/api/v1/person/999")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(testPerson)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("PATCH /delete/{id} - Debe retornar 404 para persona inexistente")
    void logicallyDelete_NonExistingPerson_ShouldReturn404() {
        // Given
        when(personService.logicallyDelete(999)).thenReturn(Mono.empty());

        // When & Then
        webTestClient.patch()
                .uri("/api/v1/person/delete/999")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("PATCH /active/{id} - Debe retornar 404 para persona inexistente")
    void reactivate_NonExistingPerson_ShouldReturn404() {
        // Given
        when(personService.reactivate(999)).thenReturn(Mono.empty());

        // When & Then
        webTestClient.patch()
                .uri("/api/v1/person/active/999")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("POST / - Debe manejar creación con lista vacía")
    void createPersons_EmptyList_ShouldReturnEmptyList() {
        // Given
        when(personService.createPersons(any())).thenReturn(Flux.empty());

        // When & Then
        webTestClient.post()
                .uri("/api/v1/person")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Flux.empty(), Person.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBodyList(Person.class)
                .hasSize(0);
    }

    // ========== MÉTODOS AUXILIARES ==========
    private Person createTestPerson() {
        Person person = new Person();
        person.setIdPerson(1);
        person.setName("Juan");
        person.setSurname("Pérez");
        person.setAge(33);
        person.setBirthdate(LocalDate.of(1990, 5, 15));
        person.setTypeDocument("DNI");
        person.setDocumentNumber("12345678");
        person.setTypeKinship("Jefe de familia");
        person.setSponsored("No");
        person.setEducationLevel("Primaria");
        person.setState("A");
        person.setFamilyIdFamily(1);
        return person;
    }

    private List<Person> createTestPersonList() {
        Person person1 = createTestPerson();
        person1.setIdPerson(1);
        person1.setName("Juan");
        person1.setTypeKinship("Jefe de familia");
        
        Person person2 = createTestPerson();
        person2.setIdPerson(2);
        person2.setName("María");
        person2.setSurname("García");
        person2.setAge(30);
        person2.setDocumentNumber("87654321");
        person2.setTypeKinship("Esposa");
        person2.setBirthdate(LocalDate.of(1993, 8, 20));
        
        return Arrays.asList(person1, person2);
    }
}
