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
        Client client = clientDao.readClient(id);
        client.setContacts(listContacts(id));
        return client;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Client readClient(String clientName) {
        Client client = clientDao.readClient(clientName);
        client.setContacts(listContacts(clientName));
        return client;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = false)
    public Integer createClient(Client client) {
        return clientDao.createClient(client);
    }

    /**
     * This method is for changing the contacts via the edit page for the given client.
     * @param client the edited client
     * @param newContactList the new list of contacts for this client by name
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = false)
    public void updateClient(Client client, List<String> newContactList) {
        if (newContactList != null) {
            List<Person> newContacts = personDao.listPeople()
                    .stream()
                    .filter(person -> newContactList.contains(person.getFirstName() + ' ' + person.getLastName()))
                    .collect(Collectors.toList());
            client.setContacts(newContacts);
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
        clientDao.updateClient(client);
    }

    /**
     * This method is for removing contacts on the single client view page.
     * @param clientId the id of the client to update
     * @param contactId the id of the contact to remove
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = false)
    public void updateClient(String clientId, String contactId) {
        Client client = clientDao.readClient(Integer.parseInt(clientId));
        client.setContacts(client.getContacts()
                .stream()
                .filter(contact -> !contact.getPersonId().equals(Integer.parseInt(contactId)))
                .collect(Collectors.toList())
        );
        Person person = personDao.readPerson(Integer.parseInt(contactId));
        person.setClient(null);
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
