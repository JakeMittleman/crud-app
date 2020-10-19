package com.aquent.crudapp.service.client;

import com.aquent.crudapp.dto.Client;
import com.aquent.crudapp.dao.client.ClientDao;
import com.aquent.crudapp.dto.Person;
import com.aquent.crudapp.dao.person.PersonDao;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Default implementation of {@link ClientService}.
 */
@Component
public class DefaultClientService implements ClientService {

    private final ClientDao clientDao;
    private final PersonDao personDao;
    private final Validator validator;

    public DefaultClientService(ClientDao clientDao, PersonDao personDao, Validator validator) {
        this.clientDao = clientDao;
        this.personDao = personDao;
        this.validator = validator;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Client> listClients() {
        return clientDao.listClients();
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Person> listContacts(String clientName) {
        return personDao.listPeople(clientName);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Person> listContacts(Integer clientId) {
        return personDao.listPeople(clientId);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Client readClient(Integer id) {
        return clientDao.readClient(id);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Client readClient(String clientName) {
        return clientDao.readClient(clientName);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = false)
    public Integer createClient(Client client) {
        return clientDao.createClient(client);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = false)
    public void updateClient(Client client, List<String> newContactList) {
        System.out.println(client.getContacts());
        if (newContactList != null) {
            List<Person> newContacts = personDao.listPeople()
                    .stream()
                    .filter(person -> newContactList.contains(person.getFirstName() + ' ' + person.getLastName()))
                    .collect(Collectors.toList());
            client.setContacts(newContacts);
            clientDao.updateClient(client);
        }
        List<Person> allPeople = personDao.listPeople();
        for (Person person : allPeople) {
            if (client.getContacts().contains(person)) {
                if (person.getClient() == null || !client.getClientId().equals(person.getClient().getClientId())) {
                    person.setClient(client);
                    personDao.updatePerson(person);
                }
            } else {
                if (person.getClient() != null && person.getClient().getClientId().equals(client.getClientId())) {
                    person.setClient(null);
                    personDao.updatePerson(person);
                }
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = false)
    public void updateClient(String clientId, String contactId) {
        Client client = clientDao.readClient(Integer.parseInt(clientId));
        System.out.println("client before edits: " + client);
        client.setContacts(client.getContacts()
                .stream()
                .filter(contact -> !contact.getPersonId().equals(Integer.parseInt(contactId)))
                .collect(Collectors.toList())
        );
        Person person = personDao.readPerson(Integer.parseInt(contactId));
        person.setClient(null);
        System.out.println("client after edits: " + client);
        personDao.updatePerson(person);
        clientDao.updateClient(client);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = false)
    public void deleteClient(Integer id) {
        clientDao.deleteClient(id);
    }

    @Override
    public List<String> validateClient(Client client) {
        Set<ConstraintViolation<Client>> violations = validator.validate(client);
        List<String> errors = new ArrayList<String>(violations.size());
        for (ConstraintViolation<Client> violation : violations) {
            errors.add(violation.getMessage());
        }
        Collections.sort(errors);
        return errors;
    }
}
