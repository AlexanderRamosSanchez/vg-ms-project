package pe.edu.vallegrande.database.service;

public class PersonServiceTest {
    /*
    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private PersonService personService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindById_PersonFound() {
        Person person = new Person(1, "John", "Doe", 30, LocalDate.of(1993, 1, 1), "DNI", "12345678", "Sibling", "Yes",
                "A", null);
        when(personRepository.findById(1)).thenReturn(Mono.just(person));

        Mono<Person> result = personService.findById(1);
        assertEquals("John", result.block().getName());
        verify(personRepository).findById(1);
    }

    @Test
    public void testSaveAll() {
        Person person1 = new Person(null, "Alice", "Smith", 25, LocalDate.of(1998, 5, 15), "DNI", "87654321", "Child",
                "No", null, null);
        Person person2 = new Person(null, "Bob", "Brown", 40, LocalDate.of(1983, 6, 20), "DNI", "23456789", "Parent",
                "Yes", null, null);

        when(personRepository.save(any(Person.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        // Cambia el tipo de retorno a Mono<List<Person>>
        Mono<List<Person>> result = personService.saveAll(Flux.just(person1, person2)).collectList();

        // Bloquea el Mono para obtener la lista
        List<Person> personList = result.block();

        assertEquals(2, personList.size());
        verify(personRepository, times(2)).save(any(Person.class));
    }

    @Test
    public void testUpdate_PersonExists() {
        Person existingPerson = new Person(1, "John", "Doe", 30, LocalDate.of(1993, 1, 1), "DNI", "12345678", "Sibling",
                "Yes", "A", null);
        Person updatedPerson = new Person(null, "Jane", "Doe", 28, LocalDate.of(1995, 2, 2), "DNI", "12345679",
                "Sibling", "Yes", null, null);

        when(personRepository.findById(1)).thenReturn(Mono.just(existingPerson));
        when(personRepository.save(any(Person.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        Mono<Person> result = personService.update(1, updatedPerson);

        assertEquals("Jane", result.block().getName());
        verify(personRepository).findById(1);
        verify(personRepository).save(any(Person.class));
    }

    @Test
    public void testLogicalDelete_PersonExists() {
        Person existingPerson = new Person(1, "John", "Doe", 30, LocalDate.of(1993, 1, 1), "DNI", "12345678", "Sibling",
                "Yes", "A", null);

        when(personRepository.findById(1)).thenReturn(Mono.just(existingPerson));
        when(personRepository.save(any(Person.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        Mono<Person> result = personService.logicalDelete(1);

        assertEquals("I", result.block().getState());
        verify(personRepository).findById(1);
        verify(personRepository).save(any(Person.class));
    } */
}
