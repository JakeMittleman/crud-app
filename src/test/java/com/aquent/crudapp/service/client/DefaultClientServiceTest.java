package com.aquent.crudapp.service.client;

import com.aquent.crudapp.dto.Client;
import com.aquent.crudapp.dto.Person;
import com.aquent.crudapp.service.person.DefaultPersonService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DefaultClientServiceTest {

    @Autowired
    private DefaultClientService defaultClientService;
    @Autowired
    private DefaultPersonService defaultPersonService;
    private Client newClient;

    @BeforeEach
    public void setUp() {
        newClient = Client.builder()
                .clientName("Broadway")
                .websiteUri("http://broadway.com")
                .phoneNumber("555-555-5555")
                .streetAddress("1 main st.")
                .city("Manhattan")
                .state("NY")
                .zipCode("15342")
                .contacts(new ArrayList<>())
                .build();

        int id = defaultClientService.createClient(newClient);
        newClient.setClientId(id);
    }

    @AfterEach
    public void tearDown() {
        defaultClientService.deleteClient(newClient.getClientId());
    }

    @Test
    void testListClients() {
        List<Client> prevClients = defaultClientService.listClients();
        int prevSize = prevClients.size();
        Client tempClient = Client.builder()
                .clientName("TempName")
                .websiteUri("http://fakesite.com")
                .phoneNumber("555-555-5555")
                .streetAddress("1 main st.")
                .city("Manhattan")
                .state("NY")
                .contacts(new ArrayList<>())
                .build();
        int id = defaultClientService.createClient(tempClient);
        assertEquals(prevSize+1, defaultClientService.listClients().size());
        defaultClientService.deleteClient(id);
    }

    @Test
    void testListContactsByName() {
        Person jenna = Person.builder()
                .firstName("Jenna")
                .lastName("Roland")
                .emailAddress("rolsroyce@gmail.com")
                .streetAddress("25 None Rd.")
                .city("Red Bank")
                .state("NJ")
                .zipCode("98765")
                .client(newClient)
                .build();

        Person michael = Person.builder()
                .firstName("Michael")
                .lastName("Mell")
                .emailAddress("mike@gmail.com")
                .streetAddress("25 Bathroom Rd.")
                .city("Red Bank")
                .state("NJ")
                .zipCode("98765")
                .client(newClient)
                .build();

        int jennaId = defaultPersonService.createPerson(jenna);
        jenna.setPersonId(jennaId);
        int michaelId = defaultPersonService.createPerson(michael);
        michael.setPersonId(michaelId);
        List<Person> contacts = defaultClientService.listContacts(newClient.getClientName());
        assertEquals(Arrays.asList(jenna, michael), contacts);
        defaultPersonService.deletePerson(jennaId);
        defaultPersonService.deletePerson(michaelId);
    }

    @Test
    void testListContactsById() {
        Person jenna = Person.builder()
                .firstName("Jenna")
                .lastName("Roland")
                .emailAddress("rolsroyce@gmail.com")
                .streetAddress("25 None Rd.")
                .city("Red Bank")
                .state("NJ")
                .zipCode("98765")
                .client(newClient)
                .build();

        Person michael = Person.builder()
                .firstName("Michael")
                .lastName("Mell")
                .emailAddress("mike@gmail.com")
                .streetAddress("25 Bathroom Rd.")
                .city("Red Bank")
                .state("NJ")
                .zipCode("98765")
                .client(newClient)
                .build();

        int jennaId = defaultPersonService.createPerson(jenna);
        jenna.setPersonId(jennaId);
        int michaelId = defaultPersonService.createPerson(michael);
        michael.setPersonId(michaelId);
        List<Person> contacts = defaultClientService.listContacts(newClient.getClientId());
        assertEquals(Arrays.asList(jenna, michael), contacts);
        defaultPersonService.deletePerson(jennaId);
        defaultPersonService.deletePerson(michaelId);
    }

    @Test
    void readClient() {
    }

    @Test
    void testReadClient() {
    }

    @Test
    void createClient() {
    }

    @Test
    void updateClient() {
    }

    @Test
    void testUpdateClient() {
    }

    @Test
    void deleteClient() {
    }

    @Test
    void validateClient() {
    }
}